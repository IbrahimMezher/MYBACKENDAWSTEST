package com.flutterbackend.security.ratelimit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    @Value("${ratelimit.enabled:true}")
    private boolean enabled;

    @Value("${ratelimit.ip.max-attempts:10}")
    private int ipMax;
    @Value("${ratelimit.ip.window-seconds:60}")
    private long ipWindowSec;

    @Value("${ratelimit.account.max-attempts:5}")
    private int accountMax;
    @Value("${ratelimit.account.window-seconds:300}")
    private long accountWindowSec;
    @Value("${ratelimit.account.lock-seconds:300}")
    private long accountLockSec;

    private final Map<String, Window> ipWindows = new ConcurrentHashMap<>();
    private final Map<String, Window> accountWindows = new ConcurrentHashMap<>();
    private final Map<String, Long> accountLockedUntil = new ConcurrentHashMap<>();

    private static final class Window {
        volatile long start;
        final AtomicInteger count = new AtomicInteger(0);
        Window(long start) { this.start = start; }
    }

    public void checkIp(String ip, String bucket) {
        if (!enabled || ip == null) return;
        String key = bucket + "|" + ip;
        long retry = hit(ipWindows, key, ipMax, ipWindowSec);
        if (retry > 0) {
            throw new RateLimitException(
                    "Too many attempts. Please try again in " + retry + " seconds.",
                    retry);
        }
    }

    public void assertAccountNotLocked(String account) {
        if (!enabled || account == null) return;
        String key = account.toLowerCase().trim();
        Long until = accountLockedUntil.get(key);
        if (until != null) {
            long now = System.currentTimeMillis();
            if (now < until) {
                long retry = (until - now) / 1000 + 1;
                throw new RateLimitException(
                        "Account temporarily locked due to repeated attempts. "
                                + "Try again in " + retry + " seconds.", retry);
            }
            accountLockedUntil.remove(key);
        }
    }

    public boolean recordAccountFailure(String account) {
        if (!enabled || account == null) return false;
        String key = account.toLowerCase().trim();
        long retry = hit(accountWindows, key, accountMax, accountWindowSec);
        if (retry > 0) {
            accountLockedUntil.put(key,
                    System.currentTimeMillis() + accountLockSec * 1000);
            accountWindows.remove(key);
            return true;
        }
        return false;
    }

    public void recordAccountSuccess(String account) {
        if (account == null) return;
        String key = account.toLowerCase().trim();
        accountWindows.remove(key);
        accountLockedUntil.remove(key);
    }

    private long hit(Map<String, Window> store, String key, int max, long windowSec) {
        long now = System.currentTimeMillis();
        Window w = store.compute(key, (k, existing) -> {
            if (existing == null || now - existing.start >= windowSec * 1000) {
                return new Window(now);
            }
            return existing;
        });
        int c = w.count.incrementAndGet();
        if (c > max) {
            long resetIn = (w.start + windowSec * 1000 - now) / 1000 + 1;
            return Math.max(resetIn, 1);
        }
        return 0;
    }
}
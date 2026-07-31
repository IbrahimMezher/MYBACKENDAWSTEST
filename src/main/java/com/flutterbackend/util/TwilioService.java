package com.flutterbackend.util;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.verify.service.sid}")
    private String serviceSid;

    public void sendOtp(String phoneNumber) {
        ensureConfigured();
        try {
            Twilio.init(accountSid, authToken);
            Verification.creator(serviceSid, phoneNumber, "sms").create();
        } catch (ApiException ex) {
            throw new RuntimeException(twilioMessage(ex));
        }
    }

    public boolean verifyOtp(String phoneNumber, String code) {
        ensureConfigured();
        try {
            Twilio.init(accountSid, authToken);
            VerificationCheck check = VerificationCheck.creator(serviceSid)
                    .setTo(phoneNumber)
                    .setCode(code)
                    .create();
            return "approved".equals(check.getStatus());
        } catch (ApiException ex) {
            throw new RuntimeException(twilioMessage(ex));
        }
    }

    private void ensureConfigured() {
        if (isBlank(accountSid) || !accountSid.startsWith("AC")) {
            throw new RuntimeException("Twilio Account SID is missing or invalid. It must start with AC.");
        }
        if (isBlank(authToken)) {
            throw new RuntimeException("Twilio Auth Token is missing.");
        }
        if (isBlank(serviceSid) || !serviceSid.startsWith("VA")) {
            throw new RuntimeException("Twilio Verify Service SID is missing or invalid. Copy the Verify Service SID that starts with VA.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String twilioMessage(ApiException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains("/v2/Services/")) {
            return "Twilio Verify service was not found. Check TWILIO_VERIFY_SID in secrets.properties; it must be the Verify Service SID starting with VA.";
        }
        return message != null ? message : "Twilio verification failed.";
    }
}

package com.flutterbackend.broker.web;

import com.flutterbackend.broker.dto.BrokerDetails;
import com.flutterbackend.broker.dto.BrokerResponse;
import com.flutterbackend.broker.dto.BrokerSignupRequest;
import com.flutterbackend.broker.service.BrokerService;
import com.flutterbackend.user.dto.UserLoginResponse;
import com.flutterbackend.util.CurrentUser;
import com.flutterbackend.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/brokers")
@CrossOrigin(origins = "*")
public class BrokerController {

    private final BrokerService brokerService;
    private final CurrentUser currentUser;

    public BrokerController(BrokerService brokerService,
                            CurrentUser currentUser) {
        this.brokerService = brokerService;
        this.currentUser = currentUser;
    }

    @PostMapping("/signup")
    public UserLoginResponse signup(@RequestBody BrokerSignupRequest body) {

        return brokerService.signup(body);
    }

    @GetMapping("/brokerId")
    public Long brokerId(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return brokerService.getBrokerByUserId(user.getUserId()).getBrokerId();
    }

    @GetMapping("/brokerinfo")
    public Object brokerInfo(HttpServletRequest request) {
        User user = currentUser.getCurrentUser(request);
        return brokerService.getBrokerByUserId(user.getUserId());
    }

    @GetMapping("/broker/{brokerid}")
    public BrokerDetails brokerdetails(@PathVariable Long brokerid) {
        return brokerService.getBrokerByBrokerId(brokerid).build();
    }

    @GetMapping("/public")
    public java.util.List<BrokerResponse> publicBrokers() {
        return brokerService.getActiveBrokerDirectory();
    }
}

package com.flutterbackend.account.service;

import com.flutterbackend.user.domain.User;

public interface AccountService {

    String requestActionCode(User user, String action);

    String confirmAction(User user, String code);
}

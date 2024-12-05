package com.donghaeng.withme.screen;

public class ScreenList {
    public interface ACTIVITY {
        String START = "com.donghaeng.withme.screen.start.StartActivity";
    }
    public interface FRAGMENT {
        String LOGIN = "LoginFragment";
        String SIGNUP_NAME = "SignupNameFragment";
        String SIGNUP_PHONE = "SignupVerifyingPhoneNumberFragment";
        String SIGNUP_PASSWORD = "SignupPassWordFragment";
        String SELECT = "SelectFragment";
        String CONTROLLER_QR = "controller_QR";
        String TARGET_QR = "target_QR";
    }
}

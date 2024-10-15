package com.donghaeng.withme.login;

// 본인인증
public class IdentityVerification {
    /*
    전화번호 로그인을 시작하려면 사용자에게 전화번호 입력을 요청하는 인터페이스를 제시합니다.
    지역에 따라 현지 법규가 다르지만 일반적으로는 사용자가 제반 상황을 미리 알 수 있도록 전화 로그인을 사용하면
    인증용 SMS 메시지가 발송되고 일반 요금이 부과될 수 있다는 점을 알려야 합니다.
     */
    public static void authenticatePhoneNumber(String phoneNumber) {
        PhoneAuthActivity phoneAuthActivity = new PhoneAuthActivity();
    }

}

package com.donghaeng.withme.login.connect;

import java.util.HashMap;
import java.util.Map;

public class LocalConfirmationStatus {
    private static final Map<String, Boolean> confirmationStatus = new HashMap<>();

    public static void updateStatus(String userId, boolean status) {
        confirmationStatus.put(userId, status);
    }
    public static boolean isConfirmed(String userId) {
        Boolean status = confirmationStatus.get(userId);
        return status != null ? status : false;
    }

    public static void clear() {
        confirmationStatus.clear();
    }
}

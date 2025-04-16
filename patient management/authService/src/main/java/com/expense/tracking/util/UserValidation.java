package com.expense.tracking.util;

import com.expense.tracking.model.UserInfoDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidation {
    public static boolean validateUserCredentials(UserInfoDTO userInfo) {
        if (userInfo == null || userInfo.getPassword() == null || userInfo.getEmail() == null) {
            return false;
        }

        String regPasswordExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$";
        String regEmailExpn = "^(.+)@(\\S+)$";

        Pattern pattern = Pattern.compile(regPasswordExpn, Pattern.CASE_INSENSITIVE);
        Matcher passwordMatcher = pattern.matcher(userInfo.getPassword());

        Pattern emailPattern = Pattern.compile(regEmailExpn);
        Matcher emailMatcher = emailPattern.matcher(userInfo.getEmail());

        return passwordMatcher.matches() && emailMatcher.matches();
    }

}

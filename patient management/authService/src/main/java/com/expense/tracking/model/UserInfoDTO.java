package com.expense.tracking.model;

import lombok.Data;

@Data
public class UserInfoDTO {

    private String userName;
    private String lastName;
    private String password;
    private Long phoneNumber;
    private String email;
}

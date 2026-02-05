package com.sup.event_management.dto.request;

import lombok.Data;

@Data
public class UserCreateDTO {
    private String name;
    private String email;
    private String password;
}

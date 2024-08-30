package com.jmt.mypage.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestProfile {
    @NotBlank
    private String userName;

    private String password;

    private String confirmPassword;

    private String mobile;
}

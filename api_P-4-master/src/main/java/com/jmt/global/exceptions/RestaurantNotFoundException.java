package com.jmt.global.exceptions;

import org.springframework.http.HttpStatus;

public class RestaurantNotFoundException extends CommonException{
    public RestaurantNotFoundException() {
        super("NotFound.restaurant", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}

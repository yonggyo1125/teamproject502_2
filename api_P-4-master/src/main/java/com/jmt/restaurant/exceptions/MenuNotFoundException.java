package com.jmt.restaurant.exceptions;

import com.jmt.global.exceptions.CommonException;
import org.springframework.http.HttpStatus;

public class MenuNotFoundException extends CommonException {

    public MenuNotFoundException() {
        super("NotFound.Menu", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}

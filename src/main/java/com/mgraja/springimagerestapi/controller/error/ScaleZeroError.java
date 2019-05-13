package com.mgraja.springimagerestapi.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ScaleZeroError extends RuntimeException {
    public ScaleZeroError(){
        super("Scale can't be equal to zero");
    }
}

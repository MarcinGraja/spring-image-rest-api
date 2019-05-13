package com.mgraja.springimagerestapi.controller.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ImageNotFound extends RuntimeException{

    public ImageNotFound(String id) {
        super("image with id " + id + " not found");
    }
}

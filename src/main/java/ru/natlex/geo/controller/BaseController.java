package ru.natlex.geo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;


public abstract class BaseController {
    protected long parseId(String s) throws HttpClientErrorException.BadRequest {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error reading id");
        }
    }

}

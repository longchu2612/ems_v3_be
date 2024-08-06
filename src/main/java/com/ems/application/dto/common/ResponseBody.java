package com.ems.application.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseBody {
    private String message;
    private int status;

    public ResponseBody(String message, int status) {
        this.message = message;
        this.status = status;
    }
}

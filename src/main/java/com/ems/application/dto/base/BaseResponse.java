package com.ems.application.dto.base;

import java.util.HashMap;
import java.util.List;

import com.ems.application.enums.ErrorMessageType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse {

    protected ErrorMessageType type;
    protected String title;
    protected Integer status;
    protected HashMap<String, List<Object>> messages;
}

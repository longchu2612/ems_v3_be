package com.ems.application.dto.table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewTableRequest {

    private String name;
    private String position;
    private Integer status;
}

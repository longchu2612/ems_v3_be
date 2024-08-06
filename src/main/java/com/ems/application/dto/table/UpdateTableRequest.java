package com.ems.application.dto.table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTableRequest {
    private String id;
    private String name;
    private String position;
    private Boolean status;
}

package com.ems.application.dto.file;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentRequest {

    private Integer documentType;
    private String documentName;
}

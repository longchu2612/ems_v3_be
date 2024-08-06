package com.ems.application.dto.category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatesCategoryRequest {
    private String id;
    private String categoryName;
    private Boolean status;
}

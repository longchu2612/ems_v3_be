package com.ems.application.dto.eatery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewEateryRequest {

    private String name;
    private String address;
    private String taxNo;
    private String phone;
}

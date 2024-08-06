package com.ems.application.mapping.eatery;

import com.ems.application.dto.eatery.EateryResponse;
import com.ems.application.dto.eatery.NewEateryRequest;
import com.ems.application.entity.Eatery;
import com.ems.application.util.HashIdsUtils;

public class EateryMapping {
    public static Eatery convertToEntity(NewEateryRequest eateryRequest, Eatery dtbEatery) {
        dtbEatery.setName(eateryRequest.getName());
        dtbEatery.setAddress(eateryRequest.getAddress());
        dtbEatery.setTaxNo(eateryRequest.getTaxNo());
        dtbEatery.setPhone(eateryRequest.getPhone());
        return dtbEatery;
    }

    public static EateryResponse convertToDto(Eatery dtbEatery, HashIdsUtils hashIdsUtils) {
        EateryResponse response = new EateryResponse();
        response.setId(hashIdsUtils.encodeId(dtbEatery.getId()));
        response.setName(dtbEatery.getName());
        response.setAddress(dtbEatery.getAddress());
        response.setTaxNo(dtbEatery.getTaxNo());
        response.setPhone(dtbEatery.getPhone());
        return response;
    }

}

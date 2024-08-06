package com.ems.application.controller.eatery;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ems.application.dto.eatery.EateryResponse;
import com.ems.application.dto.eatery.NewEateryRequest;
import com.ems.application.service.eatery.EateryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Eatery")

@RestController
@RequestMapping(value = "/api/eateries")
public class EateryController {
    private final EateryService eateryService;

    public EateryController(EateryService eateryService) {
        this.eateryService = eateryService;
    }

    @ApiOperation(value = "Get eatery by id")
    @GetMapping(value = "/detail/{id}")
    public ResponseEntity<EateryResponse> getEatery(@PathVariable("id") String id) {
        return eateryService.getEateryById(id);
    }

    @ApiOperation(value = "Update eatery")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<EateryResponse> updateEatery(@PathVariable("id") String id,
            @Valid @RequestBody NewEateryRequest eateryRequest) {
        return eateryService.updateEatery(id, eateryRequest);
    }

}

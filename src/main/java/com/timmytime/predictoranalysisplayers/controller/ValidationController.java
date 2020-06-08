package com.timmytime.predictoranalysisplayers.controller;

import com.timmytime.predictoranalysisplayers.service.ValidationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Api(value = "Validation", description = "API ...")
@RequestMapping("/api/prediction/players/validation")
public class ValidationController {

    @Autowired
    private ValidationService validationService;

    @RequestMapping(
            value = "",
            method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void validate(){
        validationService.validate(UUID.randomUUID(), Boolean.FALSE);
    }

}

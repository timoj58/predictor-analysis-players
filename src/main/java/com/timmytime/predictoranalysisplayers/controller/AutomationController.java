package com.timmytime.predictoranalysisplayers.controller;


import com.timmytime.predictoranalysisplayers.service.AutomationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@Api(value = "Automation", description = "API to handle automation (receipts and any manual intervention)")
@RequestMapping("/api/prediction/players/automation")
public class AutomationController {

    @Autowired
    private AutomationService automationService;

    @RequestMapping(
            value = "start",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void start(
    ) {
        CompletableFuture.runAsync(() -> automationService.start());
        return;
    }


}

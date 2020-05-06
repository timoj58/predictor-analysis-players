package com.timmytime.predictoranalysisplayers.service.impl;

import com.timmytime.predictoranalysisplayers.service.AutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("automationService")
public class AutomationServiceImpl implements AutomationService {

    private static final Logger log = LoggerFactory.getLogger(AutomationServiceImpl.class);

    @Autowired
    public AutomationServiceImpl(
    ) {
    }

    @Override
    public void start() {

    }

}

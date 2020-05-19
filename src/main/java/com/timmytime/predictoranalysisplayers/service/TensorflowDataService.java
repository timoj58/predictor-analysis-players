package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;

import java.util.List;
import java.util.UUID;

public interface TensorflowDataService {
    List<PlayerEventOutcomeCsv> getPlayerCsv(String fromDate, String toDate);
}

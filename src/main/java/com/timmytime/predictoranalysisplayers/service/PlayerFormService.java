package com.timmytime.predictoranalysisplayers.service;

import com.timmytime.predictoranalysisplayers.response.data.Player;


public interface PlayerFormService {

    void load(Player player);

    void clear();
}

package com.timmytime.predictoranalysisplayers.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.timmytime.predictoranalysisplayers.response.PlayerEventOutcomeCsv;
import com.timmytime.predictoranalysisplayers.service.TensorflowDataService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prediction/players/ml-data")
public class TensorflowDataController {

    private static final Logger log = LoggerFactory.getLogger(TensorflowDataController.class);

    @Autowired
    private TensorflowDataService tensorflowDataService;


    @RequestMapping(
            value = "/player/{player}/{from-date}/{to-date}",
            method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_AUTOMATION')")
    public void getCountryData(HttpServletResponse response,
                               @PathVariable("player") UUID player,
                               @PathVariable("from-date") String fromDate,
                               @PathVariable("to-date") String toDate) {

        List<PlayerEventOutcomeCsv> competitionEventOutcomeCsvs = tensorflowDataService.getPlayerCsv(player, fromDate, toDate);
        response.setContentType("text/plain"); //probably wrong to look up correct ie text/csv
        //  response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + filename + "\""));
        if (!competitionEventOutcomeCsvs.isEmpty()) {

            StringBuilder stringBuilder = new StringBuilder();

            competitionEventOutcomeCsvs.stream().map(m -> m.toString()).forEach(
                    f -> stringBuilder.append(f).append("\n")
            );

            response.setContentLength(stringBuilder.toString().getBytes().length);

            try {
                response.getOutputStream().write(stringBuilder.toString().getBytes());
            } catch (IOException ioe) {
                log.error("csv", ioe);
            }

        }

        response.setStatus(HttpStatus.OK.value());
    }

}

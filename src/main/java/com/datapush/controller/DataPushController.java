package com.datapush.controller;

import com.datapush.dto.PushResultDTO;
import com.datapush.service.DataPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/push")
public class DataPushController {

    private static final Logger log = LoggerFactory.getLogger(DataPushController.class);

    @Autowired
    private DataPushService dataPushService;

    @PostMapping("/lingma-usage")
    public PushResultDTO pushLingmaUsage(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {

        log.info("Received push request: start={}, end={}", start, end);

        return dataPushService.pushData(start, end);
    }
}

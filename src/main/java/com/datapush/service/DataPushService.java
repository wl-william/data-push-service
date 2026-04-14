package com.datapush.service;

import com.alibaba.fastjson.JSON;
import com.datapush.config.PushConfig;
import com.datapush.dto.LingmaUsageDTO;
import com.datapush.dto.PushResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DataPushService {

    private static final Logger log = LoggerFactory.getLogger(DataPushService.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PushConfig pushConfig;

    public PushResultDTO pushData(String start, String end) {
        PushResultDTO result = new PushResultDTO();

        String sql = pushConfig.getQuery().getSql()
                .replace("${start}", start)
                .replace("${end}", end);

        log.info("Executing query with start={}, end={}", start, end);

        List<LingmaUsageDTO> dataList = jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(LingmaUsageDTO.class)
        );

        log.info("Queried {} records from database", dataList.size());

        result.setTotal(dataList.size());

        String apiUrl = pushConfig.getApi().getUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < dataList.size(); i++) {
            LingmaUsageDTO data = dataList.get(i);
            try {
                String jsonBody = JSON.toJSONString(data);
                log.info("Pushing record {}/{}: {}", i + 1, dataList.size(), jsonBody);

                HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    successCount++;
                    log.info("Push succeeded for user={}, week={}, response={}",
                            data.getUser_name(), data.getWeek_day(), response.getBody());
                } else {
                    failCount++;
                    log.warn("Push failed for user={}, week={}, status={}, response={}",
                            data.getUser_name(), data.getWeek_day(), response.getStatusCode(), response.getBody());
                }
            } catch (Exception e) {
                failCount++;
                log.error("Push error for user={}, week={}: {}",
                        data.getUser_name(), data.getWeek_day(), e.getMessage(), e);
            }
        }

        result.setSuccess(successCount);
        result.setFailed(failCount);
        result.setMessage(String.format("Push completed: total=%d, success=%d, failed=%d",
                dataList.size(), successCount, failCount));

        log.info(result.getMessage());

        return result;
    }
}

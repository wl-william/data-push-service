package com.datapush.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "push")
public class PushConfig {

    private ApiConfig api = new ApiConfig();
    private QueryConfig query = new QueryConfig();

    @Data
    public static class ApiConfig {
        private String url;
        private int connectTimeout = 5000;
        private int readTimeout = 10000;
    }

    @Data
    public static class QueryConfig {
        private String sql;
    }
}

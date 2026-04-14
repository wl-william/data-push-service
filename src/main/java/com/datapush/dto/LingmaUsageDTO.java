package com.datapush.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class LingmaUsageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String week_day;

    private String dept_name;

    private String user_name;

    @JSONField(name = "chatturnsaccepted")
    private Integer chatturnsaccepted;

    @JSONField(name = "lines_accepted")
    private Integer lines_accepted;

    @JSONField(name = "lines_suggested")
    private Integer lines_suggested;

    @JSONField(name = "total_chat_turns")
    private Integer total_chat_turns;

    @JSONField(name = "count_accepted")
    private Integer count_accepted;

    @JSONField(name = "count_suggested")
    private Integer count_suggested;

    @JSONField(name = "accept_rate")
    private Double accept_rate;

    @JSONField(name = "total_lines_changed")
    private Integer total_lines_changed;

    @JSONField(name = "total_lines_accepted")
    private Integer total_lines_accepted;

    @JSONField(name = "generate_rate")
    private Double generate_rate;
}

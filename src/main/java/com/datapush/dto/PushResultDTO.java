package com.datapush.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PushResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int total;

    private int success;

    private int failed;

    private String message;
}

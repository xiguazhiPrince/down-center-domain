package com.watermelon.domain.task.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorMsg {

    private String errorMsg;

    private LocalDateTime time;

    public ErrorMsg(String errorMsg, LocalDateTime time) {
        this.errorMsg = errorMsg;
        this.time = time;
    }
}

package io.omengye.userinfo.entity;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class UserInfo {

    private String visitTime;

    private String ip;

    private Integer count = 0;

    public UserInfo(String visitTime, String ip, Integer count) {
        this.visitTime = visitTime;
        this.ip = ip;
        this.count = count;
    }
}

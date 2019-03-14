package io.omengye.userinfo.entity;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class UserInfo {

    private String visittime;

    private String ip;

    private Integer count = 0;

    public UserInfo(String visittime, String ip, Integer count) {
        this.visittime = visittime;
        this.ip = ip;
        this.count = count;
    }
}

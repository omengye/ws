package io.omengye.userinfo.entity;

import lombok.Data;

@Data
public class UserInfoEntity {

    private String visitTime;

    private String ip;

    private Integer count = 0;

    public UserInfoEntity(String visitTime, String ip, Integer count) {
        this.visitTime = visitTime;
        this.ip = ip;
        this.count = count;
    }
}

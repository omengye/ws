package io.omengye.gcs.entity;

import lombok.Data;

@Data
public class SearchEntity {

    private String q;
    private String start;
    private String num;
    private String sort;

    public SearchEntity(String q, String start, String num) {
        this.q = q;
        this.start = start;
        this.num = num;
    }

}

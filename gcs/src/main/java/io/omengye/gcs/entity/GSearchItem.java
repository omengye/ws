package io.omengye.gcs.entity;

import lombok.Data;

import java.util.Map;

@Data
public class GSearchItem {

    private int index = 0;

    private String key;

    private String cx;

    public GSearchItem(String key, String cx) {
        this.key = key;
        this.cx = cx;
    }

    public GSearchItem(Map<String, String> item) {
        this.key = item.get("key");
        this.cx = item.get("cx");
    }

}

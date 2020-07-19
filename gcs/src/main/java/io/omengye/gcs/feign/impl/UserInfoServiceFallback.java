package io.omengye.gcs.feign.impl;

import io.omengye.gcs.feign.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
public class UserInfoServiceFallback implements UserInfoService {

    @Override
    public Map<String, Boolean> addVisitCount(String userIp) {
        log.info("addVisitCount fallback");
        return Collections.emptyMap();
    }
}

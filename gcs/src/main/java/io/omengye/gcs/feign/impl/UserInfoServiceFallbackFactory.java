package io.omengye.gcs.feign.impl;

import feign.hystrix.FallbackFactory;
import io.omengye.gcs.feign.UserInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserInfoServiceFallbackFactory implements FallbackFactory<UserInfoService> {

    @Resource
    private UserInfoServiceFallback userInfoServiceFallback;

    @Override
    public UserInfoService create(Throwable throwable) {
        return userInfoServiceFallback;
    }
}

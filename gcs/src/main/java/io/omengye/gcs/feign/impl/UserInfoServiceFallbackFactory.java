package io.omengye.gcs.feign.impl;

import io.omengye.gcs.feign.UserInfoFeignService;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class UserInfoServiceFallbackFactory implements FallbackFactory<UserInfoFeignService> {

    @Resource
    private UserInfoServiceFallback userInfoServiceFallback;

    @Override
    public UserInfoFeignService create(Throwable throwable) {
        return userInfoServiceFallback;
    }
}

package io.omengye.gcs.feign;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.omengye.gcs.feign.impl.UserInfoServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value="WS-USERINFO", fallbackFactory = UserInfoServiceFallbackFactory.class)
public interface UserInfoService  {

    @GetMapping("/visit")
    Map<String, Boolean> addVisitCount(@RequestParam("userIp") String userIp);

}

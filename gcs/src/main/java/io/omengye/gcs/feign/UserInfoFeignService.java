package io.omengye.gcs.feign;


import io.omengye.gcs.feign.impl.UserInfoServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "ws-userinfo", url="${userinfo.user.url}", fallback = UserInfoServiceFallback.class)
public interface UserInfoFeignService  {

    @GetMapping("/visit")
    Map<String, Boolean> addVisitCount(@RequestParam("userIp") String userIp);

}

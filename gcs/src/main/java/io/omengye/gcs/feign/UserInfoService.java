package io.omengye.gcs.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value="WS-USERINFO")
public interface UserInfoService  {

    @GetMapping("/visit")
    Map<String, Boolean> addVisitCount(@RequestParam String userIp);

}

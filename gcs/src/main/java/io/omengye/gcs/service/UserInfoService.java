package io.omengye.gcs.service;


import io.omengye.gcs.configure.UserServiceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

//@FeignClient(value="WS-USERINFO", configuration = UserServiceFeignConfig.class)
@FeignClient(value="WS-USERINFO")
public interface UserInfoService  {

    @GetMapping("/visit")
    public Map<String, Boolean> addVisitCount(@RequestParam String userip, @RequestParam(required = false) String user,
                                              @RequestParam(required = false) String password);

}

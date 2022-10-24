package io.omengye.gcs.service;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class StratRunner implements CommandLineRunner {

    @Resource
    private ChooseItemService chooseItemService;

    @Override
    public void run(String... args) throws Exception {
        chooseItemService.init();
    }
}

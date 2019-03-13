package io.omengye.gcs.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StratRunner implements CommandLineRunner {

    @Autowired
    ChooseItemService chooseItemService;

    @Override
    public void run(String... args) throws Exception {
        chooseItemService.init();
    }
}

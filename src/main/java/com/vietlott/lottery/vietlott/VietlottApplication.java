package com.vietlott.lottery.vietlott;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VietlottApplication {

    public static void main(String[] args) {
        SpringApplication.run(VietlottApplication.class, args);
    }

}

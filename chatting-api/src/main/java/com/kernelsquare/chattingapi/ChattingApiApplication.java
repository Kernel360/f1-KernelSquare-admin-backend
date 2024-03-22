package com.kernelsquare.chattingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.kernelsquare.core",
    "com.kernelsquare.chattingapi",
    "com.kernelsquare.domainmysql",
    "com.kernelsquare.domainmongodb"})
public class ChattingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChattingApiApplication.class, args);
    }

}

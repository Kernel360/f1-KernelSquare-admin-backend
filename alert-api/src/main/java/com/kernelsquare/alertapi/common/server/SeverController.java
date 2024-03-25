package com.kernelsquare.alertapi.common.server;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeverController {
    @GetMapping("/environment")
    public String getEnv() {
        return "alert";
    }
}

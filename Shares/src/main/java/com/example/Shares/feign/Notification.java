package com.example.Shares.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service", url = "http://localhost:8081/api/setup")
public interface Notification {

    @PostMapping("/notification")
    public String registerToken(@RequestBody String requestBody);

    @PostMapping("/sendPaymentNotification")
    public void sendPaymentNotification(@RequestBody Map<String, Object> requestBody);

    @PostMapping("/sendFailureNotification")
    public void sendFailureNotification (@RequestBody Map<String, Object> requestBody);
}

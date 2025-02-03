package com.example.Shares.notification.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    private static final  String  EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    public void sendPushNotification(String expoPushToken,String message){
        if(!expoPushToken.startsWith("ExponentPushToken")){
            System.out.println("Invalid Expo Push Token");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, Object> body = new HashMap<>();
        body.put("to",expoPushToken);
        body.put("title","Gizmo Gate");
        body.put("body",message);
        body.put("sound","default");

        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(body,headers);
        String response = restTemplate.postForObject(EXPO_PUSH_URL,entity,String.class);
        System.out.println("Expo push Response: "+ response);
    }
}

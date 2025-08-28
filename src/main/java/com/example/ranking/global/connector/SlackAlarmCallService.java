package com.example.ranking.global.connector;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SlackAlarmCallService{

    private final WebClient.Builder webClientBuilder;

    public void sendSlackAlarmMessage(SlackAlarm slackAlarm){
        webClientBuilder
                .baseUrl("https://hooks.slack.com/services/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")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build()
                .post()
                .bodyValue(slackAlarm)
                .retrieve()
                .toEntity(String.class)
                .subscribe(); // 비동기 .block(); // 동기
    }

}

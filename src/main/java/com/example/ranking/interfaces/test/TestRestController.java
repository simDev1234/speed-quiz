package com.example.ranking.interfaces.test;

import com.example.ranking.global.connector.SlackAlarm;
import com.example.ranking.global.connector.SlackAlarmCallService;
import com.example.ranking.interfaces.test.request.TestApiLogUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestRestController {

    private final SlackAlarmCallService slackAlarmCallService;

    @GetMapping
    public ResponseEntity testGet(){
        log.info("=======> Test Start");
        slackAlarmCallService.sendSlackAlarmMessage(
                SlackAlarm.builder()
                        .username("[쿨스쿨] 테스트3")
                        .iconEmoji("\uD83D\uDC31")
                        .text("yoyoyoyo")
                        .build()
        );
        return ResponseEntity.ok("success");
    }

    @PutMapping
    public ResponseEntity test(@RequestBody TestApiLogUpdate testApiLogUpdate){
        log.info("=======> Test Start");
        return ResponseEntity.ok("success");
    }

}
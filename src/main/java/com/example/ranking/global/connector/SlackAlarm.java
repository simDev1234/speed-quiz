package com.example.ranking.global.connector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SlackAlarm(String username, @JsonProperty("icon_emoji") String iconEmoji, String text) {
}

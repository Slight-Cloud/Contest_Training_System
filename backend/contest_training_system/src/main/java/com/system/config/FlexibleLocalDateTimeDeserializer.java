package com.system.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 灵活的 LocalDateTime 反序列化器
 * 同时支持以下格式：
 * - yyyy-MM-dd HH:mm:ss （空格分隔）
 * - yyyy-MM-dd'T'HH:mm:ss （ISO-8601 带T分隔）
 * - yyyy-MM-dd'T'HH:mm:ss.SSS （带毫秒）
 */
public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText().trim();
        
        if (dateStr.isEmpty()) {
            return null;
        }

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDateTime.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // 尝试下一个格式
            }
        }

        throw new IOException("无法解析日期时间: " + dateStr + 
            "，支持的格式: yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd'T'HH:mm:ss");
    }
}

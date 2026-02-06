package com.neck.weatherpick.server.dto;

public record WeatherNowResponse(
        String regionLabel,
        double tempC,               // T1H
        double precipitation1hMm,   // RN1
        int humidity,               // REH
        double windSpeedMs,         // WSD
        String precipType,
        String skyType
) {}
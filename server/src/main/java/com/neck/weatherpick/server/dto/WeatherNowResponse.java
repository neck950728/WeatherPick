package com.neck.weatherpick.server.dto;

public record WeatherNowResponse(
        // String regionLabel,      // 사용자가 입력한 지역명
        String resolvedAddress,     // 실제 조회된 지역의 주소
        String resolvedPlaceName,   // 실제 조회된 지역의 장소명
        double tempC,               // T1H
        double precipitation1hMm,   // RN1
        int humidity,               // REH
        double windSpeedMs,         // WSD
        String precipType,
        String skyType
) {}
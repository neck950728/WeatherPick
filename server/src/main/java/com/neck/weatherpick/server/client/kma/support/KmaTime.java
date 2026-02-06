package com.neck.weatherpick.server.client.kma.support;

import com.neck.weatherpick.server.client.kma.KmaClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * - 가장 최근 or 직전의 baseDate(발표 일자) 및 baseTime(발표 시각) 구하기 <br>
 * - 날짜 및 시간 포맷을 API 규격에 맞게 변환
 * @see KmaClient
 */
public class KmaTime {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static BaseDt latestUltraSrtNcstBase(LocalDateTime nowKst) {
        LocalDate date = nowKst.toLocalDate();
        int hour = nowKst.getHour();
        String baseDate = date.format(DATE_FMT); // ex) 2026-01-29 → 20260129
        String baseTime = String.format("%02d00", hour); // ex) 7 → 0700
        return new BaseDt(baseDate, baseTime);
    }

    public static BaseDt latestUltraSrtFcstBase(LocalDateTime nowKst) {
        if(nowKst.getMinute() < 30) {
            nowKst = nowKst.minusHours(1);
        }

        LocalDateTime base = nowKst.truncatedTo(ChronoUnit.HOURS); // ex) 07:10:20 → 07:00:00
        base = base.plusMinutes(30); // ex) 07:00:00 → 07:30:00

        String baseDate = base.toLocalDate().format(DATE_FMT); // ex) 2026-01-29 → 20260129
        String baseTime = base.toLocalTime().format(DateTimeFormatter.ofPattern("HHmm")); // ex) 07:30:00 → 0730
        return new BaseDt(baseDate, baseTime);
    }

    public static BaseDt previousUltraSrtNcstBase(String baseDate, String baseTime) {
        LocalDate date = LocalDate.parse(baseDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        int hour = Integer.parseInt(baseTime.substring(0, 2));

        LocalDateTime dt = date.atTime(hour, 0).minusHours(1);

        String newDate = dt.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String newTime = String.format("%02d00", dt.getHour());
        return new BaseDt(newDate, newTime);
    }

    public static BaseDt previousUltraSrtFcstBase(String baseDate, String baseTime) {
        LocalDateTime dt = LocalDateTime.parse(baseDate + baseTime, DT_FMT).minusHours(1);

        String newDate = dt.toLocalDate().format(DATE_FMT);
        String newTime = dt.toLocalTime().format(DateTimeFormatter.ofPattern("HHmm"));
        return new BaseDt(newDate, newTime);
    }

    public record BaseDt(String baseDate, String baseTime) {}
}
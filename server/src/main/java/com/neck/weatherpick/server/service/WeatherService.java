package com.neck.weatherpick.server.service;

import com.neck.weatherpick.server.client.geo.KakaoLocalClient;
import com.neck.weatherpick.server.client.geo.dto.request.LonLat;
import com.neck.weatherpick.server.client.kma.KmaClient;
import com.neck.weatherpick.server.client.kma.dto.request.KmaGridPoint;
import com.neck.weatherpick.server.client.kma.dto.response.ncst.NcstItem;
import com.neck.weatherpick.server.client.kma.dto.response.ncst.NcstResponse;
import com.neck.weatherpick.server.client.kma.dto.response.fcst.FcstItem;
import com.neck.weatherpick.server.client.kma.dto.response.fcst.FcstResponse;
import com.neck.weatherpick.server.client.kma.support.KmaGridConverter;
import com.neck.weatherpick.server.client.kma.support.KmaTime;
import com.neck.weatherpick.server.dto.WeatherNowResponse;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {
    private final KmaClient kmaClient;
    private final KakaoLocalClient kakaoLocalClient;

    public WeatherService(KmaClient kmaClient, KakaoLocalClient kakaoLocalClient) {
        this.kmaClient = kmaClient;
        this.kakaoLocalClient = kakaoLocalClient;
    }

    public WeatherNowResponse getNowByRegion(String region) {
        LonLat lonLat = kakaoLocalClient.keywordToLonLat(region); // 1. 해당 지역(region)의 위ㆍ경도 조회  →  카카오맵 API 이용
        String addressName = lonLat.addressName(), placeName = lonLat.placeName();
        double lon = lonLat.lon(), lat = lonLat.lat();

        KmaGridPoint kmaGridPoint = KmaGridConverter.convert(lon, lat); // 2. 위ㆍ경도를 기상청 격자 좌표로 변환

        return getNow(kmaGridPoint, addressName, placeName); // 3. 기상청 격자 좌표에 해당하는 지점(addressName)의 날씨를 조회  →  공공데이터포털 API 이용
    }

    public WeatherNowResponse getNowByCoord(double lon, double lat) {
        String addressName = kakaoLocalClient.coordToAddress(lon, lat);
        KmaGridPoint kmaGridPoint = KmaGridConverter.convert(lon, lat);
        return getNow(kmaGridPoint, addressName, null);
    }

    private WeatherNowResponse getNow(KmaGridPoint kmaGridPoint, String resolvedAddress, String resolvedPlaceName) {
        LocalDateTime nowKst = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        KmaTime.BaseDt ncstBase = KmaTime.latestUltraSrtNcstBase(nowKst);
        NcstResponse ncstResponse = kmaClient.getUltraSrtNcst(ncstBase.baseDate(), ncstBase.baseTime(), kmaGridPoint);

        // 날씨 데이터를 쉽게 꺼내 쓰기 위해 category(항목 코드)를 key로, 실제 관측값(obsrValue)을 value로 가지는 Map으로 변환
        Map<String, String> m = new HashMap<>();
        for(NcstItem item : ncstResponse.response().body().items().item()) {
            m.put(item.category(), item.obsrValue());
        }

        double t1h = Double.parseDouble(m.getOrDefault("T1H", "0"));    // 기온(섭씨)
        double rn1 = Double.parseDouble(m.getOrDefault("RN1", "0"));    // 1시간 강수량(mm)
        int reh = Integer.parseInt(m.getOrDefault("REH", "0"));         // 습도(%)
        double wsd = Double.parseDouble(m.getOrDefault("WSD", "0"));    // 풍속(m/s)
        int pty = Integer.parseInt(m.getOrDefault("PTY", "0"));         // 강수 형태

        String precipType = switch(pty) {
            case 0 -> "NONE";
            case 1 -> "RAIN";           // 비
            case 2 -> "RAIN_SNOW";      // 진눈깨비
            case 3 -> "SNOW";           // 눈
            case 5 -> "DRIZZLE";        // 이슬비
            case 6 -> "DRIZZLE_SNOW";   // 이슬비 + 눈 날림
            case 7 -> "SNOW_FLURRY";    // 눈 날림
            default -> "UNKNOWN";
        };

        String skyType = extractSkyType(nowKst, kmaGridPoint);

        return new WeatherNowResponse(resolvedAddress, resolvedPlaceName, t1h, rn1, reh, wsd, precipType, skyType);
    }

    private String extractSkyType(LocalDateTime nowKst, KmaGridPoint kmaGridPoint) {
        KmaTime.BaseDt fcstBase = KmaTime.latestUltraSrtFcstBase(nowKst);
        FcstResponse fcstResponse = kmaClient.getUltraSrtFcst(fcstBase.baseDate(), fcstBase.baseTime(), kmaGridPoint);

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HHmm");

        long bestDiffMin = Long.MAX_VALUE;
        String bestSky = null;

        for(FcstItem item : fcstResponse.response().body().items().item()) {
            if(!"SKY".equals(item.category())) continue;
            LocalDate d = LocalDate.parse(item.fcstDate(), dateFmt);
            LocalTime t = LocalTime.parse(item.fcstTime(), timeFmt);
            LocalDateTime dt = LocalDateTime.of(d, t);

            // 현재 시각과 예보 시각 간의 차이를 구해, 그중 현재 시각에 가장 가까운 예보의 하늘 상태를 구한다.
            long diff = Math.abs(Duration.between(nowKst, dt).toMinutes());
            if(diff < bestDiffMin) {
                bestDiffMin = diff;
                bestSky = item.fcstValue();
            }
        }

        int skyCode = Integer.parseInt(bestSky);
        return switch(skyCode) {
            case 1 -> "CLEAR";          // 맑음
            case 3 -> "PARTLY_CLOUDY";  // 구름 많음
            case 4 -> "CLOUDY";         // 흐림
            default -> "UNKNOWN";
        };
    }
}
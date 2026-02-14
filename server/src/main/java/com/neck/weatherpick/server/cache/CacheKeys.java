package com.neck.weatherpick.server.cache;

import com.neck.weatherpick.server.client.kma.dto.request.KmaGridPoint;
import com.neck.weatherpick.server.dto.WeatherNowResponse;

/**
 * <h5>SpEL(@Cacheable key = ...)에서 사용하기 위한 캐시 Key 생성 유틸 클래스</h5>
 * 캐시 Key를 생성할 땐 가능하다면 '완전 동일'보다는, 아래 kakaoCoordToAddressKey 및 aiKey처럼 비슷한 상황을 묶어 '구간화'하는 것이 좋다.
 * 그래야 적중률(다시 계산하지 않고, 캐시에서 값을 가져오는 비율)이 올라가기 때문이다.
 */
public final class CacheKeys {
    private CacheKeys() {}

    public static String kakaoKeywordToLonLatKey(String regionName) {
        return regionName.trim();
    }

    // https://naver.me/5If20SNh 참고
    public static String kakaoCoordToAddressKey(double lon, double lat) {
        /*
            소수점 넷째 자리까지 반올림
            ex 1) 3.141592 → 3.1416
            ex 2) 126.7247245 → 126.7247
        */
        double qLon = round(lon, 4);
        double qLat = round(lat, 4);
        return "lon=" + qLon + "|lat=" + qLat;
    }

    private static double round(double value, int scale) {
        double factor = Math.pow(10, scale);
        return Math.round(value * factor) / factor;
    }

    public static String kmaKey(String baseDate, String baseTime, KmaGridPoint p) {
        return baseDate + ":" + baseTime + ":" + p.nx() + ":" + p.ny();
    }

    // 날씨 같은 경우에는 어차피 미세한 차이는 체감하기 어렵기 때문에 구간화가 가능하다.
    public static String aiKey(WeatherNowResponse w) {
        int t = (int)Math.round(w.tempC());         // 온도 : 1도 단위 반올림
        int h = (w.humidity() / 5) * 5;             // 습도 : 5% 단위 내림
        int ws = (int)Math.round(w.windSpeedMs());  // 풍속 : 1m/s 단위 반올림
        String rnBucket = rainBucket(w.precipitation1hMm());

        return "t=" + t +
                "|h=" + h +
                "|ws=" + ws +
                "|rn=" + rnBucket +
                "|pty=" + w.precipType() +
                "|sky=" + w.skyType();
    }

    private static String rainBucket(double rn1) {
        if(rn1 <= 0) return "0";    // 0.0mm
        if(rn1 <= 1) return "0-1";  // 0.1 ~ 1.0mm
        if(rn1 <= 5) return "1-5";  // 1.1 ~ 5.0mm
        return "5+";                // 5.1mm 이상
    }
}
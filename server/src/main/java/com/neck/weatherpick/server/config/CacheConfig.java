package com.neck.weatherpick.server.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

/**
 * <h5>외부 API 호출 비용/지연을 줄이기 위한 캐시 설정</h5>
 */
@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cm = new SimpleCacheManager();

        /*
            1. Kakao Maps
            - 입력 : 지역
            - 출력 : 주소, 장소명, 위ㆍ경도
            주소, 장소명, 위ㆍ경도  ←  이러한 정보는 웬만하면 바뀔 일이 없으므로, TTL을 길게 설정하는 것이 좋다.
        */
        CaffeineCache kakaoLonLat = new CaffeineCache(
                "kakaoLonLat",
                Caffeine.newBuilder()
                        .maximumSize(100) // 최대 100개까지 캐싱(만약 100개가 넘으면, 오래 사용되지 않은 것부터 자동 삭제)
                        .expireAfterWrite(Duration.ofDays(1)) // 캐시에 저장된 지 1일이 지나면, 자동 삭제
                        .build()
        );

        /*
            2. Kakao Maps
            - 입력 : 위ㆍ경도
            - 출력 : 주소
            주소  ←  마찬가지로 이러한 정보는 웬만하면 바뀔 일이 없으므로, TTL을 길게 설정하는 것이 좋다.
        */
        CaffeineCache kakaoAddr = new CaffeineCache(
                "kakaoAddr",
                Caffeine.newBuilder()
                        .maximumSize(200)
                        .expireAfterWrite(Duration.ofDays(1))
                        .build()
        );

        /*
            3. 공공데이터포털
            - 입력 : 기상청 격자 좌표
            - 출력 : 기상청 격자 좌표에 해당하는 지점의 날씨 정보
            날씨는 계속 바뀌므로, 오래 캐싱하면 잘못된 정보가 될 수 있다.
            즉, TTL을 짧게 설정하는 것이 좋다.
        */
        CaffeineCache kmaNcst = new CaffeineCache(
                "kmaNcst",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .build()
        );
        CaffeineCache kmaFcst = new CaffeineCache(
                "kmaFcst",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .build()
        );

        /*
            4. OpenAI
            - 입력 : 날씨 정보
            - 출력 : 날씨 정보를 기반으로 옷차림/준비물 추천
            OpenAI API는 호출할 때마다 비용/지연이 발생한다.
            더군다나, 동일하거나 유사한 날씨 조건에서의 추천 결과는 변화가 거의 없다.
            즉, 재사용 가치가 높으므로 TTL을 길게 설정하는 것이 좋다.
        */
        CaffeineCache aiReco = new CaffeineCache(
                "aiReco",
                Caffeine.newBuilder()
                        .maximumSize(100)
                        .expireAfterWrite(Duration.ofDays(1))
                        .build()
        );

        cm.setCaches(List.of(kakaoLonLat, kakaoAddr, kmaNcst, kmaFcst, aiReco));
        return cm;
    }
}
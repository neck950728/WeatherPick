package com.neck.weatherpick.server.client.geo;

import com.neck.weatherpick.server.client.geo.dto.request.LonLat;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @see <a href="https://developers.kakao.com/docs/latest/ko/local/dev-guide">API Docs</a>
 */
@Component
public class KakaoLocalClient {
    private final KakaoLocalProperties props;
    private final WebClient webClient;

    public KakaoLocalClient(KakaoLocalProperties props) {
        this.props = props;
        this.webClient = WebClient.builder().baseUrl(props.getBaseUrl()).build();
    }

    /*
        📣 응답 예시 📣
        {
            "documents": [
                {
                    "address_name": "인천 부평구 부평동 224-1",
                    "place_name": "부평문화의거리",
                    "x": "126.724277577653",
                    "y": "37.4941629743516",
                    ...
                }
            ],
            ...
        }
    */
    @Cacheable(
            cacheNames = "kakaoLonLat",
            key = "T(com.neck.weatherpick.server.cache.CacheKeys).kakaoKeywordToLonLatKey(#p0)",
            unless = "#result == null"
    )
    public LonLat keywordToLonLat(String regionName) {
        String query = regionName.trim();

        KakaoKeywordResponse res = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/search/keyword.json")
                        .queryParam("query", query)
                        .queryParam("size", 1) // 검색 결과 중 최상위 1개만 조회
                        .build())
                .header("Authorization", "KakaoAK " + props.getRestApiKey())
                .retrieve() // 요청 전송 + 응답받을 준비
                .bodyToMono(KakaoKeywordResponse.class) // JSON → KakaoKeywordResponse 객체로 변환(Mono : 응답이 0 ~ 1개인 경우 / Flux : 0 ~ 여러 개)
                .block(); // 완료될 때까지 대기

        String addressName = res.documents[0].address_name;
        String placeName = res.documents[0].place_name;
        double lon = Double.parseDouble(res.documents[0].x);
        double lat = Double.parseDouble(res.documents[0].y);
        return new LonLat(addressName, placeName, lon, lat);
    }

    /*
        {
            "documents": [
                {
                    "road_address": {
                        "address_name": "인천광역시 부평구 부평대로278번길 42",
                        ...
                    },
                    "address": {
                        "address_name": "인천 부평구 갈산동 179-1",
                        ...
                    }
                }
            ],
            ...
        }
    */
    @Cacheable(
            cacheNames = "kakaoAddr",
            key = "T(com.neck.weatherpick.server.cache.CacheKeys).kakaoCoordToAddressKey(#p0, #p1)",
            unless = "#result == null"
    )
    public String coordToAddress(double lon, double lat) {
        KakaoCoordResponse res = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2address.json")
                        .queryParam("x", lon)
                        .queryParam("y", lat)
                        .build())
                .header("Authorization", "KakaoAK " + props.getRestApiKey())
                .retrieve()
                .bodyToMono(KakaoCoordResponse.class)
                .block();

        return res.documents[0].address.address_name;
    }

    static class KakaoKeywordResponse {
        public Document[] documents;

        static class Document {
            public String address_name; // 실제 조회된 지역의 주소
            public String place_name;   // 실제 조회된 지역의 장소명
            public String x;            // longitude(경도)
            public String y;            // latitude(위도)
        }
    }

    static class KakaoCoordResponse {
        public Document[] documents;

        static class Document {
            public RoadAddress road_address;
            public Address address;
        }

        static class RoadAddress {
            public String address_name;
        }

        static class Address {
            public String address_name;
        }
    }
}
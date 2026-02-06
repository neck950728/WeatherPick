package com.neck.weatherpick.server.client.geo;

import com.neck.weatherpick.server.client.geo.dto.request.LatLon;
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
                    "x": "126.724277577653",
                    "y": "37.4941629743516",
                    ...
                },
                ...
            ],
            ...
        }
    */
    public LatLon keywordToLatLon(String regionName) {
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

        // x(경도), y(위도)
        double lon = Double.parseDouble(res.documents[0].x);
        double lat = Double.parseDouble(res.documents[0].y);
        return new LatLon(lat, lon);
    }

    static class KakaoKeywordResponse {
        public Document[] documents;

        static class Document {
            public String x; // longitude
            public String y; // latitude
        }
    }
}
package com.neck.weatherpick.server.client.kma;

import com.neck.weatherpick.server.client.kma.dto.request.KmaGridPoint;
import com.neck.weatherpick.server.client.kma.dto.response.KmaApiResponse;
import com.neck.weatherpick.server.client.kma.dto.response.ncst.NcstResponse;
import com.neck.weatherpick.server.client.kma.dto.response.fcst.FcstResponse;
import com.neck.weatherpick.server.client.kma.support.KmaTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <h5>UltraSrtNcst</h5>
 * <ul>
 *     <li>초단기 <u>실황</u></li>
 *     <li>발표 시각 : 매 정시(HH00)</li>
 * </ul>
 *
 * <h5>UltraSrtFcst</h5>
 * <ul>
 *     <li>초단기 <u>예보</u>(최대 6시간)</li>
 *     <li>발표 시각 : 매 시각 30분(HH30)</li>
 * </ul>
 *
 * @see <a href="https://www.data.go.kr/data/15084084/openapi.do">API Docs</a>
 */
@Component
@Slf4j
public class KmaClient {
    private final KmaProperties props;
    private final WebClient webClient;

    public KmaClient(KmaProperties props) {
        this.props = props;
        this.webClient = WebClient.builder().baseUrl(props.getBaseUrl()).build();
    }

    /*
        ❓ 2차 시도란
        현재 시각의 데이터가 발표되었다 하더라도, 실제 API 반영까지는 수십 분의 지연이 발생할 수 있다.
        따라서 현재 시각 기준 base_time 요청이 실패(응답 : null)할 경우,
        아직 반영이 안 된 것이므로 직전 시각의 데이터라도 재요청한다.
    */

    public NcstResponse getUltraSrtNcst(String baseDate, String baseTime, KmaGridPoint kmaGridPoint) {
        // 1차 시도
        NcstResponse ncstResponse = requestUltraSrtNcst(baseDate, baseTime, kmaGridPoint);
        if(isValid(ncstResponse)) return ncstResponse;

        log.warn("============================== 2차 시도(초단기 실황) ==============================");

        // 2차 시도
        KmaTime.BaseDt prev = KmaTime.previousUltraSrtNcstBase(baseDate, baseTime);
        return requestUltraSrtNcst(prev.baseDate(), prev.baseTime(), kmaGridPoint);
    }

    public FcstResponse getUltraSrtFcst(String baseDate, String baseTime, KmaGridPoint kmaGridPoint) {
        // 1차 시도
        FcstResponse fcstResponse = requestUltraSrtFcst(baseDate, baseTime, kmaGridPoint);
        if(isValid(fcstResponse)) return fcstResponse;

        log.warn("============================== 2차 시도(초단기 예보) ==============================");

        // 2차 시도
        KmaTime.BaseDt prev = KmaTime.previousUltraSrtFcstBase(baseDate, baseTime);
        return requestUltraSrtFcst(prev.baseDate(), prev.baseTime(), kmaGridPoint);
    }

    /*
        📣 응답 예시 📣
        {
          "response": {
            ...
            "body": {
              "items": {
                "item": [
                  { "category": "T1H", "obsrValue": "-8.4", ... },
                  { "category": "RN1", "obsrValue": "0", ... },
                  { "category": "REH", "obsrValue": "44", ... },
                  { "category": "WSD", "obsrValue": "2.5", ... },
                  { "category": "PTY", "obsrValue": "0", ... },
                  ...
                ]
              },
              ...
            }
          }
        }
    */
    public NcstResponse requestUltraSrtNcst(String baseDate, String baseTime, KmaGridPoint kmaGridPoint) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/getUltraSrtNcst")
                    .queryParam("serviceKey", props.getServiceKey())
                    .queryParam("dataType", "JSON") // 응답 형식

                    // https://naver.me/5UTYwQ5q 참고
                    .queryParam("numOfRows", 20)
                    .queryParam("pageNo", 1)

                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", kmaGridPoint.nx())
                    .queryParam("ny", kmaGridPoint.ny())
                    .build())
                .retrieve() // 요청 전송 + 응답받을 준비
                .bodyToMono(NcstResponse.class) // JSON → NcstResponse 객체로 변환(Mono : 응답이 0 ~ 1개인 경우 / Flux : 0 ~ 여러 개)
                .block(); // 완료될 때까지 대기
    }

    /*
        📣 응답 예시 📣
        {
          "response": {
            ...
            "body": {
              "items": {
                "item": [
                  { "category": "T1H", "fcstDate": "20260202", "fcstTime": "1200", "fcstValue": "-1", ... },
                  { "category": "T1H", "fcstDate": "20260202", "fcstTime": "1300", "fcstValue": "-1", ... },
                  { "category": "T1H", "fcstDate": "20260202", "fcstTime": "1400", "fcstValue": "0", ... },
                  { "category": "T1H", "fcstDate": "20260202", "fcstTime": "1500", "fcstValue": "0", ... },
                  { "category": "T1H", "fcstDate": "20260202", "fcstTime": "1600", "fcstValue": "0", ... },
                  { "category": "T1H", "fcstDate": "20260202", "fcstTime": "1700", "fcstValue": "0", ... },

                  { "category": "SKY", "fcstDate": "20260202", "fcstTime": "1200", "fcstValue": "1", ... },
                  { "category": "SKY", "fcstDate": "20260202", "fcstTime": "1300", "fcstValue": "1", ... },
                  { "category": "SKY", "fcstDate": "20260202", "fcstTime": "1400", "fcstValue": "1", ... },
                  { "category": "SKY", "fcstDate": "20260202", "fcstTime": "1500", "fcstValue": "1", ... },
                  { "category": "SKY", "fcstDate": "20260202", "fcstTime": "1600", "fcstValue": "1", ... },
                  { "category": "SKY", "fcstDate": "20260202", "fcstTime": "1700", "fcstValue": "1", ... },

                  ...
                ]
              },
              ...
            }
          }
        }
    */
    public FcstResponse requestUltraSrtFcst(String baseDate, String baseTime, KmaGridPoint kmaGridPoint) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtFcst")
                        .queryParam("serviceKey", props.getServiceKey())
                        .queryParam("dataType", "JSON")
                        .queryParam("numOfRows", 60)
                        .queryParam("pageNo", 1)
                        .queryParam("base_date", baseDate)
                        .queryParam("base_time", baseTime)
                        .queryParam("nx", kmaGridPoint.nx())
                        .queryParam("ny", kmaGridPoint.ny())
                        .build())
                .retrieve()
                .bodyToMono(FcstResponse.class)
                .block();
    }

    private boolean isValid(KmaApiResponse<?> res) {
        return res != null
            && res.response() != null
            && res.response().body() != null;
    }
}
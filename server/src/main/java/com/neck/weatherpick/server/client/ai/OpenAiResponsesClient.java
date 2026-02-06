package com.neck.weatherpick.server.client.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://platform.openai.com/docs/api-reference/responses">API Docs</a>
 */
@Component
public class OpenAiResponsesClient {
    private final OpenAiProperties props;
    private final ObjectMapper om;
    private final WebClient webClient;

    public OpenAiResponsesClient(OpenAiProperties props, ObjectMapper om, WebClient.Builder webClientBuilder) {
        this.props = props;
        this.om = om;
        this.webClient = webClientBuilder
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 요청 바디 형식 지정(Content-Type: application/json)
                .build();
    }

    public JsonNode createTextResponse(String system, String user) {
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.put("model", props.getModel());
        body.put("temperature", 0); // 응답 변동성 최소화(0 : 같은 입력에 항상 같은 출력)
        body.put("max_output_tokens", 60); // 응답 길이 제한(1토큰 : 한글 약 1 ~ 2자)  →  AI 모델에 따라 달라질 수 있으며, 공백ㆍ줄 바꿈도 토큰으로 계산됨
        body.put("input", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", user)
        ));

        return webClient.post()
                .uri("/responses")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getApiKey())
                .bodyValue(body)
                .retrieve() // 요청 전송 + 응답받을 준비
                .bodyToMono(JsonNode.class) // JSON → JsonNode 객체로 변환(Mono : 응답이 0 ~ 1개인 경우 / Flux : 0 ~ 여러 개)
                .block(); // 완료될 때까지 대기
    }

    /*
        📣 응답(responseJson) 예시 📣
        {
          ...
          "output": [
            {
              "id": "msg_0766facfd1117d6800697da036ed48819b82bac3d45a499e01",
              "type": "message",
              "status": "incomplete",
              "content": [
                {
                  "type": "output_text",
                  "annotations": [],
                  "logprobs": [],
                  "text": "- 옷차림 : 패딩, 히트텍, 목도리  \n- 준비물 : 우산"
                }
              ],
              "role": "assistant"
            }
          ],
          ...
        }
    */
    public static String extractOutputText(JsonNode responseJson) {
        JsonNode output = responseJson.get("output");
        JsonNode content = output.get(0).get("content");
        JsonNode first = content.get(0);
        JsonNode text = first.get("text");
        return text.asText();
    }
}
package com.ssafy.staticsserver.common.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GptClient {
    @Value("${API_KEY}")
    private String API_KEY;
    private final OkHttpClient client = new OkHttpClient();

    public String ask(String prompt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("model", "gpt-3.5-turbo");
        root.put("temperature", 0.7);
        root.put("max_tokens", 200); // 필요시 조절

        ArrayNode messages = mapper.createArrayNode();
        messages.add(mapper.createObjectNode()
                .put("role", "system")
                .put("content", "너는 뉴스 여론 분석 전문가야."));
        messages.add(mapper.createObjectNode()
                .put("role", "user")
                .put("content", prompt));

        root.set("messages", messages);

        String requestBody = mapper.writeValueAsString(root);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("GPT 호출 실패: " + response);
            }
            return extractMessage(response.body().string());
        }
    }

    private String extractMessage(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            return "GPT 응답 파싱 오류";
        }
    }
}
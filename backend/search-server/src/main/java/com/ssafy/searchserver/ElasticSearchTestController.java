package com.ssafy.searchserver;

import lombok.RequiredArgsConstructor;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/elasticsearch")
@RequiredArgsConstructor
public class ElasticSearchTestController {

    private final RestClient restClient;

    @GetMapping("/test")
    public ResponseEntity<String> testElasticConnection() {
        try {
            Request request = new Request("GET", "/");
            Response response = restClient.performRequest(request);
            return ResponseEntity.ok("Elasticsearch 연결 성공! \n" + EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Elasticsearch 연결 실패 : " + e.getMessage());
        }
    }
}

package com.ssafy.searchserver;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mongo")
@RequiredArgsConstructor
public class MongoTestController {

    private final MongoTemplate mongoTemplate;

    @GetMapping("/test")
    public ResponseEntity<String> testMongoConnection() {
        try {
            mongoTemplate.getDb().getName();
            return ResponseEntity.ok("MongoDB 연결 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("MongoDB 연결 실패 : " + e.getMessage());
        }
    }
}

package com.ssafy.staticsserver.domain.news.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.staticsserver.domain.news.model.DomesticNewsMongo;

public interface DomesticNewsMongoDBRepository extends MongoRepository<DomesticNewsMongo, String> {
}

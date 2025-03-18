package com.ssafy.searchserver.news.repository;

import com.ssafy.searchserver.news.entity.ForeignNewsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
}

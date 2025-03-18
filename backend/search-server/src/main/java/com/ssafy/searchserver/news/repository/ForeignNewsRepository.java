package com.ssafy.searchserver.news.repository;

import com.ssafy.searchserver.news.entity.ForeignNews;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForeignNewsRepository extends MongoRepository<ForeignNews, String> {
}

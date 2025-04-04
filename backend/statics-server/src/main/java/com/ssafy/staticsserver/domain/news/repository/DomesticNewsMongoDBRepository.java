package com.ssafy.staticsserver.domain.news.repository;

import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.staticsserver.domain.news.model.DomesticNewsMongo;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DomesticNewsMongoDBRepository extends MongoRepository<DomesticNewsMongo, String> {
    List<ForeignNewsMongo> findByIdIn(List<String> idList);
    @Query(value = "{ '_id': { $in: ?0 } }", fields = "{ 'keywords': 1, '_id': 0 }") //  조회 필드 지정 keywords만 조회
    List<KeywordsOnly> findKeywordsOnly(List<String> idList);
}

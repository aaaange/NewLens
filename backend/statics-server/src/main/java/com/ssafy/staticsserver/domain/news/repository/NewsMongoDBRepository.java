package com.ssafy.staticsserver.domain.news.repository;

import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NewsMongoDBRepository {
    List<ForeignNewsMongo> findByIdIn(List<String> ids);
    @Query(value = "{ '_id': { $in: ?0 } }", fields = "{ 'keywords': 1, '_id': 0 }") //  조회 필드 지정 keywords만 조회
    List<KeywordsOnly> findKeywordsOnly(List<String> idList);
    Page<ForeignNewsMongo> findByIdIn(List<String> ids, Pageable pageable);
}

package com.ssafy.staticsserver.domain.news.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);
	// ID필드에서 $in => SQL의 IN , 리스트에 포함되는거 찾음
	@Query(value = "{ '_id': { $in: ?0 } }", fields = "{ 'keywords': 1, '_id': 0 }") //  조회 필드 지정 keywords만 조회
	List<KeywordsOnly> findKeywordsOnly(List<String> idList);
	Page<ForeignNewsMongo> findByIdIn(List<String> idList, Pageable pageable);

}

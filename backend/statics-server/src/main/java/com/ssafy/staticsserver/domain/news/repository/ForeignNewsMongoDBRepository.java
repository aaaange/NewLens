package com.ssafy.staticsserver.domain.news.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);

}

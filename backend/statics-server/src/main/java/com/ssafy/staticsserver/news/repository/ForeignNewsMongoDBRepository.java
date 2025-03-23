package com.ssafy.staticsserver.news.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.staticsserver.news.entitiy.ForeignNewsMongo;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);

}

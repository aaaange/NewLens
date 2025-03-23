package com.ssafy.staticsserver.search.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ssafy.staticsserver.search.entitiy.ForeignNewsMongo;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);

}

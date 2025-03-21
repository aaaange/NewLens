package com.ssafy.searchserver.news.repository;

import java.util.Collection;
import java.util.List;

import com.ssafy.searchserver.news.entity.ForeignNewsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);

}

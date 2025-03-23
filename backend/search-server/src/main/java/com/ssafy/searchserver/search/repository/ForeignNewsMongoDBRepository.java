package com.ssafy.searchserver.search.repository;

import java.util.List;

import com.ssafy.searchserver.search.entity.ForeignNewsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);

}

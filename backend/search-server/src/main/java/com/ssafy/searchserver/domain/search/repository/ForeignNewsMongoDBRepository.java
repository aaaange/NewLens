package com.ssafy.searchserver.domain.search.repository;

import java.util.List;

import com.ssafy.searchserver.domain.search.model.ForeignNewsMongo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ForeignNewsMongoDBRepository extends MongoRepository<ForeignNewsMongo, String> {
	List<ForeignNewsMongo> findByIdIn(List<String> idList);

}

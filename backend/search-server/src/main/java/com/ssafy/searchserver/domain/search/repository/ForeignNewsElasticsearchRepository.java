package com.ssafy.searchserver.domain.search.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;

public interface ForeignNewsElasticsearchRepository extends ElasticsearchRepository<ForeignNewsElastic, String> {
	List<ForeignNewsElastic> findByKeywords(String keyword);
	List<ForeignNewsElastic> findByCategories(String category);
	List<ForeignNewsElastic> findByPublishedAtBetween(LocalDateTime from, LocalDateTime to);


}

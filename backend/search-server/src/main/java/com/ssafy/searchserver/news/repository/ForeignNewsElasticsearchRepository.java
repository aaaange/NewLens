package com.ssafy.searchserver.news.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ssafy.searchserver.news.entity.ForeignNewsElastic;

public interface ForeignNewsElasticsearchRepository extends ElasticsearchRepository<ForeignNewsElastic, String> {
	List<ForeignNewsElastic> findByKeywords(String keyword);
	List<ForeignNewsElastic> findByCategories(String category);
	List<ForeignNewsElastic> findByPublishedAtBetween(Instant from, Instant to);


}

package com.ssafy.searchserver.news.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.ssafy.searchserver.news.entity.ForeignNewsElastic;

public interface ForeignNewsElasticsearchRepository extends ElasticsearchRepository<ForeignNewsElastic, String> {
	List<ForeignNewsElastic> findByKeywordsContaining(String keyword);
	List<ForeignNewsElastic> findByCategoriesContaining(String category);
}

package com.ssafy.searchserver.news_es.repository;

import com.ssafy.searchserver.news_es.entity.ForeignNews;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface ForeignNewsRepository extends ElasticsearchRepository<ForeignNews, String> {
    List<ForeignNews> findByKeywordsContaining(String keyword);
    List<ForeignNews> findByCategoriesContaining(String category);
}


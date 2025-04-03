package com.ssafy.staticsserver.domain.news.repository;

import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class ForeignNewsRepositoryImpl implements NewsMongoDBRepository{
    private final ForeignNewsMongoDBRepository repository;


    @Override
    public List<ForeignNewsMongo> findByIdIn(List<String> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public List<KeywordsOnly> findKeywordsOnly(List<String> idList) {
        return repository.findKeywordsOnly(idList);
    }
}

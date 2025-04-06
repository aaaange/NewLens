package com.ssafy.staticsserver.domain.news.repository;

import com.ssafy.staticsserver.domain.news.model.ForeignNewsMongo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class DomesticNewsRepositoryImpl implements NewsMongoDBRepository {
    private final DomesticNewsMongoDBRepository repository;


    @Override
    public List<ForeignNewsMongo> findByIdIn(List<String> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public List<KeywordsOnly> findKeywordsOnly(List<String> idList) {
        return repository.findKeywordsOnly(idList);
    }

    @Override
    public Page<ForeignNewsMongo> findByIdIn(List<String> ids, Pageable pageable) { return repository.findByIdIn(ids, pageable); }
}

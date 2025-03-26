package com.ssafy.searchserver.application.country;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.searchserver.domain.search.model.ForeignNewsElastic;
import com.ssafy.searchserver.domain.search.model.ForeignNewsMongo;
import com.ssafy.searchserver.domain.search.repository.ForeignNewsMongoDBRepository;
import com.ssafy.searchserver.interfaces.country.dto.DashboardData;
import com.ssafy.searchserver.interfaces.country.dto.DashboardResponse;
import com.ssafy.searchserver.interfaces.country.dto.KeywordResponse;
import com.ssafy.searchserver.interfaces.search.dto.ForeignNewsListResponse;
import com.ssafy.searchserver.interfaces.search.dto.ForeignNewsResponse;
import com.ssafy.searchserver.interfaces.search.dto.RelatedKeywordsResponse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {

	private final ForeignNewsMongoDBRepository mongoDBRepository;
	private final ElasticsearchClient esClient;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public DashboardResponse getDashboard(String category, int period, String keyword,String keywordMind, String keywordCloud, String country,
		boolean is_korea) {
		try {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime from = now.minusDays(period);



			// 필터링 Query
			Query boolQuery = Query.of(q -> q.bool(b -> {
				List<Query> mustQueries = new ArrayList<>();

				mustQueries.add(Query.of(m -> m.term(t -> t
					.field("keywords")
					.value(FieldValue.of(keyword))
				)));

				if (!keywordMind.isEmpty()) {
					mustQueries.add(Query.of(m -> m.term(t -> t
						.field("keywords")
						.value(FieldValue.of(keywordMind))
					)));
				}

				if (!keywordCloud.isEmpty()) {
					mustQueries.add(Query.of(m -> m.term(t -> t
						.field("keywords")
						.value(FieldValue.of(keywordCloud))
					)));
				}

				mustQueries.add(Query.of(m -> m.term(t -> t
					.field("categories")
					.value(FieldValue.of(category))
				)));

				//                if (is_korea) {
				//                    mustQueries.add(Query.of(m -> m.term(t -> t
				//                            .field("country")
				//                            .value(FieldValue.of("KR"))
				//                    )));
				//                } else if (country != null && !country.isBlank()) {
				//                    mustQueries.add(Query.of(m -> m.term(t -> t
				//                            .field("country")
				//                            .value(FieldValue.of(country))
				//                    )));
				//                }

				mustQueries.add(Query.of(m -> m.range(r -> r
					.date(d -> d
						.field("published_at")
						.gte(from.toString())
						.lte(now.toString())
					)
				)));
				return b.must(mustQueries);
			}));

			// 연관어 추출 Aggregation
			Aggregation agg = Aggregation.of(a -> a
				.terms(t -> t
					.field("keywords")
					.size(20)
				)
			);

			// ID 조회용 searchRequest
			var searchRequest = SearchRequest.of(s -> s
					.index("foreign_news")
					.query(boolQuery)
					.size(10000)
					.aggregations("word_cloud", agg)
					.source(src -> src.filter(f -> f.includes("id")))
				// 우리는 id만 필요하니까 id만 반환
			);

			// ES에서 조회
			var response = esClient.search(searchRequest, ForeignNewsElastic.class);

			// ES에서 필터링 거친 뉴스 id 리스트 리턴
			List<String> idList = response.hits().hits().stream()
				.map(hit -> hit.source().getId())
				.collect(Collectors.toList());

			// kafka로 전송
			String json = objectMapper.writeValueAsString(idList);
			kafkaTemplate.send("dashboard", json);

			// 4. Aggregation 처리
			StringTermsAggregate aggregation = response.aggregations()
				.get("word_cloud")
				.sterms();

			List<KeywordResponse> wordCloud = aggregation.buckets().array().stream()
				.filter(rel -> !rel.equals(keyword)) // keyword1과 중복 제거
				.filter(rel -> !rel.equals(keywordMind))
				.filter(rel -> !rel.equals(keywordCloud))
				.map(bucket -> KeywordResponse.builder()
					.name(bucket.key().stringValue())
					.count(bucket.docCount())
					.build())
				.collect(Collectors.toList());


			System.out.println("워드 클라우드");
			for (KeywordResponse keywordResponse : wordCloud) {
				System.out.println(keywordResponse.toString());
			}
			return DashboardResponse.builder()

				.build();

		} catch (Exception e) {
			throw new RuntimeException("Dashboard 데이터 검색 실패", e);
		}
	}

}

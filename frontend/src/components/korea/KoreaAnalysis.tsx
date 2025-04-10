import WordCloud from '../worldDetail/Wordcloud';
import useCountryData from '../../hooks/useCountryData';
import useDescription from '../../hooks/useDescription';
import StackedColumns from '../worldDetail/StackedColumns';
import MentionChart from '../worldDetail/MentionChart';
import NewsList from '../worldDetail/NewsList';
import KoreaVideoList from './KoreaVideoList';
import NewsSummary from '../worldDetail/NewsSummary';
import NewsModal from '../common/NewsModal';
import { useMemo, useState } from 'react';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from '../worldDetail/MockData';
import GlobalSpinner from '../common/GlobalSpinner';

interface KoreaAnalysisProps {
  country: string;
  country_name: string;
  keyword: string;
  keyword_mind: string;
  category: string;
  period: number;
  is_korea: boolean;
}

const KoreaAnalysis = ({
  country,
  country_name,
  keyword,
  keyword_mind,
  category,
  period,
  is_korea,
}: KoreaAnalysisProps) => {
  const memoizedParams = useMemo(
    () => ({
      country,
      category,
      period,
      keyword,
      keyword_mind,
      is_korea,
    }),
    [country, keyword_mind, keyword, category, period]
  );
  const [keyword_cloud, setKeywordCloud] = useState('');
  const [isModal, setIsModal] = useState(false);
  const [selectCountry, setSelectCountry] = useState('');

  const { data, isLoading, error } = useCountryData(memoizedParams);
  console.log(keyword);

  const {
    data: gpt_data,
    isLoading: isLoading_GPT,
    error: error_GPT,
  } = useDescription(memoizedParams);

  if (isLoading) return <GlobalSpinner />;
  if (error)
    return (
      <div className="text-center mt-10 text-system-danger">
        오류가 발생했습니다: {error.message}
      </div>
    );

  // 서버 응답 없을 경우 목데이터로 대체
  const safeData = data ?? {
    keywords: words.map((word) => ({ text: word.text, value: word.value })),
    // description: description,
    sentimentData: sentimentData,
    mentions: mentionData,
    articles: newsData,
    videos: videos,
  };

  const description = gpt_data || '뉴스 요약을 불러오는 중입니다...🔥';

  const isAllDataEmpty =
    safeData.keywords?.length === 0 &&
    safeData.sentimentData?.length === 0 &&
    safeData.mentions?.length === 0 &&
    safeData.articles?.length === 0 &&
    safeData.videos?.length === 0;

  const handleModalOpen = (country: string) => {
    setIsModal(true);
    setSelectCountry('KR');
  };
  const handleModalClose = () => {
    setIsModal(false);
    setKeywordCloud('');
  };
  const handleWordCloudChange = (newKeyword: string) => {
    setKeywordCloud(newKeyword);
  };

  return (
    <div className="flex flex-col gap-5 mb-5">
      {isAllDataEmpty ? (
        // 키워드에 대한 뉴스가 0건일 경우 안내
        <div className="text-center flex flex-col gap-2">
          <div className="headline-xlarge">😢</div>
          <div>현재 선택한 키워드에 대한 뉴스가 없습니다.</div>
        </div>
      ) : (
        <>
          <WordCloud
            keywords={safeData.keywords}
            keyword_mind={keyword_mind}
            width={900}
            height={400}
            keyword={keyword}
            country_name={country_name}
            country_code={'KR'}
            handleWordCloudChange={handleWordCloudChange}
            handleModalOpen={handleModalOpen} // 모달 열기 함수
          />
          <NewsSummary
            description={description}
            width={900}
            height={100}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={'KR'}
          />

          <div className="flex">
            <StackedColumns
              data={safeData.sentimentData}
              width={450}
              height={250}
              keyword={keyword}
              keyword_mind={keyword_mind}
              country_name={country_name}
              country_code={'KR'}
            />
            <MentionChart
              data={safeData.mentions}
              width={450}
              height={250}
              keyword={keyword}
              keyword_mind={keyword_mind}
              country_name={country_name}
              country_code={'KR'}
            />
          </div>
          <NewsList
            news={safeData.articles}
            width={900}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={'KR'}
            handleModalOpen={handleModalOpen}
          />
          <KoreaVideoList
            videos={safeData.videos}
            width={900}
            height={300}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={'KR'}
          />
        </>
      )}
      {isModal && (
        <NewsModal
          handleModalClose={handleModalClose}
          category={category}
          period={period}
          keyword={keyword}
          keyword_mind={keyword_mind}
          keyword_cloud={keyword_cloud}
          country=""
          isKorea={true}
        />
      )}
    </div>
  );
};

export default KoreaAnalysis;
// const KoreaAnalysis = () => {
//   return <div>KoreaAnalysis</div>;
// };
// export default KoreaAnalysis;

import WordCloud from '../worldDetail/Wordcloud';
import useCountryData from '../../hooks/useCountryData';
import useDescription from '../../hooks/useDescription';
import StackedColumns from '../worldDetail/StackedColumns';
import MentionChart from '../worldDetail/MentionChart';
import NewsList from '../worldDetail/NewsList';
import KoreaVideoList from './KoreaVideoList';
import NewsSummary from '../worldDetail/NewsSummary';
import NewsModal from '../common/NewsModal';
import { useState } from 'react';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from '../worldDetail/MockData';

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
  const koreaParams = {
    country: country,
    category: category,
    period: period,
    keyword: keyword,
    keyword_mind: keyword_mind,
    is_korea: true,
  };
  const [isModal, setIsModal] = useState(false);
  const [selectCountry, setSelectCountry] = useState('');

  const handleModalOpen = (country: string) => {
    setIsModal(true);
    setSelectCountry('KR');
  };
  const handleModalClose = () => {
    setIsModal(false);
  };
  const [keyword_cloud, setKeywordCloud] = useState('');
  const handleWordCloudChange = (newKeyword: string) => {
    setKeywordCloud(newKeyword);
  };

  const { data, isLoading, error } = useCountryData(koreaParams);
  console.log('KoreaAnalysis data:', data);

  const {
    data: gpt_data,
    isLoading: isLoading_GPT,
    error: error_GPT,
  } = useDescription(koreaParams);

  if (isLoading) return <div>Loading...</div>;
  // if (error) return <div>Error! {error.message}</div>; // mock 데이터 제거 시 주석 풀어주기.

  // 서버 응답 없을 경우 목데이터로 대체
  const safeData = data ?? {
    keywords: words,
    // description: description,
    sentimentData: sentimentData,
    mentions: mentionData,
    articles: newsData,
    videos: videos,
  };

  const description = gpt_data || '뉴스 요약을 불러오는 중입니다...🔥';
  return (
    <div className="flex flex-col gap-5 mb-5">
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
      {isModal && (
        <NewsModal
          handleModalClose={handleModalClose}
          category={category}
          period={period}
          keyword={keyword}
          keyword_mind={keyword_mind}
          keyword_cloud={keyword_cloud}
          country={selectCountry}
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

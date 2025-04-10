import useCountryData from '../../hooks/useCountryData';
import useDescription from '../../hooks/useDescription';
import MentionChart from './MentionChart';
import NewsList from './NewsList';
import NewsSummary from './NewsSummary';
import StackedColumns from './StackedColumns';
import VideoList from './VideoList';
import WordCloud from './Wordcloud';
import { useMemo, useEffect } from 'react';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  videos,
} from './MockData';
import GlobalSpinner from '../common/GlobalSpinner';

interface FirstCountryBoardProps {
  country: string;
  country_name: string;
  keyword: string;
  keyword_mind: string;
  category: string;
  period: number;
  handleWordCloudChange: (word: string) => void;
  handleModalOpen: (country: string) => void;
  handleModalClose: () => void;
}

const FirstCountryBoard = ({
  country,
  country_name,
  keyword,
  keyword_mind,
  category,
  period,
  handleWordCloudChange,
  handleModalOpen,
}: FirstCountryBoardProps) => {
  const memoizedParams = useMemo(
    () => ({
      category,
      period,
      keyword,
      keyword_mind,
      country,
      is_korea: country === 'kr',
    }),
    [country, keyword_mind, keyword, category, period]
  );

  const { data, isLoading, error } = useCountryData(memoizedParams);
  const {
    data: gpt_data,
    isLoading: isLoading_GPT,
    error: error_GPT,
  } = useDescription(memoizedParams);

  if (isLoading) return <GlobalSpinner />;
  if (error) return <div>Error! {error.message}</div>; // mock 데이터 제거 시 주석 풀어주기.
  // if (!data) return <div>No Data</div>;

  // const country_name: string = '미국';

  // 서버 응답 없을 경우 목데이터로 대체
  const safeData = data ?? {
    keywords: words.map((word) => ({ text: word.text, value: word.value })),
    // description: description,
    sentimentData: sentimentData,
    mentions: mentionData,
    articles: newsData,
    videos: videos,
  };

  const description = isLoading_GPT
    ? '뉴스 요약을 불러오는 중입니다...🔥'
    : (gpt_data ?? '요약 데이터가 없습니다.');

  // 모든 데이터가 비어있는지 확인하는 조건
  const isAllDataEmpty =
    safeData.keywords?.length === 0 &&
    safeData.sentimentData?.length === 0 &&
    safeData.mentions?.length === 0 &&
    safeData.articles?.length === 0 &&
    safeData.videos?.length === 0;

  return (
    <div className="flex flex-col gap-5">
      {isAllDataEmpty ? (
        // 키워드에 대한 뉴스가 0건일 경우 안내
        <div className="text-center flex flex-col gap-2">
          <div className="headline-xlarge">😢</div>
          <div>현재 선택한 키워드에 대한 뉴스가 없습니다.</div>
        </div>
      ) : (
        <>
          <WordCloud
            handleWordCloudChange={handleWordCloudChange}
            handleModalOpen={handleModalOpen}
            keyword={keyword}
            keywords={safeData.keywords}
            keyword_mind={keyword_mind}
            width={410}
            height={200}
            country_name={country_name}
            country_code={country}
          />
          <NewsSummary
            description={description}
            width={410}
            height={150}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={country}
          />
          <StackedColumns
            data={safeData.sentimentData}
            width={410}
            height={200}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={country}
          />
          <MentionChart
            data={safeData.mentions}
            width={410}
            height={200}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={country}
          />
          <NewsList
            handleModalOpen={handleModalOpen}
            news={safeData.articles}
            width={410}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={country}
          />
          <VideoList
            videos={safeData.videos}
            width={410}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={country}
          />
        </>
      )}
    </div>
  );
};

export default FirstCountryBoard;

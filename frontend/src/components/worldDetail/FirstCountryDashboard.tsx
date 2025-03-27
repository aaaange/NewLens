import useCountryData from '../../hooks/useCountryData';
import MentionChart from './MentionChart';
import NewsList from './NewsList';
import NewsSummary from './NewsSummary';
import StackedColumns from './StackedColumns';
import VideoList from './VideoList';
import WordCloud from './Wordcloud';
import { useMemo } from 'react';

import {
  words,
  sentimentData,
  mentionData,
  newsData,
  description,
  videos,
} from './MockData';

interface FirstCountryBoardProps {
  country: string;
  country_name: string;
  keyword: string;
  category: string;
  period: number;
}

const FirstCountryBoard = ({
  country,
  country_name,
  keyword,
  category,
  period,
}: FirstCountryBoardProps) => {
  const memoizedParams = useMemo(
    () => ({
      country,
      country_name,
      keyword,
      category,
      period,
      is_korea: country === 'kr',
    }),
    [country, country_name, keyword, category, period]
  );

  const { data, isLoading, error } = useCountryData(memoizedParams);

  if (isLoading) return <div>Loading...</div>;
  // if (error) return <div>Error! {error.message}</div>; // mock 데이터 제거 시 주석 풀어주기.
  // if (!data) return <div>No Data</div>;

  // const country_name: string = '미국';

  // 서버 응답 없을 경우 목데이터로 대체
  const safeData = data ?? {
    keywords: words.map((word) => ({ text: word.text, value: word.value })),
    description: description,
    sentimentData: sentimentData,
    mentions: mentionData,
    articles: newsData,
    videos: videos,
  };

  return (
    <div className="flex flex-col gap-5">
      <WordCloud
        keyword={keyword}
        keywords={safeData.keywords}
        width={410}
        height={200}
        country_name={country_name}
        country_code={country}
      />
      <NewsSummary
        description={safeData.description}
        width={410}
        height={150}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
      <StackedColumns
        data={safeData.sentimentData}
        width={410}
        height={200}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
      <MentionChart
        data={safeData.mentions}
        width={410}
        height={200}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
      <NewsList
        news={safeData.articles}
        width={410}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
      <VideoList
        videos={safeData.videos}
        width={410}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
    </div>
  );
};

export default FirstCountryBoard;

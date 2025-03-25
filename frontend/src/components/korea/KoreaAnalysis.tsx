import WordCloud from '../worldDetail/Wordcloud';
import useCountryData from '../../hooks/useCountryData';
import StackedColumns from '../worldDetail/StackedColumns';
import MentionChart from '../worldDetail/MentionChart';
import NewsList from '../worldDetail/NewsList';
import KoreaVideoList from './KoreaVideoList';
import { useMemo } from 'react';

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
  keyword: string | string[];
  category: string;
  period: string;
}

// const country = 'KR';
// const country_name = '대한민국';

const KoreaAnalysis = ({
  country,
  country_name,
  keyword,
  category,
  period,
}: KoreaAnalysisProps) => {
  const memoizedParams = useMemo(
    () => ({
      country,
      country_name,
      keyword,
      category,
      period,
      is_korea: country === 'KR',
    }),
    [country, country_name, keyword, category, period]
  );

  const { data, isLoading, error } = useCountryData(memoizedParams);

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error! {error.message}</div>;

  // 서버 응답 없을 경우 목데이터로 대체
  const safeData = data ?? {
    keywords: words,
    description: description,
    sentimentData: sentimentData,
    mentions: mentionData,
    articles: newsData,
    videos: videos,
  };
  return (
    <div className='flex flex-col gap-5 mb-5'>
      <WordCloud
        keywords={safeData.keywords}
        width={900}
        height={400}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
      <div className="flex">
        <StackedColumns
          data={safeData.sentimentData}
          width={450}
          height={250}
          keyword={keyword}
          country_name={country_name}
          country_code={country}
        />
        <MentionChart
          data={safeData.mentions}
          width={450}
          height={250}
          keyword={keyword}
          country_name={country_name}
          country_code={country}
        />
      </div>
      <NewsList
        news={safeData.articles}
        width={900}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
      <KoreaVideoList
        videos={safeData.videos}
        width={900}
        keyword={keyword}
        country_name={country_name}
        country_code={country}
      />
    </div>
  );
};

export default KoreaAnalysis;

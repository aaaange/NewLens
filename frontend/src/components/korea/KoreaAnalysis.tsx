// import WordCloud from '../worldDetail/Wordcloud';
// import useCountryData from '../../hooks/useCountryData';
// import MentionChart from './MentionChart';
// import NewsList from './NewsList';
// import NewsSummary from './NewsSummary';
// import StackedColumns from './StackedColumns';
// import VideoList from './VideoList';
// import WordCloud from './Wordcloud';
// import { useMemo } from 'react';

// import {
//   words,
//   sentimentData,
//   mentionData,
//   newsData,
//   description,
//   videos,
// } from '../worldDetail/MockData';

// interface FirstCountryBoardProps {
//   country: string;
//   country_name: string;
//   keyword: string | string[];
//   category: string;
//   period: string;
// }

// const FirstCountryBoard = ({
//   country,
//   country_name,
//   keyword,
//   category,
//   period,
// }: FirstCountryBoardProps) => {
//   const memoizedParams = useMemo(
//     () => ({
//       country,
//       country_name,
//       keyword,
//       category,
//       period,
//       is_korea: country === 'kr',
//     }),
//     [country, country_name, keyword, category, period]
//   );

//   const { data, isLoading, error } = useCountryData(memoizedParams);

//   if (isLoading) return <div>Loading...</div>;
//   if (error) return <div>Error! {error.message}</div>;

//   // 서버 응답 없을 경우 목데이터로 대체
//   const safeData = data ?? {
//     keywords: words,
//     description: description,
//     sentimentData: sentimentData,
//     mentions: mentionData,
//     articles: newsData,
//     videos: videos,
//   };
const KoreaAnalysis = () => {
  return (
    <div>
      <div>한국 특화 분석 페이지</div>;

    </div>
  );
};

export default KoreaAnalysis;

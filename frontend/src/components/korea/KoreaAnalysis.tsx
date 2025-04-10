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
  console.log('KoreaAnalysis data:', data);

  const {
    data: gpt_data,
    isLoading: isLoading_GPT,
    error: error_GPT,
  } = useDescription(memoizedParams);

  if (isLoading || !data) return <GlobalSpinner />;
  if (error)
    return (
      <div className="text-center mt-10 text-system-danger">
        오류가 발생했습니다: {error.message}
      </div>
    );

  const description = gpt_data || '뉴스 요약을 불러오는 중입니다...🔥';

  const isAllDataEmpty =
    data.keywords?.length === 0 &&
    data.sentimentData?.length === 0 &&
    data.mentions?.length === 0 &&
    data.articles?.length === 0 &&
    data.videos?.length === 0;

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
            keywords={data.keywords}
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
              data={data.sentimentData}
              width={450}
              height={250}
              keyword={keyword}
              keyword_mind={keyword_mind}
              country_name={country_name}
              country_code={'KR'}
            />
            <MentionChart
              data={data.mentions}
              width={450}
              height={250}
              keyword={keyword}
              keyword_mind={keyword_mind}
              country_name={country_name}
              country_code={'KR'}
            />
          </div>
          <NewsList
            news={data.articles}
            width={900}
            keyword={keyword}
            keyword_mind={keyword_mind}
            country_name={country_name}
            country_code={'KR'}
            handleModalOpen={handleModalOpen}
          />
          <KoreaVideoList
            videos={data.videos}
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

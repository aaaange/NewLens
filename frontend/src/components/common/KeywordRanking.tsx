import { useState, useEffect } from 'react';
import { getKeywordRankingApi } from '../../services/api/worldService';

interface Keyword {
  id: number;
  text: string;
  tag: string | null;
  tagColor: string;
}

interface PropsType {
  category: string;
  period: number;
  is_korea: boolean;
  is_real_time?: boolean;
  onKeywordChange: (keyword: string) => void;
  handleMindMapKeywordChange: (keyword: string) => void;
  handleInitKeywordChange: (keyword: string) => void;
  fetchWorldData: (keyword: string, mind: string) => void; // 추가된 prop
  initDetail: boolean;
  onFirstRankingChange: (keyword: string) => void; // 추가된 prop
  isCategorySelected: boolean;
  setIsCategorySelected: (value: boolean) => void;
  isPeriodSelected: boolean;
  setIsPeriodSelected: (value: boolean) => void;
}

const KeywordRanking = ({
  category,
  period,
  is_korea,
  is_real_time,
  onKeywordChange,
  handleMindMapKeywordChange,
  handleInitKeywordChange,
  fetchWorldData,
  onFirstRankingChange,
  initDetail,
  isCategorySelected,
  setIsCategorySelected,
  isPeriodSelected,
  setIsPeriodSelected,
}: PropsType) => {
  const [keywords, setKeywords] = useState<Keyword[]>([]);
  const [currentTime] = useState(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const ampm = now.getHours() >= 12 ? '오후' : '오전';
    const hour12 = now.getHours() % 12 || 12;

    if (is_real_time) {
      // `isKorea === true`일 때 분 제외
      return `${year}.${month}.${day} ${ampm} ${String(hour12).padStart(2, '0')}시 기준`;
    } else {
      // 기본 형식
      const minutes = String(now.getMinutes()).padStart(2, '0');
      return `${year}.${month}.${day} ${ampm} ${String(hour12).padStart(2, '0')}:${minutes}`;
    }
  });

  const handleKeywordClick = (keyword: string) => {
    onKeywordChange(keyword);
    handleMindMapKeywordChange('');
  };

  const fetchKeywordRanking = async () => {
    try {
      const response = await getKeywordRankingApi(category, period, is_korea);
      const { keywords: apiKeywords } = response.data;
      if (!initDetail && (!isPeriodSelected || isCategorySelected)) {
        handleInitKeywordChange(apiKeywords[0].name); // 기간 1일 선택 시 data가 빈 배열로 넘어올 경우 대비
      }
      if (isPeriodSelected) setIsPeriodSelected(false);
      if (isCategorySelected) setIsCategorySelected(false);
      if (apiKeywords.length > 0) {
        onFirstRankingChange(apiKeywords[0].name); // 첫 번째 키워드 변경
      }
      const newKeywords: Keyword[] = apiKeywords.map(
        (keyword: { name: string; state: string }, index: number) => {
          let tagColor = '';
          if (keyword.state === 'new') {
            tagColor = 'text-blue-400';
          } else if (keyword.state === 'hot') {
            tagColor = 'text-red-600';
          }

          return {
            id: index + 1,
            text: keyword.name,
            tag: keyword.state || null,
            tagColor,
          };
        }
      );

      setKeywords(newKeywords);
    } catch (error) {
      console.error('키워드 랭킹 데이터 가져오기 실패:', error);
    }
  };

  useEffect(() => {
    fetchKeywordRanking();
  }, [category, period, is_korea]);

  return (
    <div className="bg-white rounded-[20px] shadow-md w-[305px] p-6">
      <div className="flex flex-col">
        {/* 헤더 */}
        <div className="mb-4">
          <h2 className="text-lg font-semibold text-black mb-1">
            실시간 인기 키워드
          </h2>
          <div className="flex justify-between items-center">
            <p className="caption-small text-tetiary-500">
              실시간으로 가장 핫한 키워드!
            </p>
            <p className="caption-small text-tetiary-500">{currentTime}</p>
          </div>
        </div>

        {/* 키워드 목록 */}
        <div className="flex flex-col">
          {keywords.length === 0 ? (
            <div className="text-center p-4 text-tetiary-500 caption-small">
              해당 카테고리의 키워드 랭킹이 없습니다!
            </div>
          ) : (
            keywords.map((keyword) => (
              <button
                key={keyword.id}
                className="cursor-pointer flex items-center group hover:bg-gray-50 py-1 px-1 rounded transition-colors"
                onClick={() => handleKeywordClick(keyword.text)}
              >
                <span
                  className={`w-5 h-5 text-center mr-2.5 ${
                    keyword.id <= 3
                      ? 'text-slate-600 font-bold'
                      : 'text-gray-500 font-normal'
                  }`}
                >
                  {keyword.id}
                </span>
                <span
                  className={`${
                    keyword.id <= 3
                      ? 'text-gray-600 font-bold'
                      : 'text-gray-500 font-normal'
                  } group-hover:text-black`}
                >
                  {keyword.text}
                </span>
                {keyword.tag && (
                  <div className="ml-2.5 px-2 py-1 bg-gray-100 rounded-full h-4 flex items-center">
                    <span className={`text-xs ${keyword.tagColor}`}>
                      {keyword.tag}
                    </span>
                  </div>
                )}
              </button>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default KeywordRanking;

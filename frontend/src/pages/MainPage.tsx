import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import SearchInput from '../components/common/SearchInput';
import Map from '../components/world/Map';
import { debounce } from 'lodash';

import { useEffect, useState } from 'react';
import { getWorldMapDataApi } from '../services/api/worldService';

const MainPage = () => {
  //==============================================
  // 지도 토글 버튼 관련
  //==============================================
  const [activeTab, setActiveTab] = useState('mention');

  const tabs = [
    { id: 'mention', label: '언급량' },
    { id: 'sentiment', label: '긍부정' },
  ];

  const clickTab = (tabId: string) => {
    setActiveTab(tabId);
  };

  //==============================================
  // 지도에 넘겨줄 데이터
  //==============================================
  const [category, setCategory] = useState('all');
  const [period, setPeriod] = useState(30);
  const [keyword, setKeyword] = useState('');
  const [debouncedKeyword, setDebouncedKeyword] = useState('');
  const [keyword_mind, setKeywordMind] = useState('');
  const [mapData, setMapData] = useState(null);

  const categoryChangeHandler = (category: string) => {
    setCategory(category);
  };
  const periodChangeHandler = (period: number) => {
    setPeriod(period);
  };

  const keywordInputChangeHandler = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setKeyword(e.target.value);
  };

  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeywordMind(newKeyword);
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
    setKeyword(newKeyword);
  };

  const fetchWorldData = async (mind: string) => {
    try {
      if (debouncedKeyword.length == 1) {
        setDebouncedKeyword('');
        return;
      }
      const params = {
        category: category,
        period: period,
        keyword: debouncedKeyword,
        keyword_mind: mind ? mind : keyword_mind,
      };
      const response = await getWorldMapDataApi(params);
      setMapData(response.data);
      setKeyword(response.data.keyword); // 조건 없으면 무한 루프
      console.log('reponse', response.data);
    } catch (error) {
      console.error('검색 실패:', error);
    }
  };
  // Debounce 처리
  useEffect(() => {
    const handler = debounce(() => {
      setDebouncedKeyword(keyword);
    }, 500); // 500ms 딜레이 설정
    handler();
    return () => handler.cancel(); // cleanup
  }, [keyword]);

  // debouncedKeyword가 변경될 때만 fetchWorldData 호출
  useEffect(() => {
    fetchWorldData('');
  }, []);

  useEffect(() => {
    if (debouncedKeyword) {
      fetchWorldData('');
    }
  }, [debouncedKeyword, category, period]);

  const headerString = [keyword, keyword_mind]
    .filter((item) => item && item.trim() !== '') // 빈 문자열 또는 undefined/null 제거
    .join(' > '); // ' > '로 연결

  return (
    <div className="mt-5 flex gap-20 justify-center overflow-hidden pb-[32px]">
      <div className="flex flex-col gap-5">
        <SearchInput
          value={keyword}
          onChange={keywordInputChangeHandler}
          onSearch={fetchWorldData}
        />
        <MindMap
          fetchWorldData={fetchWorldData}
          keyword_mind={keyword_mind}
          onKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          mainKeyword={keyword}
          isKorea={false}
        />
        <KeywordRanking
          handleMindMapKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          is_korea={false}
          onKeywordChange={handleRankingKeywordChange}
          handleInitKeywordChange={() => {}}
        />
      </div>
      <div className="flex flex-col items-end">
        <Category
          isCategory={category}
          isPeriod={period}
          categoryChangeHandler={categoryChangeHandler}
          periodChangeHandler={periodChangeHandler}
        />
        {/* 토글 버튼에 따른 세계 지도 렌더링 */}
        <div className="flex items-center justify-between w-full mt-[40px] mb-[10px]">
          <div className="flex items-center">
            {headerString && (
              <img
                src="/assets/images/newsicon.png"
                className="mr-2"
                alt="Earth Globe"
                width={30}
                height={30}
              />
            )}

            <span className="text-amount-300 headline-large">
              {headerString}
            </span>
            {headerString && (
              <span className="caption-large text-gray-0 ml-2">
                에 대한 분석 결과입니다.
              </span>
            )}
          </div>

          <div className="w-[150px] h-[40px] flex justify-between rounded-[20px] overflow-hidden bg-primary-300 p-1">
            {tabs.map((tab) => (
              <div
                key={tab.id}
                className={`flex flex-1 justify-center items-center cursor-pointer caption-medium text-black ${
                  activeTab === tab.id
                    ? 'flex justify-center items-center w-[70px] rounded-4xl bg-gray-0'
                    : 'text-gray-0'
                }`}
                onClick={() => clickTab(tab.id)}
              >
                {tab.label}
              </div>
            ))}
          </div>
        </div>

        <div>
          <Map
            tabId={activeTab}
            keyword={keyword}
            keyword_mind={keyword_mind}
            category={category}
            period={period}
            mapData={mapData || undefined}
          />
        </div>
      </div>
    </div>
  );
};

export default MainPage;

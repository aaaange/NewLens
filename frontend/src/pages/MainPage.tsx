import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import SearchInput from '../components/common/SearchInput';
import Map from '../components/world/Map';

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

  const fetchWorldData = async () => {
    try {
      const params = {
        category: category,
        period: period,
        keyword: keyword,
        keyword_mind: keyword_mind,
      };
      const response = await getWorldMapDataApi(params);
      setMapData(response.data);
      setKeyword(response.data.keyword);
      console.log('reponse', response.data);
    } catch (error) {
      console.error('검색 실패:', error);
    }
  };

  useEffect(() => {
    fetchWorldData();
  }, [keyword, keyword_mind, category, period]);

  return (
    <div className="mt-5 flex gap-20 justify-center overflow-hidden">
      <div className="flex flex-col gap-5">
        <SearchInput
          value={keyword}
          onChange={keywordInputChangeHandler}
          onSearch={fetchWorldData}
        />
        <MindMap
          onKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          mainKeyword={keyword}
          isKorea={false}
        />
        <KeywordRanking
          category={category}
          period={period}
          is_korea={false}
          onKeywordChange={handleRankingKeywordChange}
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

        <div className="w-[150px] h-[40px] flex justify-between rounded-[20px] overflow-hidden bg-primary-300 p-1 mt-[40px] mb-[10px] ">
          {tabs.map((tab) => (
            <div
              key={tab.id}
              className={`flex flex-1 justify-center items-center cursor-pointer caption-medium text-black ${activeTab === tab.id ? 'flex justify-center items-center w-[70px] rounded-4xl bg-gray-0' : ' text-gray-0'}`}
              onClick={() => clickTab(tab.id)}
            >
              {tab.label}
            </div>
          ))}
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

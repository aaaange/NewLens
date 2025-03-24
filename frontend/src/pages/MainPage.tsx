import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import NewsModal from '../components/common/NewsModal';
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
  const [period, setPeriod] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [mapData, setMapData] = useState(null);

  const categoryChangeHandler = (category: string) => {
    setCategory(category);
    console.log(category);
  };
  const periodChangeHandler = (period: number) => {
    setPeriod(period);
    console.log(period);
  };

  const keywordInputChangeHandler = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setKeyword(e.target.value);
    console.log(e.target.value);
  };

  // const fetchWorldData = async () => {
  //   try {
  //     const response = await getWorldMapDataApi(category, period, keyword);
  //     setMapData(response.data);
  //     console.log(response.data);
  //   } catch (error) {
  //     console.error('검색 실패:', error);
  //   }
  // };
  const [json, setJson] = useState();
  const fetchWorldData = async () => {
    try {
      const response = await fetch('/worldData.json');
      const jsonData = await response.json();
      setJson(jsonData);
      console.log(jsonData);
    } catch (error) {
      console.error('검색 실패:', error);
    }
  };

  useEffect(() => {
    fetchWorldData();
  }, [mapData]);

  return (
    <div className="flex px-[100px] py-[40px] justify-center">
      <div className="flex flex-col gap-[20px] mr-[150px]">
        <SearchInput
          onChange={keywordInputChangeHandler}
          onSearch={fetchWorldData}
        />
        <MindMap />
        <KeywordRanking />
      </div>
      <div className="flex flex-col items-end">
        <Category
          categoryChangeHandler={categoryChangeHandler}
          periodChangeHandler={periodChangeHandler}
        />
        {/* 토글 버튼에 따른 세계 지도 렌더링 */}

        <div className="w-[150px] h-[40px] flex justify-between rounded-[20px] overflow-hidden bg-primary-300 p-1 mt-[40px] mb-[10px]">
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
            // keyword="테스트"
            category={category}
            period={period}
            // mapData={mapData}
            mapData={json}
          />
        </div>
      </div>

      {/* <div>
        <NewsModal />
      </div> */}
    </div>
  );
};

export default MainPage;

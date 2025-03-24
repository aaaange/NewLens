import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import NewsModal from '../components/common/NewsModal';
import SearchInput from '../components/common/SearchInput';

import WorldMap from '../components/world/Map';

import { useState } from 'react';

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
  const [category, setCategory] = useState('전체');
  const [period, setPeriod] = useState(1);
  const [keyword, setKeyword] = useState('');
  return (
    <div className="flex px-[100px] py-[40px] justify-between">
      <div className="flex flex-col gap-[20px]">
        <SearchInput />
        <MindMap />
        <KeywordRanking />
      </div>
      <div className="flex flex-col gap-[40px] items-end">
        <Category />
        {/* 토글 버튼에 따른 세계 지도 렌더링 */}

        <div className="w-[150px] h-[40px] flex justify-between rounded-[20px] overflow-hidden bg-primary-300 p-1">
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
        <WorldMap
          tabId={activeTab}
          category={category}
          period={period}
          keyword={keyword}
        />
      </div>

      {/* <div>
        <NewsModal />
      </div> */}
    </div>
  );
};

export default MainPage;

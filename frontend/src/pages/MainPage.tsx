import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import NewsModal from '../components/common/NewsModal';
import SearchInput from '../components/common/SearchInput';

import WorldMap from '../components/world/Map';
// import MapSwitchTab from '../components/world/MapSwitchTab';
import { useState } from 'react';

const MainPage = () => {
  const [activeTab, setActiveTab] = useState('mention');

  const tabs = [
    { id: 'mention', label: '언급량' },
    { id: 'sentiment', label: '긍부정' },
  ];

  const clickTab = (tabId: string) => {
    setActiveTab(tabId);
  };
  return (
    <div className="">
      <div>
        <SearchInput />
      </div>
      <div>
        <MindMap />
      </div>
      <div>
        <Category />
      </div>
      <div>
        <KeywordRanking />
      </div>
      <div>
        <NewsModal />
      </div>

      {/* 토글 버튼 */}
      <div className="w-[150px] h-[30px] flex justify-between rounded-4xl overflow-hidden">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            className={`flex-1 cursor-pointer text-black ${activeTab === tab.id ? 'bg-yellow-400' : 'bg-white text-gray-300'}`}
            onClick={() => clickTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* 토글 버튼에 따른 세계 지도 렌더링 */}
      <WorldMap />
    </div>
  );
};

export default MainPage;

import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import NewsModal from '../components/common/NewsModal';
import SearchInput from '../components/common/SearchInput';

import WorldMap from '../components/world/Map';
import MapSwitchTab from '../components/world/MapSwitchTab';
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
      <div>
        {/* 토글 버튼에 따른 세계 지도 렌더링 */}

        <div className="w-[150px] h-[50px] flex justify-between rounded-[20px] overflow-hidden border border-white p-1.5">
          {tabs.map((tab) => (
            <div
              key={tab.id}
              className={`flex flex-1 justify-center items-center cursor-pointer text-black ${activeTab === tab.id ? 'flex justify-center items-center w-[70px] rounded-4xl bg-yellow-400' : ' text-gray-200'}`}
              onClick={() => clickTab(tab.id)}
            >
              {tab.label}
            </div>
          ))}
        </div>
        <WorldMap tabId={activeTab} />
      </div>
    </div>
  );
};

export default MainPage;

import { useState } from 'react';

const MapSwitchTab = () => {
  const [activeTab, setActiveTab] = useState('mention');

  const tabs = [
    { id: 'mention', label: '언급량' },
    { id: 'sentiment', label: '긍부정' },
  ];

  const clickTab = (tabId: string) => {
    setActiveTab(tabId);
  };

  return (
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
  );
};

export default MapSwitchTab;

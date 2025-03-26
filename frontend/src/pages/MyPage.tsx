import { useState } from 'react';

import ClippingNews from '../components/mypage/ClippingNews';
import MyActivities from '../components/mypage/MyActivities';
import RecommendedArticle from '../components/mypage/RecommendedArticle';

const MyPage = () => {
  const [selectedTab, setSelectedTab] = useState('activities');

  return (
    <div className="flex m-15 gap-10">
      <div className="flex flex-col border-r-1 border-gray-300 w-1/4 gap-20">
        <h1 className="text-5xl">마이페이지</h1>
        <div className="flex flex-col text-2xl gap-5">
          <div
            className="transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
            onClick={() => setSelectedTab('activities')}
          >
            <span
              className={`inline-block ${
                selectedTab === 'activities' ? 'border-b' : ''
              }`}
            >
              나의 활동
            </span>
          </div>
          <div
            className="transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
            onClick={() => setSelectedTab('recommended')}
          >
            <span
              className={`inline-block ${
                selectedTab === 'recommended' ? 'border-b' : ''
              }`}
            >
              추천 기사
            </span>
          </div>
          <div
            className="transition-transform duration-300 ease-in-out hover:scale-105 cursor-pointer"
            onClick={() => setSelectedTab('clipping')}
          >
            <span
              className={`inline-block ${
                selectedTab === 'clipping' ? 'border-b' : ''
              }`}
            >
              스크랩 NEWS
            </span>
          </div>
        </div>
      </div>
      <div className="">
        {selectedTab === 'activities' && <MyActivities />}
        {selectedTab === 'recommended' && <RecommendedArticle />}
        {selectedTab === 'clipping' && <ClippingNews />}
      </div>
    </div>
  );
};

export default MyPage;

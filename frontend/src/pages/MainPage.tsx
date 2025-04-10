import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import SearchInput from '../components/common/SearchInput';
import Map from '../components/world/Map';
import { debounce, set } from 'lodash';

import { useEffect, useRef, useState } from 'react';
import { getWorldMapDataApi } from '../services/api/worldService';
import { useAuthStore } from '../stores/useAuthStore';
import { useLocation, useNavigate } from 'react-router';
import GlobalSpinner from '../components/common/GlobalSpinner';
import { is } from '@babel/types';

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
  const [firstRanking, setFirstRanking] = useState('');
  const [mapData, setMapData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [isPeriodSelected, setIsPeriodSelected] = useState<boolean>(false);
  const [isCategorySelected, setIsCategorySelected] = useState(false);

  const categoryChangeHandler = (category: string) => {
    setCategory(category);
    setKeywordMind('');
    setDebouncedKeyword('');
  };
  const periodChangeHandler = (period: number) => {
    setPeriod(period);
  };

  const [isText, setIsText] = useState(false);
  const keywordInputChangeHandler = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setKeyword(e.target.value);
    setIsText(true);
  };

  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeywordMind(newKeyword);
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
    setKeyword(newKeyword);
  };

  const isFirstRender = useRef(true);
  const fetchWorldData = async (keyword: string, mind: string) => {
    setLoading(true);
    try {
      if (debouncedKeyword.length == 1) {
        setDebouncedKeyword('');
        return;
      }
      const params = {
        category: category,
        period: period,
        keyword: keyword != '' ? keyword : debouncedKeyword,
        keyword_mind: mind,
      };

      const response = await getWorldMapDataApi(params);
      setMapData(response.data);
      if (response.data.keyword !== keyword) {
        setKeyword(response.data.keyword);
      }
    } catch (error) {
      console.error('검색 실패:', error);
    } finally {
      setLoading(false);
      isFirstRender.current = false;
    }
  };
  console.log(keyword_mind);

  useEffect(() => {
    const handler = debounce(() => {
      setDebouncedKeyword(keyword);
    }, 500);
    handler();
    return () => handler.cancel();
  }, [keyword]);

  useEffect(() => {
    setKeywordMind('');
  }, [debouncedKeyword]);

  useEffect(() => {
    // if (!debouncedKeyword || isFirstRender.current) return;
    // fetchWorldData(debouncedKeyword, keyword_mind);
    if (isFirstRender.current == true) {
      fetchWorldData('', '');
    }
  }, []);

  useEffect(() => {
    if (debouncedKeyword && isFirstRender.current == false) {
      fetchWorldData('', keyword_mind);
    }
  }, [debouncedKeyword, category, period]);

  const headerString = [keyword, keyword_mind]
    .filter((item) => item && item.trim() !== '')
    .join(' > ');

  const location = useLocation();
  const navigate = useNavigate();
  const setAccessToken = useAuthStore((state) => state.setAccessToken);

  useEffect(() => {
    const query = new URLSearchParams(location.search);
    const urlAccessToken = query.get('accessToken');

    if (urlAccessToken) {
      // Zustand와 localStorage에 저장
      setAccessToken(urlAccessToken);

      // URL에서 토큰 제거
      navigate('/main', { replace: true });
    }
  }, [location, setAccessToken, navigate]);

  const handleFirstRanking = (keyword: string) => {
    setFirstRanking(keyword);
  };

  return (
    <div className="mt-5 flex gap-20 justify-center overflow-hidden pb-[32px]">
      <div className="flex flex-col gap-5">
        <SearchInput
          value={keyword}
          onChange={keywordInputChangeHandler}
          onSearch={fetchWorldData}
          onKeyDown={() => {}}
        />
        <MindMap
          fetchWorldData={fetchWorldData}
          keyword_mind={keyword_mind}
          onKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          mainKeyword={debouncedKeyword}
          isKorea={false}
        />
        <KeywordRanking
          initDetail={false}
          fetchWorldData={fetchWorldData}
          handleMindMapKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          is_korea={false}
          onKeywordChange={handleRankingKeywordChange}
          isCategorySelected={isCategorySelected}
          setIsCategorySelected={setIsCategorySelected}
          isPeriodSelected={isPeriodSelected}
          setIsPeriodSelected={setIsPeriodSelected}
          handleInitKeywordChange={handleRankingKeywordChange}
          onFirstRankingChange={handleFirstRanking}
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
        <div className="flex items-center justify-between w-full mt-[40px] mb-[10px] min-h-12">
          <div className="items-center flex ">
            <div>
              {headerString && (
                <img
                  src="/assets/images/newsicon.png"
                  className="mr-2"
                  alt="Earth Globe"
                  width={50}
                  height={50}
                />
              )}
            </div>
            <div>
              {firstRanking == keyword && (
                <div className="body-small text-gray-0">
                  실시간 가장 핫한 키워드!
                </div>
              )}
              <div className="flex items-center">
                <span className="text-amount-300 headline-xlarge">
                  {headerString}
                </span>
                {headerString && (
                  <span className="headline-small text-gray-0 ml-2">
                    에 대한 분석 결과입니다.
                  </span>
                )}
              </div>
            </div>
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

        {loading ? (
          <div className="flex justify-center items-center w-full h-4/8">
            <GlobalSpinner />
          </div>
        ) : (
          <div className="h-full">
            <Map
              tabId={activeTab}
              keyword={keyword}
              keyword_mind={keyword_mind}
              category={category}
              period={period}
              mapData={mapData || undefined}
            />
          </div>
        )}
      </div>
    </div>
  );
};

export default MainPage;

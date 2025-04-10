import { useState, useEffect } from 'react';
import CountryDropdown from '../components/worldDetail/CountryDropdown';
import GptSummary from '../components/worldDetail/GptSummary';
import FirstCountryBoard from '../components/worldDetail/FirstCountryDashboard';
import SecondCountryBoard from '../components/worldDetail/SecondCountryDashboard';
import SearchInput from '../components/common/SearchInput';
import MindMap from '../components/common/MindMap';
import KeywordRanking from '../components/common/KeywordRanking';
import Category from '../components/common/Category';
import { useParams } from 'react-router-dom';
import { getCountryName } from '../utils/countryUtils';
import useCompareInfo from '../hooks/useCompareInfo';
import NewsModal from '../components/common/NewsModal';

const WorldDetail = () => {
  const {
    country,
    category: initialCategory,
    period: initialPeriod,
    keyword: initialKeyword,
    keyword_mind: initialKeywordMind,
  } = useParams();
  const upperCaseCountry = (country ?? '').toUpperCase();
  const getInitialCountry = () => {
    const saved = localStorage.getItem('firstCountry');
    return saved ?? (country ?? '').toUpperCase();
  };

  const [isReady, setIsReady] = useState(false);

  const [firstCountry, setFirstCountry] = useState(getInitialCountry());
  const [firstCountryName, setFirstCountryName] = useState(
    getCountryName(getInitialCountry())
  );

  const [secondCountry, setSecondCountry] = useState('');
  const [secondCountryName, setSecondCountryName] = useState('');

  const [category, setCategory] = useState(initialCategory ?? 'all');
  const [period, setPeriod] = useState(
    initialPeriod ? parseInt(initialPeriod) : 1
  );
  const [keyword, setKeyword] = useState(initialKeyword ?? '');

  const [keyword_mind, setKeywordMind] = useState(initialKeywordMind ?? '');
  const [keyword_cloud, setKeywordCloud] = useState('');

  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const [inputKeyword, setInputKeyword] = useState('');
  const [apiKeyword, setApiKeyword] = useState('');

  const shouldCallCompare =
    secondCountry !== '' && category && period && keyword;

  const { data, isLoading, error } = useCompareInfo(
    shouldCallCompare
      ? {
          category,
          period,
          keyword: keyword,
          keyword_mind: keyword_mind,
          country1: firstCountry,
          country2: secondCountry,
        }
      : null
  );

  const categoryChangeHandler = (category: string) => {
    setCategory(category);
    setKeywordMind('');
  };
  const periodChangeHandler = (period: number) => {
    setPeriod(period);
    setKeywordMind('');
  };

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      setApiKeyword(inputKeyword);
    }, 500);

    return () => clearTimeout(timeoutId);
  }, [inputKeyword]);

  const keywordInputChangeHandler = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setInputKeyword(e.target.value);
    setKeywordMind('');
  };

  const keywordInputKeyDownHandler = (
    e: React.KeyboardEvent<HTMLInputElement>
  ) => {
    if (e.key === 'Enter') {
      setKeyword(inputKeyword); // Enter 키로만 키워드 업데이트
    }
  };

  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeywordMind(newKeyword);
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
    setKeyword(newKeyword);
  };

  const handleInitKeywordChange = (newKeyword: string) => {
    setKeyword(newKeyword);
  };

  const handleWordCloudChange = (newKeyword: string) => {
    setKeywordCloud(newKeyword);
  };

  useEffect(() => {
    const savedFirst = localStorage.getItem('firstCountry');
    const savedSecond = localStorage.getItem('secondCountry');

    if (savedFirst) {
      setFirstCountry(savedFirst);
      setFirstCountryName(getCountryName(savedFirst));
    }
    if (savedSecond) {
      setSecondCountry(savedSecond);
      setSecondCountryName(getCountryName(savedSecond));
    }
  }, []);

  const handleFirstChange = (code: string) => {
    setFirstCountry(code);
    localStorage.setItem('firstCountry', code);
  };

  const handleSecondChange = (code: string) => {
    setSecondCountry(code);
    localStorage.setItem('secondCountry', code);
  };

  const [isModal, setIsModal] = useState(false);
  const [selectCountry, setSelectCountry] = useState('');
  const handleModalOpen = (country: string) => {
    setIsModal(true);
    setSelectCountry(country);
  };
  const handleModalClose = () => {
    setIsModal(false);
  };

  const [initDetail, setInitDetail] = useState(true);

  useEffect(() => {
    // 컴포넌트가 처음 렌더링될 때 실행
    if (initDetail) {
      setInitDetail(false);
    }
  }, []);

  useEffect(() => {
    const savedSecond = localStorage.getItem('secondCountry');

    if (savedSecond) {
      setSecondCountry(savedSecond);
      setSecondCountryName(getCountryName(savedSecond));
    }

    setIsReady(true); // 준비 완료 됐을 때만 진행
  }, []);

  return (
    <div className="flex mt-5 transition-all duration-500 ease-in-out w-full">
      {/* 버튼 */}
      <div className="flex flex-col items-start justify-start pt-1 px-4">
        <button
          className="w-10 h-10 cursor-pointer"
          onClick={() => setIsSidebarOpen((prev) => !prev)}
        >
          <img
            src={
              isSidebarOpen
                ? '/assets/images/slide_left.png'
                : '/assets/images/slide_right.png'
            }
            alt="사이드바 토글"
            className="w-10 h-10 m-auto"
          />
        </button>
      </div>

      {/* 사이드바 */}
      <div
        className={`transition-all duration-500 ease-in-out overflow-hidden
      ${isSidebarOpen ? 'w-[24rem] px-8 opacity-100' : 'w-0 px-0 opacity-0 pointer-events-none'}
      flex flex-col gap-5`}
      >
        <div className="flex items-center gap-2">
          <SearchInput
            value={keyword}
            onChange={keywordInputChangeHandler}
            onSearch={() => {}}
            onKeyDown={keywordInputKeyDownHandler}
          />
        </div>
        <MindMap
          keyword_mind={keyword_mind}
          fetchWorldData={() => {}}
          onKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          mainKeyword={keyword}
          isKorea={firstCountry === 'KR'}
        />
        <KeywordRanking
          fetchWorldData={() => {}}
          category={category}
          period={period}
          is_korea={false}
          onKeywordChange={handleRankingKeywordChange}
          handleMindMapKeywordChange={handleMindMapKeywordChange}
          handleInitKeywordChange={handleInitKeywordChange}
          initDetail={initDetail}
        />
      </div>

      {/* 메인 컨텐츠 */}
      <div
        className={`flex flex-col items-center gap-3 flex-grow transition-all duration-500 ease-in-out ${isSidebarOpen ? 'ml-[-3rem]' : 'ml-0'}`}
      >
        <Category
          isCategory={category}
          isPeriod={period}
          categoryChangeHandler={categoryChangeHandler}
          periodChangeHandler={periodChangeHandler}
        />
        <div className="flex gap-10">
          <CountryDropdown
            width="410px"
            height="60px"
            value={firstCountry}
            placeholder="비교할 나라를 선택하세요"
            onChange={(code, name) => {
              handleFirstChange(code);
              setFirstCountryName(name);
            }}
          />
          <CountryDropdown
            width="410px"
            height="60px"
            value={secondCountry}
            onChange={(code, name) => {
              handleSecondChange(code);
              setSecondCountryName(name);
            }}
          />
        </div>
        <GptSummary
          description={
            data?.analysis ??
            '비교할 나라를 선택해보세요! 양 측의 입장을 한 줄로 요약해요✨'
          }
          width={880}
          height={125}
          keyword={keyword}
        />
        <div className="flex flex-row gap-3 divide-x divide-gray-300 justify-between">
          {isReady && (
            <div className="p-5 min-w-[430px]">
              <FirstCountryBoard
                country={firstCountry}
                country_name={firstCountryName}
                keyword={keyword}
                keyword_mind={keyword_mind}
                category={category}
                period={period}
                handleWordCloudChange={handleWordCloudChange}
                handleModalOpen={handleModalOpen}
                handleModalClose={handleModalClose}
              />
            </div>
          )}

          <div className="p-5 w-[410px] min-h-[800px]">
            {secondCountry ? (
              <SecondCountryBoard
                country_name={secondCountryName}
                country={secondCountry}
                keyword={keyword}
                keyword_mind={keyword_mind}
                category={category}
                period={period}
                handleWordCloudChange={handleWordCloudChange}
                handleModalOpen={handleModalOpen}
                handleModalClose={handleModalClose}
              />
            ) : (
              <div className="flex items-center justify-center w-full h-full text-gray-400 "></div>
            )}
          </div>
        </div>
      </div>
      {isModal && (
        <NewsModal
          handleModalClose={handleModalClose}
          category={category}
          period={period}
          keyword={keyword}
          keyword_mind={keyword_mind}
          keyword_cloud={keyword_cloud}
          country={selectCountry}
          isKorea={false}
        />
      )}
    </div>
  );
};

export default WorldDetail;

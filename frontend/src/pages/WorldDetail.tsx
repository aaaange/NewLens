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

  const [firstCountry, setFirstCountry] = useState(upperCaseCountry);
  const [firstCountryName, setFirstCountryName] = useState(
    getCountryName(firstCountry)
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
  };

  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeywordMind(newKeyword);
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
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

  const [openMenu, setOpenMenu] = useState(false);
  const [isVisible, setIsVisible] = useState(false);

  const handleMenuOpen = () => {
    if (!openMenu) {
      setIsVisible(true); // 먼저 보여주고
      setOpenMenu(true); // 슬라이드 인
    } else {
      setOpenMenu(false); // 슬라이드 아웃
      setTimeout(() => setIsVisible(false), 300); // 트랜지션 끝나고 DOM 제거
    }
  };

  return (
    <div className="mt-5 flex gap-10 justify-center">
      <div className="flex flex-col gap-5">
        <SearchInput
          value={keyword}
          onChange={keywordInputChangeHandler}
          onSearch={() => console.log('Search triggered')}
        />
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
          category={category}
          period={period}
          is_korea={firstCountry === 'KR'}
          onKeywordChange={handleRankingKeywordChange}
          handleMindMapKeywordChange={handleMindMapKeywordChange}
        />
      </div>
      <div className="flex flex-col items-center gap-3">
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
          description={data?.analysis ?? '비교할 나라를 선택해보세요! 양 측의 입장을 한 줄로 요약해요✨'}
          width={880}
          height={125}
          keyword={keyword}
        />
        <div className="flex flex-row gap-3 divide-x divide-gray-300 justify-between">
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
        />
      )}
    </div>
  );
};

export default WorldDetail;

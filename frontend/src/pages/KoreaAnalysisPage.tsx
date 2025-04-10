import KoreaAnalysis from '../components/korea/KoreaAnalysis';
import SearchInput from '../components/common/SearchInput';
import MindMap from '../components/common/MindMap';
import KeywordRanking from '../components/common/KeywordRanking';
import Category from '../components/common/Category';
import { useState, useRef, useEffect } from 'react';

const KoreaAnalysisPage = () => {
  const [category, setCategory] = useState('all');
  const [period, setPeriod] = useState(30);
  const [keyword, setKeyword] = useState('');
  const [inputKeyword, setInputKeyword] = useState('');
  const [apiKeyword, setApiKeyword] = useState('');
  const [keyword_mind, setKeywordMind] = useState('');
  const [initialKeyword, setInitialKeyword] = useState('');
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isPeriodSelected, setIsPeriodSelected] = useState<boolean>(false);
  const [isCategorySelected, setIsCategorySelected] = useState(false);

  const country: string = 'kr';
  const country_name: string = '대한민국';

  const userTyped = useRef(false); // 사용자가 직접 입력했는지 여부

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      setApiKeyword(inputKeyword);
    }, 200);

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
      userTyped.current = false;
      console.log(inputKeyword);
    }
  };

  const onSearch = (keyword: string, mind: string) => {
    setKeyword(keyword);
    // setInitialKeyword(keyword);
    console.log(keyword);
    console.log('onSearch');
  };

  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeywordMind(newKeyword);
  };
  const handleInitKeywordChange = (newKeyword: string) => {
    if (!userTyped.current) {
      setKeyword(newKeyword);
      setInitialKeyword(newKeyword);
    }
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
    userTyped.current = false;
    setKeyword(newKeyword);
    setInitialKeyword(newKeyword);
  };
  const categoryChangeHandler = (category: string) => {
    setCategory(category);
    setKeywordMind('');
    setIsCategorySelected(true);
  };
  const periodChangeHandler = (period: number) => {
    setPeriod(period);
    // setKeywordMind('');
    setIsPeriodSelected(true);
  };

  useEffect(() => {
    const handleResize = () => {
      // 예: 사이드바(384px) + 메인 콘텐츠 최소 600px 필요 → 최소 984px
      if (window.innerWidth < 1400) {
        setIsSidebarOpen(false); // 공간 부족하면 사이드바 닫음
      }
    };

    window.addEventListener('resize', handleResize);
    handleResize(); // 처음에도 바로 확인

    return () => window.removeEventListener('resize', handleResize);
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
            onKeyDown={keywordInputKeyDownHandler}
            onChange={keywordInputChangeHandler}
            onSearch={onSearch}
          />
        </div>

        <MindMap
          keyword_mind={keyword_mind}
          fetchWorldData={() => {}}
          onKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          mainKeyword={keyword}
          isKorea={true}
        />
        <KeywordRanking
          fetchWorldData={() => {}}
          category={category}
          period={period}
          is_korea={true}
          onKeywordChange={handleRankingKeywordChange}
          handleMindMapKeywordChange={handleMindMapKeywordChange}
          handleInitKeywordChange={handleInitKeywordChange}
          initDetail={false}
          onFirstRankingChange={() => {}}
          isCategorySelected={isCategorySelected}
          setIsCategorySelected={setIsCategorySelected}
          isPeriodSelected={isPeriodSelected}
          setIsPeriodSelected={setIsPeriodSelected}
        />
      </div>

      {/* 메인 콘텐츠 영역 */}
      <div
        className={`flex flex-col items-center gap-3 flex-grow transition-all duration-500 ease-in-out ${isSidebarOpen ? 'ml-[-3rem]' : 'ml-0'}`}
      >
        <Category
          isCategory={category}
          isPeriod={period}
          categoryChangeHandler={categoryChangeHandler}
          periodChangeHandler={periodChangeHandler}
        />
        <KoreaAnalysis
          country={''}
          country_name={country_name}
          keyword={keyword}
          keyword_mind={keyword_mind ?? ''}
          category={category ?? ''}
          period={period ?? ''}
          is_korea={true}
        />
      </div>
    </div>
  );
};

export default KoreaAnalysisPage;

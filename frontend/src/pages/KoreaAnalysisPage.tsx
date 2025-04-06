import KoreaAnalysis from '../components/korea/KoreaAnalysis';
import SearchInput from '../components/common/SearchInput';
import MindMap from '../components/common/MindMap';
import KeywordRanking from '../components/common/KeywordRanking';
import Category from '../components/common/Category';
import { useState } from 'react';

const KoreaAnalysisPage = () => {
  const [category, setCategory] = useState('all');
  const [period, setPeriod] = useState(30);
  const [keyword, setKeyword] = useState('');
  const [keyword_mind, setKeywordMind] = useState('');
  const [initialKeyword, setInitialKeyword] = useState('');
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const country: string = 'kr';
  const country_name: string = '대한민국';

  const keywordInputChangeHandler = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setKeyword(e.target.value);
  };
  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeywordMind(newKeyword);
  };
  const handleInitKeywordChange = (newKeyword: string) => {
    setInitialKeyword(newKeyword);
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
    setKeyword(newKeyword);
    setInitialKeyword(newKeyword);
  };
  const categoryChangeHandler = (category: string) => {
    setCategory(category);
  };
  const periodChangeHandler = (period: number) => {
    setPeriod(period);
  };

  return (
    <div className="relative flex gap-10 mt-5 transition-all justify-center duration-500 ease-in-out">
      {/* 사이드바 */}
      <div
        className={`
        overflow-hidden transition-all duration-500 ease-in-out
        ${isSidebarOpen ? 'w-[20rem] mr-6' : 'w-0 mr-0'}
      `}
      >
        <div
          className={`
          transition-all duration-500 ease-in-out
          ${isSidebarOpen ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-10 pointer-events-none'}
          flex flex-col gap-5
        `}
        >
          <SearchInput
            value={initialKeyword}
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
            isKorea={true}
          />
          <KeywordRanking
            category={category}
            period={period}
            is_korea={true}
            onKeywordChange={handleRankingKeywordChange}
            handleMindMapKeywordChange={handleMindMapKeywordChange}
            handleInitKeywordChange={handleInitKeywordChange}
          />
        </div>
      </div>

      {/* 토글 버튼 */}
      <button
        className={`
        absolute top-2 z-10 bg-white text-black rounded px-2 py-1 shadow
        transition-all duration-500 ease-in-out
        ${isSidebarOpen ? 'left-[1rem]' : 'left-2'}
      `}
        onClick={() => setIsSidebarOpen((prev) => !prev)}
      >
        {isSidebarOpen ? '←' : '→'}
      </button>

      {/* 메인 콘텐츠 */}
      <div className="flex flex flex-col items-center justify-center gap-3 transition-all duration-500 ease-in-out">
        <Category
          isCategory={category}
          isPeriod={period}
          categoryChangeHandler={categoryChangeHandler}
          periodChangeHandler={periodChangeHandler}
        />
        <KoreaAnalysis
          country={''}
          country_name={country_name}
          keyword={initialKeyword ?? keyword}
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

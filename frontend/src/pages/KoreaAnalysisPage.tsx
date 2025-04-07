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
        absolute left-22 overflow-hidden transition-all duration-500 ease-in-out
        ${isSidebarOpen ? 'w-[21rem]' : 'w-0'}
      `}
      >
        <div
          className={`
          transition-all duration-500 ease-in-out
          ${isSidebarOpen ? 'opacity-100 px-4' : 'opacity-0 px-0 py-0 pointer-events-none'}
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
        absolute  z-10 text-black rounded shadow
        transition-all duration-500 ease-in-out
        ${isSidebarOpen ? 'left-[1rem] px-2' : 'left-2 px-20'}
      `}
        onClick={() => setIsSidebarOpen((prev) => !prev)}
      >
        {isSidebarOpen ? (
          <img
            src="/assets/images/curtain_left_expand_left.png"
            alt=""
            className="w-12 h-12"
          />
        ) : (
          <img
            src="/assets/images/curtain_right_expand_right.png"
            alt=""
            className="w-12 h-12"
          />
        )}
      </button>

      {/* 메인 콘텐츠 */}
      <div
        className={`flex flex-col items-center  gap-3 transition-all duration-500 ease-in-out ${isSidebarOpen ? 'ml-[23rem]' : 'ml-0'}`}
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

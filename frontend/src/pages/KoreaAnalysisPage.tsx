import KoreaAnalysis from '../components/korea/KoreaAnalysis';
import SearchInput from '../components/common/SearchInput';
import MindMap from '../components/common/MindMap';
import KeywordRanking from '../components/common/KeywordRanking';
import Category from '../components/common/Category';
import { useParams, useOutletContext } from 'react-router-dom';
import { useState } from 'react';

const KoreaAnalysisPage = () => {
  const [category, setCategory] = useState('all');
  const [period, setPeriod] = useState(30);
  const [keyword, setKeyword] = useState('');
  const [keyword_mind, setKeywordMind] = useState('');
  const [initialKeyword, setInitialKeyword] = useState('');

  const country: string = 'kr';
  const country_name: string = '대한민국';
  // const keyword: string = '임시 데이터';

  // const [keyword, setKeyword] = useState(initialKeyword ?? '');
  // const [keyword_mind, setKeywordMind] = useState(initialKeywordMind ?? '');

  // const [category, setCategory] = useState(initialCategory ?? 'all');
  // const [period, setPeriod] = useState(
  //   initialPeriod ? parseInt(initialPeriod) : 1
  // );
  // const upperCaseCountry = (country ?? '').toUpperCase();
  // const [firstCountry, setFirstCountry] = useState(upperCaseCountry);

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
    console.log(category);
  };

  const periodChangeHandler = (period: number) => {
    setPeriod(period);
    console.log(period);
  };

  return (
    <div className="mt-5 flex gap-10 justify-center">
      <div className="flex flex-col gap-5">
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
      <div className="flex flex-col items-center gap-3">
        <Category
          isCategory={category}
          isPeriod={period}
          categoryChangeHandler={categoryChangeHandler}
          periodChangeHandler={periodChangeHandler}
        />
        <KoreaAnalysis
          country={''}
          country_name={country_name}
          keyword={initialKeyword ?? keyword} // undefined 방지
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

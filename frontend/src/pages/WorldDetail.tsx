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

const description =
  "봄꽃이 개화하는 시기에 국내 여행객들이 가장 많이 찾는 여행지가 '제주도'라는 조사 결과가 나왔다. 12일 글로벌 여행 플랫폼 트립닷컴은 오는 25일~다음 달 30일 국내 여행객의 여행 추이를 공개했다. 제주시와 서귀포시가 1, 2위에 올랐다 지난해는 반대로 서귀포시가 1위, 제주시가 2위였다. 다음으로는 서울과 부산이 뒤를 이었다.";

const analysis = '한줄 비교 요약본 from gpt';

const WorldDetail = () => {
  const {
    country,
    category: initialCategory,
    period: initialPeriod,
    keyword: initialKeyword,
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

  const shouldCallCompare =
    secondCountry !== '' && category && period && keyword;

  const { data, isLoading, error } = useCompareInfo(
    shouldCallCompare
      ? {
          category,
          period,
          keyword: [keyword],
          country: [firstCountry, secondCountry],
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
    console.log(e.target.value);
  };

  const handleMindMapKeywordChange = (newKeyword: string) => {
    setKeyword(`${keyword.split(' ')[0]} ${newKeyword}`);
  };
  const handleRankingKeywordChange = (newKeyword: string) => {
    setKeyword(newKeyword);
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

  return (
    <div className="mt-5 flex gap-10 justify-center">
      <div className="flex flex-col gap-5">
        <SearchInput
          value={keyword}
          onChange={keywordInputChangeHandler}
          onSearch={() => console.log('Search triggered')}
        />
        <MindMap
          onKeywordChange={handleMindMapKeywordChange}
          category={category}
          period={period}
          mainKeyword={keyword}
        />
        <KeywordRanking
          category={category}
          period={period}
          is_korea={firstCountry === 'KR'}
          onKeywordChange={handleRankingKeywordChange}
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
          description={data?.analysis ?? 'GPT 요약 정보가 없습니다.'}
          width={880}
          height={125}
        />
        <div className="flex flex-row gap-3 divide-x divide-gray-300 justify-between">
          <div className="p-5 min-w-[430px]">
            <FirstCountryBoard
              country={firstCountry}
              country_name={firstCountryName}
              keyword={keyword}
              category={category}
              period={period}
            />
          </div>
          <div className="p-5 w-[410px] min-h-[800px]">
            {secondCountry ? (
              <SecondCountryBoard
                country_name={secondCountryName}
                country={secondCountry}
                keyword={keyword}
                category={category}
                period={period}
              />
            ) : (
              <div className="flex items-center justify-center w-full h-full text-gray-400 "></div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default WorldDetail;

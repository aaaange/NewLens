import KoreaAnalysis from '../components/korea/KoreaAnalysis';
import SearchInput from '../components/common/SearchInput';
import MindMap from '../components/common/MindMap';
import KeywordRanking from '../components/common/KeywordRanking';
import Category from '../components/common/Category';
import { useParams } from 'react-router-dom';

const KoreaAnalysisPage = () => {
  // const { category, period, keyword } = useParams();
  const { category, period } = useParams();

  const country: string = 'kr';
  const country_name: string = '대한민국';
  const keyword: string = '임시 데이터';

  return (
    <div className="mt-5 flex gap-10 justify-center">
      <div className="flex flex-col gap-5">
        <SearchInput />
        <MindMap />
        <KeywordRanking />
      </div>
      <div className="flex flex-col items-center gap-3">
        <Category />
        <KoreaAnalysis
          country={country}
          country_name={country_name}
          keyword={keyword ?? ''} // undefined 방지
          category={category ?? ''}
          period={category ?? ''}
        />
      </div>
    </div>
  );
};

export default KoreaAnalysisPage;

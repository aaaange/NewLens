import Category from '../components/common/Category';
import KeywordRanking from '../components/common/KeywordRanking';
import MindMap from '../components/common/MindMap';
import NewsModal from '../components/common/NewsModal';
import SearchInput from '../components/common/SearchInput';

const MainPage = () => {
  return (
    <div className="">
      <div>
        <SearchInput />
      </div>
      <div>
        <MindMap />
      </div>
      <div>
        <Category />
      </div>
      <div>
        <KeywordRanking />
      </div>
      <div>
        <NewsModal />
      </div>
    </div>
  );
};

export default MainPage;

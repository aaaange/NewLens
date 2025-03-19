import Category from '../components/common/Category';
import MindMap from '../components/common/MindMap';
import SearchInput from '../components/common/SearchInput';

const MainPage = () => {
  return (
    <div className="">
      <div>
        <MindMap />
      </div>
      <div>
        <SearchInput />
      </div>
      <div>
        <Category />
      </div>
    </div>
  );
};

export default MainPage;

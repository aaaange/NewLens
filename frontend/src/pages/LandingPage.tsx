import MapSwitchTab from '../components/world/MapSwitchTab';
import { Link } from 'react-router-dom';
import LoginPage from './LoginPage';

const Landing = () => {
  return (
    <div>
      <div className="flex flex-col items-center pt-[120px]">
        <h1 className="display-large pb-[30px]">
          세계가 주목하는 <span className="text-amount-300">이슈</span>
        </h1>
        <h1 className="display-large pb-[75px]">
          다양한 <span className="text-amount-300">데이터</span> 분석
        </h1>
        <Link
          to="/main"
          className="flex items-center justify-center bg-amount-300 w-[270px] h-[60px] rounded-[20px] text-gray-700 body-medium-bold"
        >
          <p className="">지금 시작하기</p>
        </Link>
        <MapSwitchTab />
      </div>

      <div
        className="flex flex-col headline-large tracking-wide
 gap-[20px] mb-[80px]"
      >
        <p className="ml-[500px]">뉴스도 데이터로 읽는 시대,</p>
        <p className="ml-[570px]">
          핫 키워드, 국가별 언급량, 감정 분석까지 한눈에!
        </p>
        <p className="ml-[350px]">
          각국의 뉴스 흐름을 비교하며 인사이트를 발견하세요.
        </p>
        <p className="ml-[490px]">내 관심사를 반영한 맞춤 뉴스까지,</p>
      </div>
      <div className="flex justify-center">
        <p className="display-small">
          <span className="text-amount-300">NEW</span>LEN
          <span className="text-amount-300">S</span>에서 경험해보세요
        </p>
      </div>

      <div className="flex flex-col items-center gap-[100px]">
        <div className="flex justify-center gap-[100px] mt-[150px]">
          <div className="flex flex-col items-center gap-[50px]">
            <img src="/assets/images/keywordranking.png" alt="로고" />
            <p className="headline-xlarge">국가별 반응 분석</p>
          </div>
          <div className="flex flex-col items-center gap-[50px]">
            <img src="/assets/images/keywordranking.png" alt="로고" />
            <p className="headline-xlarge">실시간 키워드 랭킹</p>
          </div>
          <div className="flex flex-col items-center gap-[50px]">
            <img src="/assets/images/keywordranking.png" alt="로고" />
            <p className="headline-xlarge">연관어 분석까지!</p>
          </div>
        </div>
        <h2 className="display-small">
          글로벌 <span className="text-amount-300">뉴스 흐름</span>을 한눈에!
        </h2>
      </div>

      <div className="flex flex-col justify-center items-center mt-[100px]">
        <p className="display-small">
          다양한 <span className="text-amount-300">글로벌 이슈</span>에 대한{' '}
          <span className="text-amount-300">G20</span> 국가의 반응은?
        </p>
        <div className="flex justify-center">
          <img src="/assets/images/keywordranking.png" alt="로고" />
          <img src="/assets/images/keywordranking.png" alt="로고" />
        </div>
      </div>

      <div className="flex flex-col justify-center items-center mt-[100px] gap-[40px]">
        <p className="display-small">
          당신의 관심 뉴스, <span className="text-amount-300">맞춤형 추천</span>
          부터 <span className="text-amount-300">스크랩</span>까지
        </p>
        <img src="/assets/images/news.png" alt="로고" />
        <img src="/assets/images/news.png" alt="로고" />
      </div>

      <div className="flex flex-col items-center mt-[200px] gap-[40px]">
        <img
          src="/assets/images/logo-newLens.png"
          alt="로고"
          className="w-[260px] h-[145px]"
        />
        <p className="display-small">
          지금 가입하고 나만의 맞춤 뉴스를 받아보세요!
        </p>
        <Link
          to="/main"
          className="flex items-center justify-center border border-gray-0 w-[270px] h-[60px] mt-[40px] rounded-[20px] text-gray-700 body-medium-bold"
        >
          <p className="text-gray-0">지금 시작하기</p>
        </Link>
      </div>
      <img src="/assets/images/earth.png" alt="로고" />
    </div>
  );
};

export default Landing;

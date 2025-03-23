import CountryDropdown from '../components/worldDetail/CountryDropdown';
import GptSummary from '../components/worldDetail/GptSummary';
import FirstCountryBoard from '../components/worldDetail/FirstCountryDashboard';
import SecondCountryBoard from '../components/worldDetail/SecondCountryDashboard';
import { keyword as mockKeyword } from '../components/worldDetail/MockData';

const description: string =
  "봄꽃이 개화하는 시기에 국내 여행객들이 가장 많이 찾는 여행지가 '제주도'라는 조사 결과가 나왔다. 12일 글로벌 여행 플랫폼 트립닷컴은 오는 25일~다음 달 30일 국내 여행객의 여행 추이를 공개했다. 제주시와 서귀포시가 1, 2위에 올랐다 지난해는 반대로 서귀포시가 1위, 제주시가 2위였다. 다음으로는 서울과 부산이 뒤를 이었다.";

const analysis: string = '한줄 비교 요약본 from gpt';

const WorldDetail = () => {
  return (
    <div className="mt-5">
      <div className="flex flex-col items-center gap-3">
        <div className="flex gap-10">
          <CountryDropdown width="410px" height="60px" />
          <CountryDropdown width="410px" height="60px" />
        </div>
        <GptSummary description={description} width={880} height={125} />
        <div className="flex flex-row gap-3 divide-x divide-gray-300 justify-between">
          <div className="p-5">
            <FirstCountryBoard
              country="US"
              keyword={mockKeyword}
              category="general"
              period="week"
            />
          </div>
          {/* <VerticalDivider /> */}
          <div className="p-5">
            <SecondCountryBoard
              country="US"
              keyword={mockKeyword}
              category="general"
              period="week"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default WorldDetail;

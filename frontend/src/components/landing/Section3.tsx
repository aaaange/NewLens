import { forwardRef, useEffect } from 'react';
import AOS from 'aos';
import 'aos/dist/aos.css';
const Section3 = forwardRef<HTMLDivElement>((_, ref) => {
  useEffect(() => {
    AOS.init();
  }, []);
  return (
    <div ref={ref} className="h-screen border-b border-gray-400">
      <div className="flex flex-col items-center gap-[100px]">
        <div className="display-small" data-aos="flip-up" data-aos-delay="2100">
          글로벌 <span className="text-amount-300">뉴스 흐름</span>을 한눈에!
        </div>
        <div className="flex justify-center gap-[100px]">
          <div
            className="flex flex-col items-center gap-[50px]"
            data-aos="fade-down"
            data-aos-delay="500"
          >
            <img src="/assets/images/keywordranking.png" alt="로고" />
            <p className="headline-xlarge">국가별 반응 분석</p>
          </div>
          <div
            className="flex flex-col items-center gap-[50px]"
            data-aos="fade-down"
            data-aos-delay="1300"
          >
            <img src="/assets/images/keywordranking.png" alt="로고" />
            <p className="headline-xlarge">실시간 키워드 랭킹</p>
          </div>
          <div
            className="flex flex-col items-center gap-[50px]"
            data-aos="fade-down"
            data-aos-delay="2100"
          >
            <img src="/assets/images/keywordranking.png" alt="로고" />
            <p className="headline-xlarge">연관어 분석까지!</p>
          </div>
        </div>
      </div>
    </div>
  );
});

export default Section3;

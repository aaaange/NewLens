import { forwardRef, useEffect } from 'react';
import { Fade } from 'react-awesome-reveal';
import { motion } from 'framer-motion';

const Section3 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div ref={ref} className="h-screen border-b border-gray-400 p-[40px]">
      <div className="flex flex-col items-center gap-[60px]">
        <div className="flex justify-center gap-[100px]">
          <div className="flex flex-col items-center h-[500px] w-fit">
            <div className="flex flex-col gap-[20px]">
              <div className="flex flex-col items-center">
                <p className="headline-small">국가별 이슈 언급량 & 감정 분석</p>
                <p className="caption-small text-tetiary-600">
                  키워드에 대한 국가별 관심도와 반응을 확인해보세요!
                </p>
              </div>
              <img
                src="/assets/images/vector-world-map 3.png"
                alt="로고"
                className="w-[500px] h-[250px]"
              />
            </div>

            <div className="flex items-end h-full">
              <p className="headline-xlarge justify-baseline">
                국가별 반응 분석
              </p>
            </div>
          </div>
          <div className="flex flex-col items-center h-[500px] w-fit">
            <div className="flex flex-col gap-[20px]">
              <div className="flex flex-col items-center">
                <p className="headline-small">실시간 인기 검색어</p>
                <p className="caption-small text-tetiary-600">
                  실시간으로 가장 핫한 키워드!
                </p>
              </div>
              <img
                src="/assets/images/keywordranking.png"
                alt="로고"
                className="w-[300px] h-[350px]"
              />
            </div>

            <div className="flex items-end h-full">
              <p className="headline-xlarge justify-baseline">
                실시간 키워드 랭킹
              </p>
            </div>
          </div>
          <div className="flex flex-col items-center h-[500px] w-fit">
            <div className="flex flex-col gap-[60px]">
              <div className="flex flex-col items-center">
                <p className="headline-small">연관어</p>
                <p className="caption-small text-tetiary-600">
                  추천 연관어를 선택하여 검색해보세요!{' '}
                </p>
              </div>
              <div className="flex justify-center items-center">
                <img
                  src="/assets/images/mindmap.png"
                  alt="마인드맵"
                  className="w-[350px] h-[250px]"
                />
              </div>
            </div>

            <div className="flex items-end h-full">
              <p className="headline-xlarge justify-baseline">
                연관어 분석까지!
              </p>
            </div>
          </div>
        </div>

        <Fade direction="down" triggerOnce={false} duration={1000} delay={2800}>
          <div className="display-small">
            글로벌 <span className="text-amount-300">뉴스 흐름</span>을 한눈에!
          </div>
        </Fade>
      </div>
    </div>
  );
});

export default Section3;

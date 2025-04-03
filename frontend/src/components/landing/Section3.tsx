import { forwardRef, useEffect } from 'react';
import { Fade, Zoom } from 'react-awesome-reveal';
import { motion } from 'framer-motion';

const Section3 = forwardRef<HTMLDivElement>((_, ref) => {
  const fadeSections = [
    {
      title: '국가별 이슈 언급량 & 감정 분석',
      subtitle: '키워드에 대한 국가별 관심도와 반응을 확인해보세요!',
      image: '/assets/images/vector-world-map 3.png',
      bottomText: '국가별 반응 분석',
      imgStyle: 'w-[500px] h-[250px]',
    },
    {
      title: '실시간 인기 검색어',
      subtitle: '실시간으로 가장 핫한 키워드!',
      image: '/assets/images/keywordranking.png',
      bottomText: '실시간 키워드 랭킹',
      imgStyle: 'w-[300px] h-[350px]',
    },
    {
      title: '연관어',
      subtitle: '추천 연관어를 선택하여 검색해보세요!',
      image: '/assets/images/mindmap.png',
      bottomText: '연관어 분석까지!',
      imgStyle: 'w-[350px] h-[250px]',
    },
  ];

  return (
    <div
      ref={ref}
      className="h-screen border-b border-gray-400 p-[40px] flex flex-col items-center justify-center gap-[100px]"
    >
      {/* 상단 */}
      <div className="flex justify-center gap-[100px]">
        {fadeSections.map((section, index) => (
          <Fade direction={'left'} delay={index * 400} triggerOnce={false}>
            {/* 국가별 반응 분석 */}

            <div className="flex flex-col items-center h-[500px] w-fit">
              <div className="flex flex-col gap-[20px]">
                <div className="flex flex-col items-center">
                  <p className="headline-small">{section.title}</p>
                  <p className="caption-small text-tetiary-600">
                    {section.subtitle}
                  </p>
                </div>
                <img
                  src={section.image}
                  alt="로고"
                  className={section.imgStyle}
                />
              </div>

              <div className="flex items-end h-full">
                <p className="headline-xlarge justify-baseline">
                  {section.bottomText}
                </p>
              </div>
            </div>
          </Fade>
        ))}
      </div>

      {/* 하단 */}
      <Zoom triggerOnce={false} duration={1000} delay={1600}>
        <div className="display-small">
          글로벌 <span className="text-amount-300">뉴스 흐름</span>을 한눈에!
        </div>
      </Zoom>
    </div>
  );
});

export default Section3;

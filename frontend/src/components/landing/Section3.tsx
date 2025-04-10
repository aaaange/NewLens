import { forwardRef } from 'react';
import { motion } from 'framer-motion';

const Section3 = forwardRef<HTMLDivElement>((_, ref) => {
  const fadeSections = [
    {
      title: '국가별 이슈 언급량 & 감정 분석',
      subtitle: '키워드에 대한 국가별 관심도와 반응을 확인해보세요!',
      image: '/assets/images/vector-world-map 3.png',
      bottomText: '국가별 반응 분석',
      imgStyle: 'w-[30vw] h-[30vh]',
    },
    {
      title: '실시간 인기 키워드',
      subtitle: '실시간으로 가장 핫한 키워드!',
      image: '/assets/images/keywordranking.png',
      bottomText: '실시간 키워드 랭킹',
      imgStyle: 'w-[20vw] h-[45vh]',
    },
    {
      title: '연관어',
      subtitle: '추천 연관어를 선택하여 검색해보세요!',
      image: '/assets/images/mindmap.png',
      bottomText: '연관어 분석까지!',
      imgStyle: 'w-[22vw] h-[30vh]',
    },
  ];

  // 공통 애니메이션 variants
  const fadeLeftVariant = {
    hidden: { opacity: 0, x: -50 },
    visible: (custom: number) => ({
      opacity: 1,
      x: 0,
      transition: {
        delay: custom * 0.4,
        duration: 0.6,
        ease: 'easeOut',
      },
    }),
  };

  const zoomInVariant = {
    hidden: { scale: 0.8, opacity: 0 },
    visible: {
      scale: 1,
      opacity: 1,
      transition: {
        delay: 1.6,
        duration: 0.8,
        ease: 'easeOut',
      },
    },
  };

  return (
    <div
      ref={ref}
      className="h-screen p-[6vw] flex flex-col items-center gap-[16vh]"
    >
      {/* 상단 */}
      <div className="flex justify-center gap-[1vw] -mt-[3vh] w-full">
        {fadeSections.map((section, index) => (
          <motion.div
            key={index}
            className="flex flex-col flex-1 items-center justify-center h-[50vh] w-fit"
            variants={fadeLeftVariant}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: false, amount: 0.3 }}
            custom={index}
          >
            <div className="flex flex-col h-full gap-[6vh]">
              <div className="flex flex-col items-center">
                <p className="headline-medium">{section.title}</p>
                <p className="caption-medium text-tetiary-600">
                  {section.subtitle}
                </p>
              </div>
              <div className="flex items-center">
                <img
                  src={section.image}
                  alt="로고"
                  className={section.imgStyle}
                />
              </div>
            </div>

            {/* <div className="flex items-end h-full">
              <p className="headline-large justify-baseline">
                {section.bottomText}
              </p>
            </div> */}
          </motion.div>
        ))}
      </div>

      {/* 하단 */}
      <motion.div
        className="display-medium"
        variants={zoomInVariant}
        initial="hidden"
        whileInView="visible"
        viewport={{ once: false, amount: 0.3 }}
      >
        글로벌 <span className="text-amount-300">뉴스 흐름</span>을 한눈에!
      </motion.div>
    </div>
  );
});

export default Section3;

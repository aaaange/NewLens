import { forwardRef } from 'react';
import { motion } from 'framer-motion';

const Section5 = forwardRef<HTMLDivElement>((_, ref) => {
  // 애니메이션 정의
  const fadeLeftVariant = {
    hidden: { opacity: 0, x: -50 },
    visible: {
      opacity: 1,
      x: 0,
      transition: {
        duration: 1,
        ease: 'easeOut',
        delay: 0.3,
      },
    },
  };

  return (
    <div ref={ref} className="flex h-screen justify-center gap-[8vw] p-[4vw]">
      <motion.div
        className="flex flex-col gap-[3vh] pt-[3vh] justify-items-center"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: false, amount: 0.2 }}
        variants={fadeLeftVariant}
      >
        <p className="display-medium">당신의 관심 뉴스, </p>

        <p className="display-medium">
          <span className="text-amount-300">맞춤형 추천</span> 부터{' '}
          <span className="text-amount-300">스크랩</span>까지
        </p>
        <p className="headline-small">
          지금 가장 많이 본 뉴스 기반으로, 당신만을 위한 추천 뉴스 큐레이션
        </p>
      </motion.div>

      <motion.div
        className="flex items-end"
        initial="hidden"
        whileInView="visible"
        viewport={{ once: false, amount: 0.2 }}
        variants={{
          hidden: { opacity: 0, x: 50 },
          visible: {
            opacity: 1,
            x: 0,
            transition: { duration: 1, ease: 'easeOut', delay: 0.3 },
          },
        }}
      >
        <img
          src="/assets/images/news_landing2.png"
          alt="뉴스 스크랩 이미지"
          className="right-20 bottom-0 w-[400px] h-[400px]"
        />
      </motion.div>
    </div>
  );
});

export default Section5;

import { forwardRef } from 'react';
import { motion } from 'framer-motion';

const Section2 = forwardRef<HTMLDivElement>((_, ref) => {
  // Fade + Scale + Slide Left
  const fadeScaleLeft = {
    hidden: { opacity: 0, x: -30, scale: 0.95 },
    visible: (i: number) => ({
      opacity: 1,
      x: 0,
      scale: 1,
      transition: { delay: i * 0.4, duration: 0.8, ease: 'easeOut' },
    }),
  };

  // Fade + Scale + Slide Right
  const fadeScaleRight = {
    hidden: { opacity: 0, x: 30, scale: 0.95 },
    visible: (i: number) => ({
      opacity: 1,
      x: 0,
      scale: 1,
      transition: { delay: i * 0.4, duration: 0.8, ease: 'easeOut' },
    }),
  };

  // Fade + Scale + Slide Up
  const fadeScaleUp = {
    hidden: { opacity: 0, y: 30, scale: 0.95 },
    visible: {
      opacity: 1,
      y: 0,
      scale: 1,
      transition: { delay: 2, duration: 0.8, ease: 'easeOut' },
    },
  };

  return (
    // <div
    //   ref={ref}
    //   className="relative flex flex-col justify-start items-center h-screen p-[2vw]"
    // >
    //   <div className="z-10 flex flex-col items-center gap-[4vh] mt-[10vh] text-center">
    //     <motion.p
    //       className=" font-['Cafe24ClassicType-Regular']"
    //       initial="hidden"
    //       whileInView="visible"
    //       viewport={{ once: false, amount: 0.2 }}
    //       variants={fadeScaleLeft}
    //       custom={0}
    //     >
    //       뉴스도 데이터로 읽는 시대,
    //     </motion.p>

    //     <motion.p
    //       className="headline-xlarge"
    //       initial="hidden"
    //       whileInView="visible"
    //       viewport={{ once: false, amount: 0.2 }}
    //       variants={fadeScaleRight}
    //       custom={1}
    //     >
    //       핫 키워드, 국가별 언급량, 감정 분석까지 한눈에!
    //     </motion.p>

    //     <motion.p
    //       className="headline-xlarge"
    //       initial="hidden"
    //       whileInView="visible"
    //       viewport={{ once: false, amount: 0.2 }}
    //       variants={fadeScaleLeft}
    //       custom={2}
    //     >
    //       각국의 뉴스 흐름을 비교하며 인사이트를 발견하세요.
    //     </motion.p>

    //     <motion.p
    //       className="headline-xlarge"
    //       initial="hidden"
    //       whileInView="visible"
    //       viewport={{ once: false, amount: 0.2 }}
    //       variants={fadeScaleRight}
    //       custom={3}
    //     >
    //       내 관심사를 반영한 맞춤 뉴스까지,
    //     </motion.p>

    //     <motion.p
    //       className="display-medium mt-[10vh]"
    //       initial="hidden"
    //       whileInView="visible"
    //       viewport={{ once: false, amount: 0.2 }}
    //       variants={fadeScaleUp}
    //     >
    //       <span className="text-amount-300">NEW</span>LEN
    //       <span className="text-amount-300">S</span>에서 경험해보세요
    //     </motion.p>
    //   </div>

    //   {/* 아이콘 이미지들 */}
    //   <div className="absolute bottom-[2vh] left-0 right-0 flex justify-between px-[6vw] pointer-events-none">
    //     <img
    //       src="/assets/images/bar-chart.png"
    //       className="absolute bottom-[5vh] left-[4vw] rotate-[-10deg] w-[140px] h-[140px]"
    //     />
    //     <img
    //       src="/assets/images/world-grid.png"
    //       className="absolute bottom-[5vh] right-[4vw] w-[140px] h-[140px] rotate-[15deg]"
    //     />
    //     <img
    //       src="/assets/images/news-paper.png"
    //       className="absolute bottom-[15vh] left-[18vw] w-[140px] h-[140px] rotate-[10deg]"
    //     />
    //   </div>
    // </div>
    <div>
      {/* 숫자 */}
      <div ref={ref} className="flex h-screen justify-between m-[20vh]">
        <div className="flex flex-col items-center gap-8">
          <p className="text-primary-300 display-small">COUNTRIES</p>
          <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
            18
          </p>
        </div>
        <div className="flex flex-col items-center gap-8">
          <p className="text-primary-300 display-small">DAILY DATA</p>
          <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
            100,000+
          </p>
        </div>
        <div className="flex flex-col items-center gap-8">
          <p className="text-primary-300 display-small">TOTAL DATA</p>
          <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
            1,116,279
          </p>
        </div>
      </div>

      {/* 문장 */}
      <div></div>
    </div>
  );
});

export default Section2;

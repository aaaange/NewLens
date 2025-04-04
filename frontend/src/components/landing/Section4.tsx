import { forwardRef } from 'react';
import { motion } from 'framer-motion';

const Section4 = forwardRef<HTMLDivElement>((_, ref) => {
  const clashVariant = (direction: 'left' | 'right', delay = 0) => {
    const xVal = direction === 'left' ? -300 : 300;
    return {
      hidden: { opacity: 0, x: xVal, scale: 0.5 },
      visible: {
        opacity: 1,
        x: 0,
        scale: 1.05,
        transition: {
          delay: delay / 1000,
          duration: 0.9,
          type: 'spring',
          stiffness: 120,
          damping: 14,
        },
      },
    };
  };

  const vsVariant = {
    hidden: { opacity: 0, scale: 0 },
    visible: {
      opacity: 1,
      scale: [0, 1.3, 1],
      transition: {
        delay: 0.9,
        duration: 0.9,
        ease: 'easeOut',
      },
    },
  };

  const titleVariant = {
    hidden: { opacity: 0, y: -30 },
    visible: {
      opacity: 1,
      y: 0,
      transition: {
        delay: 1.5,
        duration: 0.9,
        ease: 'easeOut',
      },
    },
  };

  return (
    <div
      ref={ref}
      className="h-screen flex flex-col justify-center items-center gap-[6vh] p-[4vw]"
    >
      <motion.p
        className="display-medium text-center"
        variants={titleVariant}
        initial="hidden"
        whileInView="visible"
        viewport={{ once: false, amount: 0.3 }}
      >
        다양한 <span className="text-amount-300">글로벌 이슈</span>에 대한{' '}
        <span className="text-amount-300">G20</span> 국가의 반응은?
      </motion.p>

      <div className="flex gap-[8vw] mt-[7vh] items-center">
        <motion.div
          className="flex flex-col items-center"
          variants={clashVariant('left', 200)}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: false, amount: 0.3 }}
        >
          <div className="flex flex-col items-center gap-[1vw]">
            <div className="flex headline-small gap-[0.4vw]">
              <img
                src="https://flagcdn.com/us.svg"
                width={24}
                height={12}
                alt="US Flag"
              />
              <p>
                <span className="headline-medium">미국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  감정
                </span>{' '}
                분석
              </p>
            </div>
            <img
              src="/assets/images/us-columns.png"
              alt="미국 감정 분석"
              className="w-[250px] h-[150px]"
            />
          </div>
          <div className="flex flex-col items-center gap-[1vw]">
            <div className="flex headline-small gap-[0.4vw] mt-[5vh]">
              <img
                src="https://flagcdn.com/us.svg"
                width={24}
                height={12}
                alt="US Flag"
              />
              <p>
                <span className="headline-medium">미국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  언급량
                </span>{' '}
                변화
              </p>
            </div>
            <img
              src="/assets/images/us-mention.png"
              alt="미국 언급량 변화"
              className="w-[250px] h-[150px]"
            />
          </div>
        </motion.div>

        <motion.div
          className="flex items-center display-xlarge"
          variants={vsVariant}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: false, amount: 0.3 }}
        >
          <p className="text-amount-300">VS</p>
        </motion.div>

        <motion.div
          className="flex flex-col items-center"
          variants={clashVariant('right', 500)}
          initial="hidden"
          whileInView="visible"
          viewport={{ once: false, amount: 0.3 }}
        >
          <div className="flex flex-col items-center gap-[1vw]">
            <div className="flex headline-small gap-[0.4vw]">
              <img
                src="https://flagcdn.com/kr.svg"
                width={24}
                height={12}
                alt="KR Flag"
              />
              <p>
                <span className="headline-medium">한국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300"> 감정 </span>
                분석
              </p>
            </div>
            <img
              src="/assets/images/kr-columns.png"
              alt="한국 감정 분석"
              className="w-[250px] h-[150px]"
            />
          </div>
          <div className="flex flex-col items-center gap-[1vw]">
            <div className="flex headline-small gap-[0.4vw] mt-[5vh]">
              <img
                src="https://flagcdn.com/kr.svg"
                width={24}
                height={12}
                alt="KR Flag"
              />
              <p>
                <span className="headline-medium">한국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  언급량
                </span>{' '}
                변화
              </p>
            </div>
            <img
              src="/assets/images/kr-mention.png"
              alt="한국 언급량 변화"
              className="w-[250px] h-[150px]"
            />
          </div>
        </motion.div>
      </div>
    </div>
  );
});

export default Section4;

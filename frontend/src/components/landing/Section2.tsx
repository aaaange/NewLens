import { forwardRef } from 'react';
import LandingBgMap from './LandingBgMap';
import CountUp from 'react-countup';
import { motion } from 'framer-motion';
import { useInView } from 'react-intersection-observer';
import { Visibility } from '@mui/icons-material';

const Section2 = forwardRef<HTMLDivElement>((_, ref) => {
  // 카운트 애니메이션 감지용 ref
  const [refCountry, inViewCountry] = useInView({
    triggerOnce: false,
    threshold: 0.3,
  });
  const [refDaily, inViewDaily] = useInView({
    triggerOnce: false,
    threshold: 0.3,
  });
  const [refTotal, inViewTotal] = useInView({
    triggerOnce: false,
    threshold: 0.3,
  });

  const fadeScaleUp = {
    hidden: { opacity: -1, scale: 0.95, Visibility: 'hidden' },
    visible: (custom: number) => ({
      opacity: 1,
      y: 0,
      scale: 1,
      transition: {
        delay: custom,
        duration: 0.8,
        ease: 'easeOut',
      },
    }),
  };

  return (
    <div
      ref={ref}
      className="relative flex flex-col items-center h-screen overflow-hidden"
    >
      {/* 배경 */}
      <div className="absolute inset-0 z-0 opacity-80 flex justify-center">
        <LandingBgMap />
      </div>

      {/* 숫자 */}
      <div className="relative z-10 flex items-center justify-between m-[20vh] w-full">
        <div
          className="flex flex-col flex-1 items-center gap-8"
          ref={refCountry}
        >
          <p className="text-primary-300 display-small">COUNTRIES</p>
          <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
            {inViewCountry ? (
              <CountUp end={17} duration={0.5} useEasing={false} />
            ) : (
              0
            )}
          </p>
        </div>

        <div className="flex flex-col flex-1 items-center gap-8" ref={refDaily}>
          <p className="text-primary-300 display-small">DAILY DATA</p>
          <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
            {inViewDaily ? (
              <CountUp
                end={40000}
                duration={1.8}
                useEasing={false}
                delay={0.5}
              />
            ) : (
              0
            )}
          </p>
        </div>

        <div className="flex flex-col flex-1 items-center gap-8" ref={refTotal}>
          <p className="text-primary-300 display-small">TOTAL DATA</p>
          <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
            {inViewTotal ? (
              <CountUp
                end={1216279}
                duration={2.7}
                useEasing={false}
                delay={2.2}
              />
            ) : (
              0
            )}
          </p>
        </div>
      </div>

      {/* 문장 */}
      <div className="relative z-10 flex flex-col items-center gap-8">
        <motion.div
          custom={4.9}
          initial="hidden"
          variants={fadeScaleUp}
          transition={{ duration: 1 }}
          whileInView="visible"
        >
          <p className="display-small">데이터로 세계의 흐름을 읽는 시대,</p>
        </motion.div>
        <motion.div
          custom={5.6}
          initial="hidden"
          variants={fadeScaleUp}
          whileInView="visible"
          transition={{ duration: 1 }}
          viewport={{ once: false, amount: 0.3 }}
        >
          <p className="display-small">
            <span className="text-amount-300">NEW</span>LEN
            <span className="text-amount-300">S</span>에서 당신만의 인사이트를
            얻으세요
          </p>
        </motion.div>
      </div>
    </div>
  );
});

export default Section2;

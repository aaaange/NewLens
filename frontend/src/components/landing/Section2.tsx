import { forwardRef } from 'react';
import LandingBgMap from './LandingBgMap';
import CountUp from 'react-countup';
import { useInView } from 'react-intersection-observer';
import VisibilitySensor from 'react-visibility-sensor';
import { motion } from 'framer-motion';

const Section2 = forwardRef<HTMLDivElement>((_, ref) => {
  const { ref: countRef, inView } = useInView({
    triggerOnce: false, // 한 번만 실행
    threshold: 0.3, // 30% 보일 때
  });

  const fadeScaleUp = {
    hidden: { opacity: 0, y: 30, scale: 0.95 },
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
    <div ref={ref} className="relative flex flex-col items-center h-screen">
      <div className="absolute inset-0 z-0 opacity-80 flex justify-center itmes-end">
        <LandingBgMap />
      </div>

      {/* 숫자 */}
      <div className="relative z-10 flex items-center justify-between m-[20vh] w-full">
        <div className="flex flex-col flex-1 items-center gap-8">
          <p className="text-primary-300 display-small">COUNTRIES</p>

          <VisibilitySensor partialVisibility offset={{ bottom: 200 }}>
            {({ isVisible }: { isVisible: boolean }) => (
              <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
                {isVisible ? (
                  <CountUp end={18} duration={0.5} useEasing={false} />
                ) : (
                  0
                )}
              </p>
            )}
          </VisibilitySensor>
        </div>
        <div className="flex flex-col flex-1 items-center gap-8">
          <p className="text-primary-300 display-small">DAILY DATA</p>
          <VisibilitySensor partialVisibility offset={{ bottom: 200 }}>
            {({ isVisible }: { isVisible: boolean }) => (
              <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
                {isVisible ? (
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
            )}
          </VisibilitySensor>
        </div>
        <div className="flex flex-col flex-1 items-center gap-8">
          <p className="text-primary-300 display-small">TOTAL DATA</p>
          <VisibilitySensor partialVisibility offset={{ bottom: 200 }}>
            {({ isVisible }: { isVisible: boolean }) => (
              <p className="font-['Cafe24ClassicType-Regular'] text-7xl text-amount-300">
                {isVisible ? (
                  <CountUp
                    end={1116279}
                    duration={2.7}
                    useEasing={false}
                    delay={2}
                  />
                ) : (
                  0
                )}
              </p>
            )}
          </VisibilitySensor>
        </div>
      </div>
      {/* 문장 */}
      <div className="relative z-10 flex flex-col items-center gap-8">
        <motion.div
          custom={0}
          initial="hidden"
          variants={fadeScaleUp}
          transition={{ duration: 1 }}
          whileInView="visible"
        >
          <p className="display-small">데이터로 세계의 흐름을 읽는 시대,</p>
        </motion.div>
        <motion.div
          custom={0.5}
          initial="hidden"
          variants={fadeScaleUp}
          whileInView="visible"
          transition={{ duration: 1 }}
          viewport={{ once: false, amount: 0.3 }}
        >
          <p className="display-small">
            NEWLENS에서 당신만의 인사이트를 얻으세요
          </p>
        </motion.div>
      </div>
    </div>
  );
});

export default Section2;

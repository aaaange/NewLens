import { forwardRef } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import PlayGround from '../common/PlayGround';

interface Section1Props {
  onArrowClick?: () => void;
}

const Section1 = forwardRef<HTMLDivElement, Section1Props>(
  ({ onArrowClick }, ref) => {
    const fadeSlideDown = {
      hidden: { opacity: 0, y: -20 },
      visible: { opacity: 1, y: 0 },
    };

    return (
      <div
        ref={ref}
        className=" flex flex-col items-center pt-10 h-screen overflow-visible"
      >
        <div className="flex-1 relative z-0 translate-x-[18vw] -translate-y-[5vh]">
          <PlayGround />
        </div>

        <div className="absolute top-[20vh] left-[8vw] z-10 flex flex-col items-center gap-[6vh]">
          {/* title */}
          <motion.div
            initial="hidden"
            animate="visible"
            variants={fadeSlideDown}
            transition={{ duration: 1, delay: 0.5 }}
            className="relative z-30 mb-[-1vh]"
          >
            <img
              src="/assets/images/logo-newLens.png"
              alt="로고"
              className="w-[130px] h-[75px]"
            />
          </motion.div>
          <div className="flex flex-col items-center gap-[30px]">
            <motion.div
              initial="hidden"
              animate="visible"
              variants={fadeSlideDown}
              transition={{ duration: 1, delay: 0.5 }}
            >
              <h1 className="display-large">
                세계가 주목하는 <span className="text-amount-300">이슈</span>
              </h1>
            </motion.div>

            <motion.div
              initial="hidden"
              animate="visible"
              variants={fadeSlideDown}
              transition={{ duration: 1, delay: 1 }}
            >
              <h2 className="display-large">
                다양한 <span className="text-amount-300">데이터</span> 분석
              </h2>
            </motion.div>
          </div>
          {/* button */}
          <Link
            to="/login"
            className="flex items-center justify-center w-[270px] h-[60px] rounded-[20px] z-20 text-gray-0 body-medium-bold mb-[20px] border-2 border-amount-300 hover:bg-amount-300 hover:text-gray-700"
            onClick={() => window.scrollTo(0, 0)}
          >
            <p>지금 시작하기</p>
          </Link>
        </div>

        <img
          src="/assets/images/Expand_down_2.png"
          alt="아래 화살표"
          className="absolute bottom-[20px] z-20 w-[100px] h-[80px] animate-bounce cursor-pointer hover:scale-120"
          onClick={onArrowClick}
        />
      </div>
    );
  }
);

export default Section1;

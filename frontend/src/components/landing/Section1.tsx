import { forwardRef } from 'react';
import { Link } from 'react-router-dom';
import React, { useEffect } from 'react';
import { Fade } from 'react-awesome-reveal';
import PlayGround from '../common/PlayGround';
import classes from './Section.module.css';

const Section1 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div
      ref={ref}
      className="flex flex-col items-center pt-[120px] min-h-screen relative border-b border-gray-400 overflow-visible"
    >
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 z-0">
        <PlayGround />
      </div>
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 flex flex-col items-center gap-[80px] z-20">
        {/* title */}
        <div className="flex flex-col items-center gap-[30px]">
          <Fade
            direction="down"
            triggerOnce={false}
            delay={200}
            duration={1000}
          >
            <h1 className="display-large">
              세계가 주목하는 <span className="text-amount-300">이슈</span>
            </h1>
          </Fade>
          <Fade
            direction="down"
            triggerOnce={false}
            duration={1000}
            delay={700}
          >
            <h2 className="display-large">
              다양한 <span className="text-amount-300">데이터</span> 분석
            </h2>
          </Fade>
        </div>

        {/* button */}
        <Link
          to="/main"
          className="flex items-center justify-center w-[270px] h-[60px] rounded-[20px] z-20 text-gray-0 body-medium-bold mb-[20px] border-2 border-amount-300 hover:bg-amount-300  hover:text-gray-700"
          onClick={() => window.scrollTo(0, 0)}
        >
          <p className="">지금 시작하기</p>
        </Link>
      </div>
      <img
        src="/assets/images/Expand_down_2.png"
        alt="아래 화살표"
        className={`w-[100px] h-[80px] z-50 absolute bottom-0 animate-bounce `}
      />
      {/* <img
        src="/assets/images/Expand_down_2.png"
        alt="아래 화살표"
        className="w-[160px] h-[120px] z-50 absolute bottom-[20px] animate-bounce"
      /> */}
    </div>
  );
});

export default Section1;

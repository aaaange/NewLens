import { forwardRef } from 'react';
import { Link } from 'react-router-dom';
import React, { useEffect } from 'react';
import { Fade } from 'react-awesome-reveal';
import AOS from 'aos';
import 'aos/dist/aos.css';

const Section1 = forwardRef<HTMLDivElement>((_, ref) => {
  useEffect(() => {
    AOS.init({
      once: false,
      mirror: true,
    });
  }, []);
  return (
    <div
      ref={ref}
      className="flex flex-col items-center pt-[120px] min-h-screen relative border-b border-gray-400 overflow-visible"
    >
      <div className="flex flex-col items-center gap-[70px]">
        {/* title */}
        <div className="flex flex-col items-center gap-[30px]">
          <Fade
            direction="down"
            triggerOnce={false}
            duration={1000}
            damping={0.2}
          >
            <h1 className="display-large">
              세계가 주목하는 <span className="text-amount-300">이슈</span>
            </h1>
            <h2 className="display-large">
              다양한 <span className="text-amount-300">데이터</span> 분석
            </h2>
          </Fade>
          <Fade direction="down" triggerOnce={false} duration={1000}></Fade>
        </div>

        {/* button */}
        <Link
          to="/main"
          className="flex items-center justify-center w-[270px] h-[60px] rounded-[20px] text-gray-0 body-medium-bold mb-[20px] border-2 border-amount-300 hover:bg-amount-300  hover:text-gray-700"
          onClick={() => window.scrollTo(0, 0)}
        >
          <p className="">지금 시작하기</p>
        </Link>
      </div>
      <img
        src="/assets/images/earth_landing.gif"
        alt="지구"
        className="absolute -bottom-[150px] w-[500px] z-10 overflow-x-hidden"
      />
    </div>
  );
});

export default Section1;

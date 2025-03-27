import { forwardRef } from 'react';
import { Link } from 'react-router-dom';
import React, { useEffect } from 'react';
import AOS from 'aos';
import 'aos/dist/aos.css';

const Section1 = forwardRef<HTMLDivElement>((_, ref) => {
  useEffect(() => {
    AOS.init();
  }, []);
  return (
    <div
      ref={ref}
      className="flex flex-col items-center pt-[120px] min-h-screen relative border-b border-gray-400 overflow-visible"
    >
      <h1
        className="display-large pb-[30px]"
        data-aos="fade-up"
        data-aos-anchor-placement="top-bottom"
        data-aos-easing="ease-in-out"
      >
        세계가 주목하는 <span className="text-amount-300">이슈</span>
      </h1>
      <h1
        className="display-large pb-[75px]"
        data-aos="fade-up"
        data-aos-anchor-placement="top-bottom"
        data-aos-delay="700"
        data-aos-easing="ease-in-out"
      >
        다양한 <span className="text-amount-300">데이터</span> 분석
      </h1>
      <Link
        to="/main"
        className="flex items-center justify-center w-[270px] h-[60px] rounded-[20px] text-gray-0 body-medium-bold mb-[20px] border-2 border-amount-300 hover:bg-amount-300  hover:text-gray-700"
      >
        <p className="">지금 시작하기</p>
      </Link>
      <img
        src="/assets/images/earth_landing.gif"
        alt="지구"
        className="absolute -bottom-[150px] w-[500px] z-10"
      />
    </div>
  );
});

export default Section1;

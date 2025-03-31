import { Link } from 'react-router-dom';
import { forwardRef } from 'react';

const Section6 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div ref={ref} className="h-screen">
      <div className="flex flex-col items-center mt-[200px] gap-[40px]">
        <img
          src="/assets/images/logo-newLens.png"
          alt="로고"
          className="w-[260px] h-[145px]"
        />
        <p className="display-small">
          지금 가입하고 나만의 맞춤 뉴스를 받아보세요!
        </p>
        <Link
          to="/login"
          className="flex items-center justify-center border border-gray-0 w-[270px] h-[60px] mt-[40px] rounded-[20px] text-gray-700 body-medium-bold"
        >
          <p className="text-gray-0">지금 시작하기</p>
        </Link>
      </div>
      <img src="/assets/images/earth.png" alt="로고" />
    </div>
  );
});

export default Section6;

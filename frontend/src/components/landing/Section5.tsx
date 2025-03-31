import { forwardRef } from 'react';
const Section5 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div ref={ref} className="h-screen border-b border-gray-400 p-[20px]">
      <div className="flex flex-col justify-center items-center gap-[40px] mt-[50px]">
        <p className="display-small">
          당신의 관심 뉴스, <span className="text-amount-300">맞춤형 추천</span>
          부터 <span className="text-amount-300">스크랩</span>까지
        </p>
        <img
          src="/assets/images/news1.png"
          alt="뉴스 스크랩 이미지"
          className="w-[600px] h-[200px]"
        />
        <img
          src="/assets/images/news2.png"
          alt="뉴스 스크랩 이미지"
          className="w-[600px] h-[200px]"
        />
      </div>
    </div>
  );
});

export default Section5;

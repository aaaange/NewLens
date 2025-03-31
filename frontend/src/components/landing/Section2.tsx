import { forwardRef } from 'react';
const Section2 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div
      ref={ref}
      className="flex flex-col h-screen border-b border-gray-400 pt-[250px] gap-[80px]"
    >
      <div className="flex flex-col headline-large gap-[20px] items-center">
        <p data-aos="fade-right">뉴스도 데이터로 읽는 시대,</p>
        <p data-aos="fade-left" data-aos-delay="500">
          핫 키워드, 국가별 언급량, 감정 분석까지 한눈에!
        </p>
        <p data-aos="fade-right" data-aos-delay="1200">
          각국의 뉴스 흐름을 비교하며 인사이트를 발견하세요.
        </p>
        <p data-aos="fade-left" data-aos-delay="1900">
          내 관심사를 반영한 맞춤 뉴스까지,
        </p>
      </div>
      <div className="flex justify-center">
        <p
          className="display-small"
          data-aos="fade-down"
          data-aos-delay="2700"
          data-aos-easing="ease-in-out"
          data-aos-offset="200"
        >
          <span className="text-amount-300">NEW</span>LEN
          <span className="text-amount-300">S</span>에서 경험해보세요
        </p>
      </div>
    </div>
  );
});

export default Section2;

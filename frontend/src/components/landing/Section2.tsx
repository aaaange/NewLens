import { forwardRef } from 'react';
import { Fade } from 'react-awesome-reveal';
const Section2 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div
      ref={ref}
      className="flex flex-col h-screen border-b border-gray-400 pt-[250px] gap-[80px]"
    >
      <div className="flex flex-col headline-large gap-[20px] items-center">
        <p>뉴스도 데이터로 읽는 시대,</p>
        <p>핫 키워드, 국가별 언급량, 감정 분석까지 한눈에!</p>
        <p>각국의 뉴스 흐름을 비교하며 인사이트를 발견하세요.</p>
        <p>내 관심사를 반영한 맞춤 뉴스까지,</p>
      </div>
      <div className="flex justify-center">
        <Fade direction="down" triggerOnce={false} duration={1000}>
          <p className="display-small">
            <span className="text-amount-300">NEW</span>LEN
            <span className="text-amount-300">S</span>에서 경험해보세요
          </p>
        </Fade>
      </div>
    </div>
  );
});

export default Section2;

import { Link } from 'react-router-dom';
import { forwardRef, useEffect, useState, useRef } from 'react';
import { useInView } from 'framer-motion';

const Section6 = forwardRef<HTMLDivElement>((_, ref) => {
  const txt = '지금 가입하고 나만의 NEWLENS를 써보세요.';
  const highlightIndexes = [12, 13, 14, 18];
  const [index, setIndex] = useState(0);

  // 내부 관찰용 ref + inView 체크
  const innerRef = useRef(null);
  const isInView = useInView(innerRef, { once: false });

  useEffect(() => {
    if (!isInView || index >= txt.length) return;

    const interval = setInterval(() => {
      setIndex((prev) => prev + 1);
    }, 100);

    return () => clearInterval(interval);
  }, [isInView, index]);

  const renderTypedText = () =>
    txt
      .slice(0, index)
      .split('')
      .map((char, i) => (
        <span
          key={i}
          className={highlightIndexes.includes(i) ? 'text-amount-300' : ''}
        >
          {char}
        </span>
      ));

  return (
    <div
      ref={ref}
      className="min-h-screen flex flex-col justify-center items-center p-[40px] gap-[40px] bg-gray-0"
    >
      <div ref={innerRef} className="flex flex-col items-center gap-[40px]">
        <img
          src="/assets/images/newlens-logo.png"
          alt="로고"
          className="w-[260px] h-[145px]"
        />
        <p className="display-small text-gray-950 text-center min-h-[3rem]">
          {renderTypedText()}
        </p>
        <Link
          to="/login"
          className="flex items-center justify-center border-2 border-amount-300 w-[270px] h-[60px] mt-[40px] rounded-[20px] text-gray-700 body-medium-bold hover:bg-amount-300 hover:text-gray-700"
        >
          <p className="text-gray-950">지금 시작하기</p>
        </Link>
      </div>
    </div>
  );
});

export default Section6;

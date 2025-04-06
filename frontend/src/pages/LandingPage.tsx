import Section1 from '../components/landing/Section1';
import Section2 from '../components/landing/Section2';
import Section3 from '../components/landing/Section3';
import Section4 from '../components/landing/Section4';
import Section5 from '../components/landing/Section5';
import Section6 from '../components/landing/Section6';
import {
  useRef,
  useState,
  useEffect,
  RefObject,
  WheelEvent as ReactWheelEvent,
} from 'react';

const LandingPage = () => {
  const sectionRefs: RefObject<HTMLDivElement>[] = [
    useRef(null),
    useRef(null),
    useRef(null),
    useRef(null),
    useRef(null),
    useRef(null),
  ];

  const [currentSectionIndex, setCurrentSectionIndex] = useState(0);
  const isScrolling = useRef(false);

  const handleWheel = (e: ReactWheelEvent) => {
    if (isScrolling.current) return;
    isScrolling.current = true;
    setTimeout(() => {
      // 한번에 여러개의 섹션이 넘겨지는 것 방지
      isScrolling.current = false;
    }, 400);

    if (e.deltaY > 50) {
      // 스크롤 강도에 따라 다음 섹션으로 이동
      scrollToNextSection();
      return;
    } else {
      scrollToPrevSection();
      return;
    }
  };

  const scrollToNextSection = () => {
    scrollToSection(currentSectionIndex + 1);
  };

  const scrollToPrevSection = () => {
    scrollToSection(currentSectionIndex - 1);
  };

  const scrollToSection = (index: number) => {
    if (index >= 0 && index < sectionRefs.length) {
      setCurrentSectionIndex(index);
      sectionRefs[index].current?.scrollIntoView({
        behavior: 'smooth',
      });
    }
  };

  useEffect(() => {
    if ('scrollRestoration' in window.history) {
      window.history.scrollRestoration = 'manual';
    }
  }, []);

  return (
    <div onWheel={handleWheel} className="bg-background overflow-hidden">
      <Section1 ref={sectionRefs[0]} onArrowClick={() => scrollToSection(1)} />
      <Section2 ref={sectionRefs[1]} />
      <Section3 ref={sectionRefs[2]} />
      <Section4 ref={sectionRefs[3]} />
      <Section5 ref={sectionRefs[4]} />
      <Section6 ref={sectionRefs[5]} />

      <img
        onClick={() => {
          setCurrentSectionIndex(0);
          sectionRefs[0].current?.scrollIntoView({ behavior: 'smooth' });
        }}
        src="/assets/images/scroll-to-top.png"
        alt="scroll to top"
        className="fixed bottom-[5vh] right-[5vw] w-[3vw] h-[3vw] cursor-pointer z-50"
      />

      {/* Dot Navigation */}
      <div className="fixed right-[2vw] top-1/2 -translate-y-1/2 z-50 flex flex-col gap-[16px]">
        {sectionRefs.map((_, i) => (
          <button
            key={i}
            onClick={() => scrollToSection(i)}
            className={`w-[8px] h-[8px] rounded-full cursor-pointer transition-all duration-300 ${
              currentSectionIndex === i
                ? 'bg-amount-300 scale-130'
                : 'bg-gray-300'
            }`}
          />
        ))}
      </div>
    </div>
  );
};

export default LandingPage;

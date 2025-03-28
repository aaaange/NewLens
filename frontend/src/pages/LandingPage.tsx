import Section1 from '../components/landing/Section1';
import Section2 from '../components/landing/Section2';
import Section3 from '../components/landing/Section3';
import Section4 from '../components/landing/Section4';
import Section5 from '../components/landing/Section5';
import Section6 from '../components/landing/Section6';
import {
  useRef,
  useState,
  RefObject,
  WheelEvent as ReactWheelEvent,
} from 'react';

const Landing = () => {
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

    if (e.deltaY > 0) {
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

  return (
    <div onWheel={handleWheel}>
      <Section1 ref={sectionRefs[0]} />
      <Section2 ref={sectionRefs[1]} />
      <Section3 ref={sectionRefs[2]} />
      <Section4 ref={sectionRefs[3]} />
      <Section5 ref={sectionRefs[4]} />
      <Section6 ref={sectionRefs[5]} />
    </div>
  );
};

export default Landing;

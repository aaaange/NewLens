import Lottie from 'lottie-react';
import animationData from '../landing/Animation - 1744177413689.json';

const LandingBgMap = () => {
  return (
    <div className="w-[1000px] h-[1000px]">
      <Lottie animationData={animationData} loop={true} />
    </div>
  );
};

export default LandingBgMap;

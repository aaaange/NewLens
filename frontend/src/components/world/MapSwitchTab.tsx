import React from 'react';
import Lottie from 'lottie-react';
import animationData from '../landing/Animation - 1742867463694.json'; // 경로는 네가 저장한 위치로

const MapSwitchTab = () => {
  return (
    <div className="w-[1000px] h-[1000px]">
      <Lottie animationData={animationData} loop={true} />
    </div>
  );
};

export default MapSwitchTab;

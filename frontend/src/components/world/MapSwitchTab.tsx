import React from 'react';
import Lottie from 'lottie-react';
import animationData from '../landing/Animation - 1742867463694.json';

interface MapSwitchTabProps {
  className?: string;
}

const MapSwitchTab = ({ className = '' }: MapSwitchTabProps) => {
  return (
    <div className="w-[1000px] h-[1000px]">
      <Lottie animationData={animationData} loop={true} />
    </div>
  );
};

export default MapSwitchTab;

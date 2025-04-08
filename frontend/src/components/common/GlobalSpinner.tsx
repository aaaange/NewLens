import { BeatLoader } from 'react-spinners';

const GlobalSpinner = () => {
  return (
    <div className="flex flex-col items-center justify-center mt-4 gap-2">
      <BeatLoader color="#ffc34a" speedMultiplier={0.5} />
      <div className="text-primary-300 opacity-60">
        데이터를 불러오고 있어요😊
      </div>
    </div>
  );
};

export default GlobalSpinner;

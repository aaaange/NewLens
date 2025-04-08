import { useState, useEffect } from 'react';
import { notify } from './Toast';

interface propsType {
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  onSearch: (keyword: string) => void;
  value: string;
}

const SearchInput = ({ onChange, onSearch, value }: propsType) => {
  const [inputValue, setInputValue] = useState(value);

  useEffect(() => {
    // 부모 컴포넌트로부터 전달된 value가 변경될 때 상태를 업데이트
    setInputValue(value);
  }, [value]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;

    // 영어 필터링 및 공백 체크 로직 추가
    if (/[a-zA-Z]/.test(newValue)) {
      notify({ type: 'warning', text: '영어는 입력할 수 없습니다.' });
      return;
    }

    if (newValue.includes(' ')) {
      notify({ type: 'warning', text: '단어 하나만 입력해 주세요.' });
      return;
    }

    // 상태 업데이트
    setInputValue(newValue);
    onChange(e);
  };

  const handleSearch = () => {
    if (inputValue.length < 2) {
      notify({ type: 'warning', text: '두 글자 이상 입력해 주세요.' });
      return;
    }
    onSearch(''); // 한 글자가 아닌 경우에만 검색 실행
  };

  return (
    <div>
      <div
        className={`relative flex items-center w-[305px] h-[50px] overflow-hidden`}
      >
        <input
          className={`w-full h-full pl-5 pr-10 border text-primary-950 border-gray-500 outline-none rounded-[20px] placeholder-primary-900 body-medium bg-gray-0`}
          placeholder="궁금한 키워드를 검색하세요!"
          type="text"
          value={value}
          onChange={handleChange}
          // onKeyDown={(e) => {
          //   if (e.key === 'Enter') {
          //     handleSearch();
          //   }
          // }}
        />
        <button
          onClick={() => handleSearch()}
          className="absolute right-2 top-1/2 transform -translate-y-1/2 cursor-pointer"
        >
          <svg
            fill="#011728"
            viewBox="0 0 30 30"
            height="22"
            width="22"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path d="M 13 3 C 7.4889971 3 3 7.4889971 3 13 C 3 18.511003 7.4889971 23 13 23 C 15.396508 23 17.597385 22.148986 19.322266 20.736328 L 25.292969 26.707031 A 1.0001 1.0001 0 1 0 26.707031 25.292969 L 20.736328 19.322266 C 22.148986 17.597385 23 15.396508 23 13 C 23 7.4889971 18.511003 3 13 3 z M 13 5 C 17.430123 5 21 8.5698774 21 13 C 21 17.430123 17.430123 21 13 21 C 8.5698774 21 5 17.430123 5 13 C 5 8.5698774 8.5698774 5 13 5 z"></path>
          </svg>
        </button>
      </div>
    </div>
  );
};
export default SearchInput;

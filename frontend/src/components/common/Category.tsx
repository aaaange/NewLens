import { useState } from 'react';

interface PropsType {
  categoryChangeHandler: (category: string) => void;
  periodChangeHandler: (period: number) => void;
  isCategory: string;
  isPeriod: number;
}

const Category = ({
  categoryChangeHandler,
  periodChangeHandler,
  isCategory,
  isPeriod,
}: PropsType) => {
  const categories = [
    { id: 'all', label: '전체' },
    { id: 'general', label: '일반' },
    { id: 'science', label: '과학' },
    { id: 'sports', label: '스포츠' },
    { id: 'business', label: '비즈니스' },
    { id: 'health', label: '헬스' },
    { id: 'entertainment', label: '엔터테인먼트' },
    { id: 'tech', label: '테크' },
    { id: 'politics', label: '정치' },
    { id: 'food', label: '식품' },
    { id: 'travel', label: '여행' },
  ];

  const periods = [
    { id: 1, label: '하루전' },
    { id: 7, label: '1주 전' },
    { id: 30, label: '1달 전' },
  ];

  const [selectedCategory, setSelectedCategory] = useState<string>(isCategory);
  const [selectedPeriod, setSelectedPeriod] = useState<number | null>(isPeriod);

  const handleCategoryClick = (categoryId: string) => {
    setSelectedCategory(categoryId);
    categoryChangeHandler(categoryId);
  };

  const handlePeriodClick = (periodId: number) => {
    setSelectedPeriod(periodId);
    periodChangeHandler(periodId);
  };

  return (
    <div className="w-full max-w-[938px] h-auto rounded-lg border border-primary-400">
      <div className="px-4 py-1.5">
        <div className="flex flex-col sm:flex-row items-start sm:items-center py-2">
          <div className="text-white caption-large mr-4 mb-2 sm:mb-0">
            카테고리
          </div>
          <div className="flex flex-wrap gap-3">
            {categories.map((category) => (
              <button
                key={category.id}
                onClick={() => handleCategoryClick(category.id)}
                className={`cursor-pointer px-4 py-2 rounded-full text-sm font-normal transition-colors ${
                  selectedCategory === category.id
                    ? 'bg-primary-500 text-white'
                    : 'bg-violet-50 text-neutral-800 hover:bg-tetiary-200'
                }`}
              >
                {category.label}
              </button>
            ))}
          </div>
        </div>

        <div className="flex flex-col sm:flex-row items-start sm:items-center py-2">
          <div className="text-white caption-large mr-4 mb-2 sm:mb-0">기간</div>
          <div className="ml-8 flex flex-wrap gap-6">
            {periods.map((period) => (
              <button
                key={period.id}
                onClick={() => handlePeriodClick(period.id)}
                className={`cursor-pointer text-sm transition-colors ${
                  selectedPeriod === period.id
                    ? 'font-semibold text-white'
                    : 'font-normal text-gray-300 hover:text-white'
                }`}
              >
                {period.label}
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Category;

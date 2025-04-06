import {
  ChevronFirst,
  ChevronLast,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';

interface PaginationPropsType {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
  onPageChange: (page: number) => void;
}

const Pagination = ({
  page,
  totalPages,
  hasNext,
  hasPrevious,
  onPageChange,
}: PaginationPropsType) => {
  const getPageNumbers = () => {
    const pageNumbers = [];
    const maxPagesToShow = 5;

    let startPage = Math.max(1, page - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage + 1 < maxPagesToShow) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i);
    }

    return pageNumbers;
  };

  return (
    <div className="flex justify-center items-center p-4 flex-wrap gap-1">
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(1)}
        disabled={!hasPrevious}
        aria-label="첫 페이지"
      >
        <ChevronFirst color="black" size={16} />
      </button>
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(page - 1)}
        disabled={!hasPrevious}
        aria-label="이전 페이지"
      >
        <ChevronLeft color="black" size={16} />
      </button>

      {getPageNumbers().map((pageNum) => (
        <button
          key={pageNum}
          className={`cursor-pointer w-8 h-8 flex items-center justify-center rounded-lg ${
            pageNum === page
              ? 'bg-slate-300 text-white'
              : 'bg-white text-zinc-800 border border-zinc-200 hover:bg-gray-100'
          }`}
          onClick={() => onPageChange(pageNum)}
          aria-label={`${pageNum} 페이지`}
          aria-current={pageNum === page ? 'page' : undefined}
        >
          {pageNum}
        </button>
      ))}

      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(page + 1)}
        disabled={!hasNext}
        aria-label="다음 페이지"
      >
        <ChevronRight color="black" size={16} />
      </button>
      <button
        className="cursor-pointer w-8 h-8 flex items-center justify-center bg-white rounded-lg border border-zinc-200 hover:bg-gray-100 disabled:opacity-50"
        onClick={() => onPageChange(totalPages)}
        disabled={!hasNext}
        aria-label="마지막 페이지"
      >
        <ChevronLast color="black" size={16} />
      </button>
    </div>
  );
};

export default Pagination;

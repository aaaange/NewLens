import { forwardRef } from 'react';
const Section4 = forwardRef<HTMLDivElement>((_, ref) => {
  return (
    <div ref={ref} className="h-screen border-b border-gray-400 p-[20px]">
      <div className="flex flex-col justify-center items-center gap-[40px] mt-[50px]">
        <p className="display-small">
          다양한 <span className="text-amount-300">글로벌 이슈</span>에 대한{' '}
          <span className="text-amount-300">G20</span> 국가의 반응은?
        </p>

        <div className="flex gap-[100px]">
          {/* 왼쪽 */}
          <div className="flex flex-col items-center">
            <div className="flex headline-small gap-[5px]">
              <img
                src={`https://flagcdn.com/us.svg`}
                width={24}
                height={12}
                alt="US Flag"
              />
              <p>
                <span className="headline-medium">미국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  감정 분석
                </span>
              </p>
            </div>
            <img
              src="/assets/images/us-columns.png"
              alt="미국 막대그래프"
              className="w-[280px] h-[180px]"
            />
            <div className="flex headline-small gap-[5px] mt-[40px]">
              <img
                src={`https://flagcdn.com/us.svg`}
                width={24}
                height={12}
                alt="US Flag"
              />
              <p>
                <span className="headline-medium">미국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  언급량 변화
                </span>
              </p>
            </div>
            <img
              src="/assets/images/us-mention.png"
              alt="미국 언급량그래프"
              className="w-[330px] h-[180px]"
            />
          </div>
          {/* 오른쪽 */}
          <div className="flex flex-col items-center">
            <div className="flex headline-small gap-[5px]">
              <img
                src={`https://flagcdn.com/kr.svg`}
                width={24}
                height={12}
                alt="KR Flag"
              />
              <p>
                <span className="headline-medium">한국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  감정 분석
                </span>
              </p>
            </div>
            <img
              src="/assets/images/kr-columns.png"
              alt="한국 막대그래프"
              className="w-[280px] h-[180px]"
            />
            <div className="flex headline-small gap-[5px] mt-[40px]">
              <img
                src={`https://flagcdn.com/kr.svg`}
                width={24}
                height={12}
                alt="KR Flag"
              />
              <p>
                <span className="headline-medium">한국</span>에서 본
                <span className="headline-medium text-amount-300">
                  {' '}
                  인공지능
                </span>
                에 대한
                <span className="headline-medium text-amount-300">
                  {' '}
                  언급량 변화
                </span>
              </p>
            </div>
            <img
              src="/assets/images/kr-mention.png"
              alt="한국 언급량 그래프"
              className="w-[330px] h-[180px]"
            />
          </div>
        </div>
      </div>
    </div>
  );
});

export default Section4;

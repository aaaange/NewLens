const Category = () => {
  return (
    <div className="w-[938px] h-24 relative">
      <div className="w-[938px] h-24 left-0 top-0 absolute rounded-lg outline outline-1 outline-offset-[-1px] outline-slate-400">
        <div className="w-[921px] h-20 left-[17px] top-[6px] absolute">
          <div className="w-[915px] h-12 left-0 top-0 absolute inline-flex justify-start items-center">
            <div className="justify-start text-white text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
              카테고리
            </div>
            <div className="w-[823px] h-12">
              <div className="size- opacity-0 bg-white rounded-[20px]" />
            </div>
          </div>
          <div className="size- left-0 top-[59px] absolute inline-flex justify-start items-center gap-20">
            <div className="justify-start text-white text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
              기간
            </div>
            <div className="w-56 self-stretch flex justify-start items-center gap-6">
              <div>하루전</div>
              <div>1주 전</div>
              <div>1달 전</div>
            </div>
          </div>
        </div>
      </div>
      <div className="size- left-[118px] top-[17px] absolute inline-flex justify-start items-center gap-1.5">
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            전체
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            일반
          </div>
        </div>
        <div
          data-state="Active"
          className="size- px-4 py-2 bg-slate-400 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-white text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            과학
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            스포츠
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            비즈니스
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            헬스
          </div>
        </div>
        <div
          data-state="Active"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-slate-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            엔터테인먼트
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            테크
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            정치
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            식품
          </div>
        </div>
        <div
          data-state="Default"
          className="size- px-4 py-2 bg-violet-50 rounded-[100px] flex justify-start items-start gap-2.5"
        >
          <div className="text-right justify-start text-neutral-800 text-sm font-normal font-['Nunito_Sans'] capitalize leading-none">
            여행
          </div>
        </div>
      </div>
    </div>
  );
};

export default Category;

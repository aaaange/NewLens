const MyPage = () => {
  return (
    <div className="flex m-15 gap-10">
      <div className="flex flex-col border-r-1 border-gray-300 w-1/4 gap-20">
        <h1 className="text-5xl">마이페이지</h1>
        <div className="flex flex-col text-2xl gap-5">
          <div>나의 활동</div>
          <div>추천 기사</div>
          <div>스크랩 NEWS</div>
        </div>
      </div>
      <div className="">
        <div>나의 정보</div>
      </div>
    </div>
  );
};

export default MyPage;

import NewsCarousel from './NewsCarousel';
import MyProfile from './MyProfile';


const MyActivities = () => {
  return (
    <div className="flex flex-col gap-10">
      <div>
        <MyProfile />
      </div>
      <div>
        <h2 className="text-2xl mb-4">최근 본 NEWS</h2>
        <NewsCarousel />
      </div>
    </div>
  );
};

export default MyActivities;

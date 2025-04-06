import { Typography } from '@mui/material';
import Carousel from 'react-material-ui-carousel';
import Flag from 'react-world-flags';

interface VideoItemType {
  title: string;
  url: string;
  published_at: string;
  thumbnail_url: string;
}

interface KoreaVideoListProps {
  videos: VideoItemType[];
  width: number;
  height: number;
  keyword: string;
  keyword_mind: string;
  country_name: string;
  country_code: string;
}

const chunkArray = (
  array: VideoItemType[],
  size: number
): VideoItemType[][] => {
  const chunked: VideoItemType[][] = [];
  for (let i = 0; i < array.length; i += size) {
    chunked.push(array.slice(i, i + size));
  }
  return chunked;
};

const KoreaVideoList = ({
  videos,
  width,
  keyword,
  keyword_mind,
  height,
  country_name,
  country_code,
}: KoreaVideoListProps) => {
  const videoChunks = chunkArray(videos, 3);

  const Flags = Flag as any;

  return (
    <>
      <div
        className="flex flex-col gap-2"
        style={{
          width: `${width}px`,
          // overflow: 'hidden',
        }}
      >
        <p className="flex items-center flex-wrap">
          <Flags code={country_code} width="24" height="12" /> &nbsp;
          <span className="text-lg "> {country_name}</span>에서 본&nbsp;
          <span className="text-amount-300 text-lg ">{keyword}</span>의
          관련&nbsp;
          <span className="text-amount-300 text-lg "> 유튜브</span>
        </p>
        <div className="space-y-2 border-2 border-gray-500 rounded-lg">
          <Carousel
            cycleNavigation
            navButtonsAlwaysVisible
            fullHeightHover={false} // 인디케이터 컨테이너를 절대 배치하여 아래쪽에 오도록
            indicatorContainerProps={{
              style: {
                position: 'absolute',
                bottom: 10,
                left: 0,
                width: '100%',
                display: 'flex',
                justifyContent: 'center',
                zIndex: 2,
              },
            }}
            // 화살표(버튼)도 이미지 위에 오도록
            navButtonsWrapperProps={{
              style: {
                position: 'absolute',
                top: 'calc(50% - 38px)', // 세로 가운데쯤
                zIndex: 3,
              },
            }}
          >
            {videoChunks.map((chunk, idx) => (
              <div
                key={idx}
                className="flex justify-center gap-4 p-2"
                // style={{
                //   display: 'flex',
                //   flexDirection: 'column', // 텍스트와 이미지를 위아래로 배치
                //   alignItems: 'center', // 수직 방향(주축이 세로일 때) 중앙정렬
                //   justifyContent: 'center', // 수평 방향(축이 가로일 때) 중앙정렬
                // }}
              >
                {chunk.map((content) => (
                  <a
                    key={content.url}
                    href={content.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex flex-col items-center w-1/3"
                  >
                    <img
                      src={content.thumbnail_url}
                      alt={content.title}
                      style={{
                        width: '100%',
                        height: '100%',
                        borderRadius: 4,
                      }}
                    />
                  </a>
                ))}
              </div>
            ))}
          </Carousel>
        </div>
      </div>
    </>
  );
};

export default KoreaVideoList;

import Chart from 'react-apexcharts';
import Flag from 'react-world-flags';
import { ApexOptions } from 'apexcharts';

interface MentionData {
  period: string;
  count: number;
}

interface MentionChartProps {
  data: MentionData[];
  width: number;
  height: number;
  keyword: string;
  country_name: string;
  country_code: string;
}

const MentionChart = ({
  data,
  width,
  height,
  keyword,
  country_name,
  country_code,
}: MentionChartProps) => {
  // x축과 y축 데이터를 변환
  const categories = data.map((item) => item.period);
  const seriesData = data.map((item) => item.count);

  const options: ApexOptions = {
    chart: {
      type: 'area',
      background: 'transparent',
      toolbar: {
        show: false, // 툴바 비활성화
      },
    },
    dataLabels: {
      enabled: false, // 데이터 라벨(숫자 박스) 숨기기
    },

    xaxis: {
      categories: categories,
      labels: {
        style: {
          colors: '#fff', // X축 라벨 색상 (화이트)
        },
      },
    },
    yaxis: {
      labels: {
        style: {
          colors: '#fff', // Y축 라벨 색상 (화이트)
        },
      },
    },
    grid: {
      borderColor: 'rgba(255,255,255,0.05)', // 격자 선을 흐리게
    },
    stroke: {
      curve: 'smooth',
      width: 2,
      colors: ['#ffffff'], // 라인 색상
    },
    fill: {
      type: 'gradient',
      gradient: {
        shade: 'dark',
        type: 'vertical',
        gradientToColors: ['rgba(255, 255, 255, 0.1)'], // 반투명 그라디언트
        stops: [0, 100],
      },
    },
    tooltip: {
      theme: 'dark', // 툴팁 다크 테마 적용
    },
  };

  const series: { name: string; data: number[] }[] = [
    {
      name: '언급량',
      data: seriesData,
    },
  ];

  return (
    <div className="flex flex-col gap-2 chart-container" style={{ width: `${width}px` }}>
      <p className="flex items-center flex-wrap">
        <Flag code={country_code} width={24} height={12} /> &nbsp;
        {country_name}에서 본&nbsp;
        <span className="text-amount-300 text-lg ">{keyword}</span>에 대한&nbsp;
        <span className="text-amount-300 text-lg ">언급량 변화</span>
      </p>
      <Chart
        options={options}
        series={series}
        type="area"
        width={width}
        height={height}
      />
    </div>
  );
};

export default MentionChart;

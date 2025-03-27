import Chart from 'react-apexcharts';
import Flag from 'react-world-flags';
import { ApexOptions } from 'apexcharts';

interface SentimentData {
  published_at: string;
  positive: number;
  neutral: number;
  negative: number;
}

interface StackedColumnChartProps {
  data: SentimentData[];
  width: number;
  height: number;
  keyword: string;
  country_name: string;
  country_code: string;
}

const StackedColumns = ({
  data,
  width,
  height,
  keyword,
  country_name,
  country_code,
}: StackedColumnChartProps) => {
  const options: ApexOptions = {
    chart: {
      type: 'bar',
      stacked: true,
      stackType: '100%',
      toolbar: {
        show: false, // 햄버거 버튼 없애기
      },
    },
    dataLabels: {
      enabled: false, // 차트 위 숫자 숨기기
    },
    xaxis: {
      labels: {
        style: {
          colors: '#FFFFFF',
          fontSize: '12px',
          fontWeight: 500,
        },
      },
      categories: data.map((item) => item.published_at),
    },
    yaxis: {
      labels: {
        style: {
          colors: '#FFFFFF',
          fontSize: '12px',
          fontWeight: 500,
        },
        formatter: (value: number) => `${value}%`,
      },
    },
    colors: ['#5279BD', '#D3E67E', '#E2695C'],
    tooltip: {
      theme: 'dark', // 툴팁 테마 변경 (light, dark)
      style: {
        fontSize: '14px',
        fontWeight: 'bold',
        // colors: ['#F1C40F'], // 툴팁 글씨 색상 (예: 노란색)
      },
      marker: {
        fillColors: ['#5279BD', '#D3E67E', '#E2695C'], // 툴팁 마커 색상
      },
      y: {
        formatter: (val: number) => `${val.toFixed(1)}%`,
      },
    },
    legend: {
      labels: {
        colors: '#FFFFFF',
      },
    },
  };

  const series: { name: string; data: number[] }[] = [
    { name: 'Positive', data: data.map((item) => item.positive * 100) },
    { name: 'Neutral', data: data.map((item) => item.neutral * 100) },
    { name: 'Negative', data: data.map((item) => item.negative * 100) },
  ];

  return (
    <div className="flex flex-col gap-2" style={{ width: `${width}px` }}>
      <p className="flex items-center flex-wrap">
        <Flag code={country_code} width="24" height="12" /> &nbsp;
        {country_name}에서 본&nbsp;
        <span className="text-amount-300 text-lg ">{keyword}</span>에 대한&nbsp;
        <span className="text-amount-300 text-lg ">감정 분석</span>
      </p>
      <Chart
        options={options}
        series={series}
        type="bar"
        width={width}
        height={height}
      />
    </div>
  );
};

export default StackedColumns;

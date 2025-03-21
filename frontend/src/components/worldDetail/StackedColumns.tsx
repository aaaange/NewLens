import Chart from 'react-apexcharts';
import Flag from 'react-world-flags';
import { SentimentData } from '../../pages/WorldDetail';
import { ApexOptions } from 'apexcharts';

interface StackedColumnChartProps {
  data: SentimentData[];
  width: number;
  height: number;
  keyword: string;
  country_name: string;
}

const StackedColumnChart = ({
  data,
  width,
  height,
  keyword,
  country_name,
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
      categories: data.map((item) => item.period),
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
    <>
      <p className="flex">
        <Flag code="US" width="24" height="12" /> &nbsp;
        {country_name}에서 본&nbsp;
        <span className="text-system-warning">{keyword}</span>에 대한&nbsp;
        <span className="text-system-warning">감정 분석</span>
      </p>
      <Chart
        options={options}
        series={series}
        type="bar"
        width={width}
        height={height}
      />
    </>
  );
};

export default StackedColumnChart;

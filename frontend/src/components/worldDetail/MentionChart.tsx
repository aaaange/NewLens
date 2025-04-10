import Chart from 'react-apexcharts';
import Flag from 'react-world-flags';
import { ApexOptions } from 'apexcharts';
import { formatDate } from '../../utils/formatDateUtils';

interface MentionData {
  published_at: string;
  count: number;
}

interface MentionChartProps {
  data: MentionData[];
  width: number;
  height: number;
  keyword: string;
  keyword_mind: string;
  country_name: string;
  country_code: string;
}

const MentionChart = ({
  data,
  width,
  height,
  keyword,
  keyword_mind,
  country_name,
  country_code,
}: MentionChartProps) => {
  // x축과 y축 데이터를 변환
  const categories = data.map((item) =>
    formatDate(item.published_at, 'perHour')
  );
  const seriesData = data.map((item) => item.count);
  const FixedFlag = Flag as any;

  const isSingle = data.length === 1;

  const options: ApexOptions = isSingle
    ? {
        chart: {
          type: 'bar',
          background: 'transparent',
          toolbar: { show: false },
        },
        plotOptions: {
          bar: {
            borderRadius: 4,
            columnWidth: '40%',
          },
        },
        dataLabels: { enabled: false },
        xaxis: {
          categories,
          labels: {
            style: { colors: '#fff' },
          },
        },
        yaxis: {
          labels: {
            style: { colors: '#fff' },
          },
        },
        grid: {
          borderColor: 'rgba(255,255,255,0.05)',
        },
        tooltip: { theme: 'dark' },
        colors: undefined, // 막대 색상
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'dark',
            type: 'vertical',
            gradientToColors: ['rgba(255, 255, 255, 0.1)'],
            stops: [0, 100],
          },
        },
      }
    : {
        chart: {
          type: 'area',
          background: 'transparent',
          toolbar: { show: false },
        },
        dataLabels: { enabled: false },
        xaxis: {
          categories,
          labels: {
            style: { colors: '#fff' },
          },
        },
        yaxis: {
          labels: {
            style: { colors: '#fff' },
          },
        },
        grid: {
          borderColor: 'rgba(255,255,255,0.05)',
        },
        stroke: {
          curve: 'smooth',
          width: 2,
          colors: ['#ffffff'],
        },
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'dark',
            type: 'vertical',
            gradientToColors: ['rgba(255, 255, 255, 0.1)'],
            stops: [0, 100],
          },
        },
        tooltip: { theme: 'dark' },
      };

  const series: { name: string; data: number[] }[] = [
    {
      name: '언급량',
      data: seriesData,
    },
  ];
  const headerString = [keyword, keyword_mind]
    .filter((item) => item && item.trim() !== '') // 빈 문자열 또는 undefined/null 제거
    .join(' > '); // ' > '로 연결

  return (
    <div
      className="flex flex-col gap-2 chart-container"
      style={{ width: `${width}px` }}
    >
      <p className="flex items-center flex-wrap">
        <FixedFlag code={country_code} width={24} height={12} /> &nbsp;
        {country_name}에서 본&nbsp;
        <span className="text-amount-300 text-lg ">{headerString}</span>에
        대한&nbsp;
        <span className="text-amount-300 text-lg ">언급량 변화</span>
      </p>
      <Chart
        options={options}
        series={series}
        type={isSingle ? 'bar' : 'area'}
        width={width}
        height={height}
      />
    </div>
  );
};

export default MentionChart;

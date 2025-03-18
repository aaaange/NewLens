import React from 'react';
import Chart from 'react-apexcharts';

const StackedColumnChart = ({ data, width = '100%', height = 400 }) => {
  const options = {
    chart: {
      type: 'bar',
      stacked: true,
      stackType: '100%',
      toolbar: {
        show: false, // 햄버거 버튼 없애기
      },
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
        formatter: (value) => `${value}%`,
      },
    },
    colors: ['#4CAF50', '#FFC107', '#F44336'],
    tooltip: {
      theme: 'dark', // ✅ 툴팁 테마 변경 (light, dark)
      style: {
        fontSize: '14px',
        fontWeight: 'bold',
        colors: ['#F1C40F'], // ✅ 툴팁 글씨 색상 (예: 노란색)
      },
      marker: {
        fillColors: ['#4CAF50', '#FFC107', '#F44336'], // ✅ 툴팁 마커 색상
      },
      y: {
        formatter: (val) => `${val.toFixed(1)}%`,
      },
    },
    legend: {
      labels: {
        colors: '#FFFFFF',
      },
    },
  };

  const series = [
    { name: 'Positive', data: data.map((item) => item.positive * 100) },
    { name: 'Neutral', data: data.map((item) => item.neutral * 100) },
    { name: 'Negative', data: data.map((item) => item.negative * 100) },
  ];

  return (
    <Chart
      options={options}
      series={series}
      type="bar"
      width={typeof width === 'number' ? `${width}px` : width}
      height={typeof height === 'number' ? `${height}px` : height}
    />
  );
};

export default StackedColumnChart;

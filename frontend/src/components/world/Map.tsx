import React, { useEffect, useRef, useState } from 'react';
import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import am5geodata_worldLow from '@amcharts/amcharts5-geodata/worldLow';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';

interface MentionDataType {
  [key: string]: number;
}

interface countryNameType {
  [key: string]: string;
}

interface WorldMapProps {
  tabId: string;
}

const WorldMap = ({ tabId }: WorldMapProps) => {
  console.log('tabId', tabId);

  const chartContainerRef = useRef<HTMLDivElement>(null); // 차트 컨테이너 ref
  const chartRef = useRef<am5.Root>(null); // amCharts 인스턴스 저장
  const [mentionData, setMentionData] = useState<MentionDataType | null>(null); // API에서 받아올 데이터 상태
  // const [nation, setNation] = useState<string>(''); // 현재 선택한 국가 상태 추가

  // worldData.json에서 로컬 데이터 가져오기
  const fetchWorldData = async () => {
    try {
      const response = await fetch('/worldData.json'); // JSON 데이터 가져오기
      const jsonData = await response.json();
      console.log('jsonData', jsonData);

      // response형태를 배열에서 객체로 변환(검색 시, 속도 차이)
      const mentionObj: MentionDataType = {};
      jsonData.data.mention.forEach((item: MentionDataType) => {
        mentionObj[item.name] = item.count;
      });

      setMentionData(mentionObj); // 상태 업데이트
    } catch (error) {
      console.error('API 데이터 가져오기 실패:', error);
    }
  };

  useEffect(() => {
    fetchWorldData(); // 호출
  }, []);

  useEffect(() => {
    if (!chartContainerRef.current) return; // 데이터가 없으면 실행 X     ////////////////////////////////////////////////////////////////////////////

    const countryName: countryNameType = {
      'South Korea': 'kr',
      'United States': 'us',
      'United Kingdom': 'gb',
      Japan: 'jp',
      China: 'cn',
      Germany: 'de',
      France: 'fr',
      Italy: 'it',
      Spain: 'es',
      Canada: 'ca',
      Australia: 'au',
      Brazil: 'br',
      India: 'in',
      Russia: 'ru',
      Mexico: 'mx',
      Turkey: 'tr',
      'South Africa': 'za',
      Philippines: 'ph',
      Indonesia: 'id',
      Argentina: 'ar',
    };

    // 핵심 amCharts 루트 요소 생성
    const root = am5.Root.new(chartContainerRef.current);

    // amCharts 5 테마 설정(애니메이션 등)
    root.setThemes([am5themes_Animated.new(root)]);

    // 지도 차트 생성해서 root의 컨테이너에 푸시
    const chart = root.container.children.push(
      am5map.MapChart.new(root, {
        panX: 'translateX',
        panY: 'translateY',
        projection: am5map.geoMercator(),
      })
    );

    // 국가별 폴리곤(경계선) 시리즈 추가
    const polygonSeries = chart.series.push(
      am5map.MapPolygonSeries.new(root, {
        geoJSON: am5geodata_worldLow,
        exclude: ['AQ'], // 남극(AQ) 제외
      })
    );

    // console.log('mentionData', mentionData);

    // 언급량에 따른 색상 설정 함수
    const getColorByMention = (count: number): string => {
      if (count <= 90) return '#E2695C'; // 매우 낮음
      if (count <= 110) return '#5279BD'; // 낮음
      if (count <= 120) return '#BBFF00'; // 보통
      if (count <= 130) return '#80BF56'; // 높음
      if (count > 130) return '#FFD700'; // 매우 높음
      return '#E0E0E0'; // 기본 설정(그레이)
    };

    // 폴리곤 데이터가 준비되면 실행
    polygonSeries.events.on('datavalidated', () => {
      polygonSeries.mapPolygons.each((polygon) => {
        const fullName = (polygon.dataItem?.dataContext as { name: string })
          .name;
        const shortName = countryName[fullName];

        if (!mentionData) return;
        let mentionCount = mentionData[shortName];

        (polygon.dataItem?.dataContext as { shortName: string }).shortName =
          shortName; // shortName 추가
        (
          polygon.dataItem?.dataContext as { mentionCount: number }
        ).mentionCount = mentionCount; // mention 추가

        // 데이터가 있는 국가의 경우에만 툴팁에 언급량 추가
        polygon.set(
          'tooltipText',
          mentionCount ? `${fullName}\n(언급량: {mentionCount})` : `${fullName}`
        );

        // 언급량에 따른 컬러 설정
        polygon.set('fill', am5.color(getColorByMention(mentionCount)));
      });
    });

    // 폴리곤(국가) 기본 스타일 설정
    polygonSeries.mapPolygons.template.setAll({
      toggleKey: 'active',
      interactive: true,
      strokeWidth: 0.2,
      stroke: am5.color('#011728'),
    });

    // console.log('언급 데이터', mentionData);

    // Hover 및 Active 상태 정의
    polygonSeries.mapPolygons.template.states.create('hover', {
      // fill: root.interfaceColors.get('primaryButtonHover'),
      stroke: am5.color('#FFFFFF'),
      strokeWidth: 4,
      scale: 1.02,
      cursorOverStyle: 'pointer',
    });

    polygonSeries.mapPolygons.template.states.create('active', {
      fill: root.interfaceColors.get('primaryButtonHover'),
      cursorOverStyle: 'pointer',
    });

    // 호버시 zindex 설정
    polygonSeries.mapPolygons.template.events.on('pointerover', (event) => {
      event.target.toFront();
    });

    // console.log('states!!!!', polygonSeries.mapPolygons.template.states);

    // 이전 클릭한 폴리곤 저장용 변수
    let previousPolygon: am5map.MapPolygon | null = null;

    // 국가 클릭 시 확대 / 홈 이동 로직
    polygonSeries.mapPolygons.template.on('active', (active, target) => {
      if (previousPolygon && previousPolygon !== target) {
        previousPolygon.set('active', false);
      }
      if (target && target.get('active')) {
        polygonSeries.zoomToDataItem(
          target.dataItem as am5.DataItem<am5map.IMapPolygonSeriesDataItem>
        );
      } else {
        chart.goHome();
      }
      previousPolygon = target ?? null;
    });

    // 📌 줌 컨트롤 추가
    const zoomControl = chart.set(
      'zoomControl',
      am5map.ZoomControl.new(root, {})
    );
    zoomControl.homeButton.set('visible', true);

    // 📌 지도 바탕 클릭 시 원래 위치로 이동
    chart.chartContainer.get('background')!.events.on('click', () => {
      chart.goHome();
    });

    // 📌 애니메이션 효과 추가
    chart.appear(1000, 100);

    // 📌 차트 인스턴스 저장
    chartRef.current = root;

    // 📌 Cleanup: 컴포넌트 언마운트 시 차트 제거 (메모리 누수 방지)
    return () => {
      if (chartRef.current) {
        chartRef.current.dispose();
        chartRef.current = null;
      }
    };
  }, [mentionData]);

  return (
    <div
      ref={chartContainerRef}
      style={{ width: '1000px', height: '600px', margin: '0 auto' }}
    />
  );
};

export default WorldMap;

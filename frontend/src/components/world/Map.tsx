import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import am5geodata_worldLow from '@amcharts/amcharts5-geodata/worldLow';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  WorldMapProps,
  worldMentionType,
  countryNameType,
  SentimentName,
  sentimentType,
  sentimentResType,
  worldSentimentType,
  getWorldMapData,
} from '../../services/api/worldService';

const WorldMap = ({ tabId, category, period, keyword }: WorldMapProps) => {
  // console.log('tabId', tabId);
  // console.log('category', category);
  // console.log('period', period);
  // console.log('keyword', keyword);

  const chartContainerRef = useRef<HTMLDivElement>(null); // 차트 컨테이너 ref
  const chartRef = useRef<am5.Root | null>(null);
  const [mentionData, setMentionData] = useState<worldMentionType | null>(null);
  const [sentimentData, setSentimentData] = useState<worldSentimentType | null>(
    null
  );
  const navigate = useNavigate();

  //===========================================================================
  // 데이터 가져오기
  //===========================================================================
  const fetchWorldData = async () => {
    try {
      const response = await fetch('/worldData.json');
      const jsonData = await response.json();
      // const jsonData = await getWorldMapData();
      // console.log('jsonData', jsonData);

      const { mention, sentiment } = jsonData.data;

      //===========================================================================
      // 언급량 데이터 response 변환 => response형태를 배열에서 객체로 변환(검색 시, 속도 차이)
      //===========================================================================

      const mentionObj: worldMentionType = {};
      mention.forEach((item: worldMentionType) => {
        mentionObj[item.name] = item.count;
      });
      setMentionData(mentionObj);
      // console.log('mentionData', mentionData);

      //===========================================================================
      // 긍부정 데이터 response 변환
      //===========================================================================

      // primarySentiment 찾는 함수
      const getPrimarySentiment = (
        positive: number,
        neutral: number,
        negative: number
      ): SentimentName => {
        if (positive >= neutral && positive > negative) return 'positive';
        if (negative >= neutral && negative > positive) return 'negative';
        return 'neutral';
      };

      const sentimentObj: worldSentimentType = {};

      sentiment.forEach(
        ({ name, positive, neutral, negative }: sentimentResType) => {
          const primarySentiment = getPrimarySentiment(
            positive,
            neutral,
            negative
          );

          sentimentObj[name] = {
            positive,
            neutral,
            negative,
            primarySentiment,
          };
        }
      );

      setSentimentData(sentimentObj);
      // console.log('sentimentData', sentimentData);
    } catch (error) {
      console.error('API 데이터 가져오기 실패:', error);
    }
  };

  useEffect(() => {
    fetchWorldData();
  }, []);

  useEffect(() => {
    if (!chartContainerRef.current) return; // 데이터가 없으면 실행 X

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
    //===========================================================================
    // 기본 설정
    //===========================================================================

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

    // 폴리곤(국가) 기본 스타일 설정
    polygonSeries.mapPolygons.template.setAll({
      toggleKey: 'active',
      // interactive: true,
      fill: am5.color('#D5DCE8'),
      strokeWidth: 0.01,
      stroke: am5.color('#011728'),
    });

    // 호버시 zindex 설정
    polygonSeries.mapPolygons.template.events.on('pointerover', (event) => {
      event.target.toFront();
    });

    // 이전 클릭한 폴리곤 저장용 변수
    let previousPolygon: am5map.MapPolygon | null = null;

    // 국가 클릭 시 확대 및 상세 페이지 이동 / 홈 이동 로직
    polygonSeries.mapPolygons.template.on('active', (active, target) => {
      if (previousPolygon && previousPolygon !== target) {
        previousPolygon.set('active', false);
      }

      if (target) {
        const dataContext = target.dataItem?.dataContext as {
          shortName?: string;
        };
        const shortName = dataContext?.shortName;

        if (shortName) {
          if (shortName === 'ru') {
            chart.zoomToGeoPoint({ latitude: 60, longitude: 90 }, 3, true);
          } else {
            polygonSeries.zoomToDataItem(
              target.dataItem as am5.DataItem<am5map.IMapPolygonSeriesDataItem>
            );
          }
          setTimeout(() => {
            // navigate(`/worldDetail/${shortName}/${category}/${period}/${keyword}`);
            navigate(`/worldDetail/${shortName}/${category}/${period}/${keyword || 'default'}`);
            window.scrollTo(0, 0); //
            // navigate(`/worldDetail/`);
          }, 1000);
        }
      } else {
        chart.goHome();
      }

      previousPolygon = target ?? null;
    });

    // 줌 컨트롤
    const zoomControl = chart.set(
      'zoomControl',
      am5map.ZoomControl.new(root, {})
    );
    zoomControl.homeButton.set('visible', true);

    // 지도 바탕 클릭 시 원래 위치로 이동
    chart.chartContainer.get('background')!.events.on('click', () => {
      chart.goHome();
    });

    // 지도 렌더링 시, 애니메이션 효과
    chart.appear(1000, 100);

    // 툴팁 설정
    const tooltip = am5.Tooltip.new(root, {
      getFillFromSprite: false,
      autoTextColor: false,
    });

    tooltip.get('background')?.setAll({
      fill: am5.color(0x00000),
      fillOpacity: 0.8,
      stroke: am5.color(0x00000),
      strokeOpacity: 0.8,
    });

    tooltip.label.setAll({
      fill: am5.color(0xffffff),
    });

    polygonSeries.set('tooltip', tooltip);

    //===========================================================================
    // 언급량, 긍부정에 따른 설정
    //===========================================================================

    // 언급량에 따른 색상 설정 함수
    const getColorByMention = (count: number): string => {
      if (count <= 90) return '#FFF9EB'; // 매우 낮음(50)
      if (count <= 110) return '#FFEEC6'; // 낮음(100)
      if (count <= 120) return '#FFC34A'; // 보통(300)
      if (count <= 130) return '#FFAA20'; // 높음(400)
      if (count > 130) return '#F98607'; // 매우 높음(500)
      return '#E0E0E0'; // 기본 설정(그레이)
    };

    // 긍부정에 따른 색상 설정 함수
    const getColorBySentiment = (primarySentiment: string): string => {
      if (primarySentiment === 'positive') return '#5279BD'; // 긍정
      if (primarySentiment === 'neutral') return '#BBFF00'; // 중립
      if (primarySentiment === 'negative') return '#E2695C'; // 부정
      return '#E0E0E0';
    };

    // 폴리곤 데이터가 준비되면 실행
    polygonSeries.events.on('datavalidated', () => {
      polygonSeries.mapPolygons.each((polygon) => {
        const fullName = (polygon.dataItem?.dataContext as { name: string })
          .name;

        const shortName = countryName[fullName];
        (polygon.dataItem?.dataContext as { shortName: string }).shortName =
          shortName; // shortName 추가

        //===========================================================================
        // 언급량 관련 설정
        //===========================================================================
        if (!mentionData) return;
        const mentionCount = mentionData[shortName];

        (
          polygon.dataItem?.dataContext as { mentionCount: number }
        ).mentionCount = mentionCount; // mention 추가

        //===========================================================================
        // 감정 관련 설정
        //===========================================================================
        if (!sentimentData) return;
        const sentiment = sentimentData[shortName];
        if (!sentiment) return; // 해당 국가 데이터 없으면 return
        const { positive, neutral, negative, primarySentiment } = sentiment;

        //===========================================================================
        // props로 내려 받은 tab에 따른 컬러와 툴팁 설정
        //===========================================================================
        if (
          mentionData?.[shortName] !== undefined ||
          sentimentData?.[shortName] !== undefined
        ) {
          polygon.set('cursorOverStyle', 'pointer');
          polygon.states.create('hover', {
            stroke: am5.color('#FFFFFF'),
            strokeWidth: 4,
            scale: 1.02,
          });
        }

        if (tabId === 'mention') {
          polygon.set('fill', am5.color(getColorByMention(mentionCount)));
          polygon.set(
            'tooltipText',
            mentionCount
              ? `${fullName}\n(언급량: {mentionCount})`
              : `${fullName}`
          );
          // legend.data.setAll([
          //   {
          //     name: '매우 낮음',
          //     color: am5.color('#FFEEC6'),
          //   },
          //   {
          //     name: '낮음',
          //     color: am5.color('#FFD677'),
          //   },
          //   {
          //     name: '보통',
          //     color: am5.color('#FFAA20'),
          //   },
          //   {
          //     name: '높음',
          //     color: am5.color('#F98607'),
          //   },
          //   {
          //     name: '매우 높음',
          //     color: am5.color('#DD6102'),
          //   },
          // ]);
        } else {
          polygon.set('fill', am5.color(getColorBySentiment(primarySentiment)));
          polygon.set(
            'tooltipText',
            sentiment
              ? `${fullName}\n긍정: ${positive}\n중립: ${neutral}\n부정: ${negative}`
              : `${fullName}`
          );
          // // 범례에 표시할 데이터 설정
          // legend.data.setAll([
          //   {
          //     name: '긍정',
          //     color: am5.color('#5279BD'),
          //   },
          //   {
          //     name: '중립',
          //     color: am5.color('#BBFF00'),
          //   },
          //   {
          //     name: '부정',
          //     color: am5.color('#E2695C'),
          //   },
          // ]);
        }
      });
    });

    // 차트 인스턴스 저장
    chartRef.current = root;

    // 컴포넌트 언마운트 시 차트 제거 (메모리 누수 방지)
    return () => {
      if (chartRef.current) {
        chartRef.current.dispose();
        chartRef.current = null;
      }
    };
  }, [mentionData, sentimentData, tabId]);

  return (
    <div ref={chartContainerRef} className="w-[930px] h-[800px] mx-auto" />
  );
};

export default WorldMap;

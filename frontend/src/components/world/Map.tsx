import * as am5 from '@amcharts/amcharts5';
import * as am5map from '@amcharts/amcharts5/map';
import am5geodata_worldLow from '@amcharts/amcharts5-geodata/worldLow';
import am5themes_Animated from '@amcharts/amcharts5/themes/Animated';
import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  WorldMapData,
  mentionResType,
  mentionObjType,
  countryNameType,
  sentimentResType,
  SentimentName,
  sentimentObjType,
} from '../../services/api/worldService';

interface WorldMapProps {
  tabId: string;
  category: string;
  period: number;
  keyword: string;
  mapData?: WorldMapData;
  keyword_mind: string;
}

const Map = ({
  tabId,
  category,
  period,
  keyword,
  mapData,
  keyword_mind,
}: WorldMapProps) => {
  const chartContainerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<am5.Root | null>(null);
  const [mentionData, setMentionData] = useState<mentionObjType | null>(null);
  const [sentimentData, setSentimentData] = useState<sentimentObjType | null>(
    null
  );
  const navigate = useNavigate();

  useEffect(() => {
    // mapData가 아직 없다면 처리하지 않음
    if (!mapData) return;

    // 구조 분해 할당
    const {
      mention,
      sentiment,
    }: {
      mention: mentionResType;
      sentiment: sentimentResType;
    } = mapData;

    // 언급량 데이터 가공: 배열 -> 객체로 변환
    const mentionObj: { [key: string]: number } = {};
    mention.forEach((item: { country: string; count: number }) => {
      mentionObj[item.country] = item.count;
    });
    setMentionData(mentionObj);

    // 긍부정 데이터 가공
    const sentimentObj: sentimentObjType = {};

    // primarySentiment 판별 함수
    const getPrimarySentiment = (
      positive: number,
      neutral: number,
      negative: number
    ): SentimentName => {
      if (positive >= neutral && positive > negative) return 'positive';
      if (negative >= neutral && negative > positive) return 'negative';
      return 'neutral';
    };

    sentiment.forEach(
      ({
        country,
        positive,
        neutral,
        negative,
      }: {
        country: string;
        positive: number;
        neutral: number;
        negative: number;
      }) => {
        const primarySentiment = getPrimarySentiment(
          positive,
          neutral,
          negative
        );
        sentimentObj[country] = {
          positive,
          neutral,
          negative,
          primarySentiment,
        };
      }
    );

    setSentimentData(sentimentObj);
  }, [mapData]);

  useEffect(() => {
    if (!chartContainerRef.current) return; // 데이터가 없으면 실행 X

    const countryName: countryNameType = {
      'South Korea': 'KR',
      'United States': 'US',
      'United Kingdom': 'GB',
      Japan: 'JP',
      China: 'CN',
      Germany: 'DE',
      France: 'FR',
      Italy: 'IT',
      Spain: 'ES',
      Canada: 'CA',
      Australia: 'AU',
      Brazil: 'BR',
      India: 'IN',
      Russia: 'RU',
      Mexico: 'MX',
      // Turkey: 'TR',
      'South Africa': 'ZA',
      'Saudi Arabia': 'SA',
      // Indonesia: 'ID',
      Argentina: 'AR',
    };
    //===========================================================================
    // 기본 설정
    //===========================================================================
    const root = am5.Root.new(chartContainerRef.current);

    root.setThemes([am5themes_Animated.new(root)]);

    const chart = root.container.children.push(
      am5map.MapChart.new(root, {
        panX: 'translateX',
        panY: 'translateY',
        projection: am5map.geoMercator(),
      })
    );

    const polygonSeries = chart.series.push(
      am5map.MapPolygonSeries.new(root, {
        geoJSON: am5geodata_worldLow,
        exclude: ['AQ'], // 남극(AQ) 제외
      })
    );

    // 기본 스타일 설정
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
          mentionCount?: number;
        };
        const shortName = dataContext?.shortName;
        const mentionCount = dataContext?.mentionCount;
        const sentiment = sentimentData?.[shortName ?? ''];

        const isValidMention = mentionCount && mentionCount > 0;
        const isValidSentiment =
          sentiment &&
          (sentiment.positive > 0 ||
            sentiment.neutral > 0 ||
            sentiment.negative > 0);

        const hasValidData = isValidMention || isValidSentiment;

        if (shortName && hasValidData) {
          if (shortName === 'RU') {
            chart.zoomToGeoPoint({ latitude: 60, longitude: 90 }, 3, true);
          } else {
            polygonSeries.zoomToDataItem(
              target.dataItem as am5.DataItem<am5map.IMapPolygonSeriesDataItem>
            );
          }
          setTimeout(() => {
            navigate(
              `/worldDetail/${shortName}/${category}/${period}/${keyword}/${keyword_mind}`
            );
            window.scrollTo(0, 0);
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

    // 범례 생성
    const legend = chart.children.push(
      am5.Legend.new(root, {
        nameField: 'name',
        fillField: 'color',
        layout: root.horizontalLayout,
      })
    );

    legend.setAll({
      x: am5.percent(50), // 수평 중앙 위치
      centerX: am5.percent(50), // 기준점도 중앙
      y: am5.percent(100), // 아래쪽 끝
      centerY: am5.percent(100), // 기준점도 아래쪽
    });

    const background = am5.RoundedRectangle.new(root, {
      fill: am5.color('#011728'),
      fillOpacity: 0.5,
    });

    legend.set('background', background);

    legend.itemContainers.template.setAll({
      layout: root.verticalLayout,
    });

    legend.markers.template.setAll({
      width: 20,
      height: 20,
      centerX: am5.percent(50),
      centerY: am5.percent(50),
      marginBottom: 2,
    });

    legend.labels.template.setAll({
      text: '{name}',
      fontSize: 13,
      fill: am5.color('#FFFFFF'),
      textAlign: 'center',
    });

    //===========================================================================
    // 언급량, 긍부정에 따른 설정
    //===========================================================================

    // 언급량에 따른 색상 설정 함수
    const mentionValues = mentionData ? Object.values(mentionData) : [];
    const minCount = Math.min(...mentionValues);
    const maxCount = Math.max(...mentionValues);
    const range = maxCount - minCount;

    const getColorByMention = (count: number): string => {
      if (range === 0) return '#FFF9EB';
      if (count <= minCount + range * 0.2) return '#FFF9EB';
      if (count <= minCount + range * 0.4) return '#FFEEC6';
      if (count <= minCount + range * 0.6) return '#FFC34A';
      if (count <= minCount + range * 0.8) return '#FFAA20';
      return '#F98607';
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
            mentionCount >= 0
              ? `${fullName}\n(언급량: {mentionCount})`
              : `${fullName}`
          );

          const countRange = [
            minCount + range * 0.2,
            minCount + range * 0.4,
            minCount + range * 0.6,
            minCount + range * 0.8,
            maxCount,
          ];

          let mentionLegend;
          if (range === 0) {
            // 언급량 0
            mentionLegend = [
              {
                name: `데이터 없음\n(0 ~ 0)`,
                color: am5.color('#FFF9EB'),
              },
            ];
          } else {
            mentionLegend = [
              {
                name: `매우 낮음\n(${minCount} ~ ${Math.floor(countRange[0])})`,
                color: am5.color('#FFF9EB'),
              },
              {
                name: `낮음\n(${Math.floor(countRange[0] + 1)} ~ ${Math.floor(countRange[1])})`,
                color: am5.color('#FFEEC6'),
              },
              {
                name: `보통\n(${Math.floor(countRange[1] + 1)} ~ ${Math.floor(countRange[2])})`,
                color: am5.color('#FFC34A'),
              },
              {
                name: `높음\n(${Math.floor(countRange[2] + 1)} ~ ${Math.floor(countRange[3])})`,
                color: am5.color('#FFAA20'),
              },
              {
                name: `매우 높음\n(${Math.floor(countRange[3] + 1)} ~ ${countRange[4]})`,
                color: am5.color('#F98607'),
              },
            ];
          }

          legend.data.setAll(mentionLegend);
        } else {
          polygon.set('fill', am5.color(getColorBySentiment(primarySentiment)));
          polygon.set(
            'tooltipText',
            sentiment
              ? `${fullName}\n긍정: ${Math.round(positive * 100)}%\n중립: ${Math.round(neutral * 100)}%\n부정: ${Math.round(negative * 100)}%`
              : `${fullName}`
          );
          legend.data.setAll([
            {
              name: '긍정',
              color: am5.color('#5279BD'),
            },
            {
              name: '중립',
              color: am5.color('#BBFF00'),
            },
            {
              name: '부정',
              color: am5.color('#E2695C'),
            },
          ]);
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

  return <div ref={chartContainerRef} className="w-[930px] h-full mx-auto" />;
};

export default Map;

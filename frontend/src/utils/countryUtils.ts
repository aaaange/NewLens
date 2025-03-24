export const g20Countries = [
    { code: 'KR', name: '대한민국' },
    { code: 'US', name: '미국' },
    { code: 'CN', name: '중국' },
    { code: 'JP', name: '일본' },
    { code: 'DE', name: '독일' },
    { code: 'FR', name: '프랑스' },
    { code: 'GB', name: '영국' },
    { code: 'IN', name: '인도' },
    { code: 'IT', name: '이탈리아' },
    { code: 'BR', name: '브라질' },
    { code: 'RU', name: '러시아' },
    { code: 'AU', name: '호주' },
    { code: 'CA', name: '캐나다' },
    { code: 'MX', name: '멕시코' },
    { code: 'ZA', name: '남아프리카공화국' },
    { code: 'SA', name: '사우디아라비아' },
    { code: 'TR', name: '터키' },
    { code: 'ID', name: '인도네시아' },
    { code: 'AR', name: '아르헨티나' },
  ];
  
  export const getCountryName = (code: string): string => {
    const country = g20Countries.find(
      (c) => c.code.toLowerCase() === code.toLowerCase()
    );
    return country ? country.name : code; // 못 찾으면 코드 그대로 반환
  };
  
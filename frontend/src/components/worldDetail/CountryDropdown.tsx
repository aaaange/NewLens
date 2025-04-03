import { useState, useEffect } from 'react';
import {
  MenuItem,
  Select,
  ListItemIcon,
  ListItemText,
  SelectProps,
} from '@mui/material';
import { ExpandMore } from '@mui/icons-material'; // 아이콘 변경용
import Flag from 'react-world-flags';

const g20Countries = [
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
  { code: 'AR', name: '아르헨티나' },
];

// 가로(width)와 세로(height)를 프롭스로 받아서 조절 가능하게 변경
interface CountryDropdownProps {
  width?: string;
  height?: string;
  value?: string; // 선택된 국가 코드
  onChange?: (code: string, name: string) => void; // 선택 변경시 호출되는 콜백
  placeholder?: string;
}

const CountryDropdown = ({
  width,
  height,
  value,
  onChange,
  placeholder,
}: CountryDropdownProps) => {
  const [selectedCountry, setSelectedCountry] = useState('');

  // 외부에서 전달된 value와 동기화
  useEffect(() => {
    if (value !== undefined && value !== selectedCountry) {
      setSelectedCountry(value);
    }
  }, [value]);

  const handleChange = (e: any) => {
    const newCode = e.target.value;
    const selectedCountry = g20Countries.find((c) => c.code === newCode);
    if (onChange && selectedCountry) {
      onChange(newCode, selectedCountry.name);
    }
  };

  const FixedFlag = Flag as any;

  return (
    <Select
      value={selectedCountry}
      // onChange={(e) => setSelectedCountry(e.target.value)}
      onChange={handleChange}
      displayEmpty
      className="bg-transparent text-white border-0 border-b border-white !rounded-none"
      sx={{
        width,
        height,
        borderRadius: '0px !important', // 라운딩 강제 제거
        '& .MuiSelect-icon': {
          color: 'white', // 드롭다운 아이콘 흰색으로 변경
        },
      }}
      IconComponent={(props) => (
        <ExpandMore {...props} className="text-white" />
      )} // 흰색 아이콘 적용
      renderValue={(selected) => {
        if (!selected) {
          return (
            <span className="text-white text-xl opacity-50">
              {placeholder || '비교할 나라를 선택하세요'}
            </span>
          );
        }

        const country = g20Countries.find((c) => c.code === selected);

        if (!country) {
          return null; // 혹은 에러 메시지를 출력하거나 fallback 처리
        }

        return (
          <div className="flex items-center gap-2">
            <FixedFlag
              code={country.code}
              fallback={<span>🏳️</span>}
              className="h-6 w-6"
            />
            <span className="text-white text-2xl">{country.name}</span>
          </div>
        );
      }}
    >
      <MenuItem disabled value="">
        {placeholder || '국가를 선택하세요'}
      </MenuItem>
      {g20Countries.map((country) => (
        <MenuItem
          key={country.code}
          value={country.code}
          className="!bg-transparent"
        >
          <ListItemIcon className="mr-2 w-9">
            <FixedFlag
              code={country.code}
              fallback={<span>🏳️</span>}
              className="h-6 w-6"
            />
          </ListItemIcon>
          <ListItemText primary={country.name} />
        </MenuItem>
      ))}
    </Select>
  );
};

export default CountryDropdown;

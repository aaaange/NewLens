import { DropdownTile } from './DropdownTile';

export const DropdownMenu = ({
  list,
  name,
}: {
  list: { text: string; onClick: () => void }[];
  name: string;
}) => {
  return (
    <div className="right-[80px] w-[180px] rounded-[10px] p-1 bg-gray-0 border border-gray-200 shadow-xsmall cursor-pointer z-10">
      <div className="text-gray-800 p-2 justify-center cursor-default border-b border-b-gray-300 flex">
        <span className="font-bold">{name}</span>
        <span>님 안녕하세요!</span>
      </div>
      {list.map((item, index) => {
        return (
          <div>
            <DropdownTile key={index} text={item.text} onClick={item.onClick} />
          </div>
        );
      })}
    </div>
  );
};

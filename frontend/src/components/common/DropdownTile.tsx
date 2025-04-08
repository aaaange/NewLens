export const DropdownTile = ({
  text,
  onClick,
}: {
  text: string;
  onClick: () => void;
}) => {
  return (
    <div
      className="block px-[8px] py-[8px] text-gray-800 hover:bg-gray-100"
      onClick={onClick}
    >
      {text}
    </div>
  );
};

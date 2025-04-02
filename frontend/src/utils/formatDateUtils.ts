export const formatDate = (rawDate: string) => {
  const date = new Date(rawDate);
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0'); // 0-based
  const day = `${date.getDate()}`.padStart(2, '0');
  return `${year}.${month}.${day}`;
};

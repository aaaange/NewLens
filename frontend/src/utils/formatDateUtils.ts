export const formatDate = (rawDate: string, type: string) => {
  const date = new Date(rawDate);
  const year = date.getFullYear();
  const month = `${date.getMonth() + 1}`.padStart(2, '0'); // 0-based
  const day = `${date.getDate()}`.padStart(2, '0');
  const hours = `${date.getHours()}`.padStart(2, '0');
  const minutes = `${date.getMinutes()}`.padStart(2, '0');
  if (type === 'full') {
    return `${year}.${month}.${day} ${hours}:${minutes}`;
  }
  return `${year}.${month}.${day}`;
};

import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api';

export const getOptimizedRoute = async () => {
  const response = await axios.get(`${BASE_URL}/route/optimize`);
  return response.data;
};
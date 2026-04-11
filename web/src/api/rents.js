import { request } from './auth';

export const getMyDues = async () => {
  return request('/rents/my-dues');
};

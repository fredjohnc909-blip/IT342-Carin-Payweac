import { request } from '../auth/auth';

export const getMyDues = async () => {
  return request('/rents/my-dues');
};

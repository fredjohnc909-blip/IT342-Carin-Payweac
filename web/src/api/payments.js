import { request } from './auth';

export const submitPayment = async (paymentData) => {
  const formData = new FormData();
  formData.append('rentId', paymentData.rentId);
  formData.append('amount', paymentData.amount);
  formData.append('paymentMethod', paymentData.paymentMethod);
  formData.append('referenceNumber', paymentData.referenceNumber);
  if (paymentData.receipt) {
    formData.append('receipt', paymentData.receipt);
  }

  // Use fetch directly for multipart as the request helper currently handles JSON
  const url = `/api/v1/payments/submit`;
  const stored = localStorage.getItem('payweac_auth');
  let authHeader = {};
  if (stored) {
    const { accessToken } = JSON.parse(stored);
    authHeader = { Authorization: `Bearer ${accessToken}` };
  }

  const res = await fetch(url, {
    method: 'POST',
    headers: {
      ...authHeader
    },
    body: formData
  });

  const data = await res.json();
  if (!res.ok) throw new Error(data?.message || 'Upload failed');
  return data;
};

export const getMyPaymentHistory = async () => {
  return request('/payments/my-history');
};

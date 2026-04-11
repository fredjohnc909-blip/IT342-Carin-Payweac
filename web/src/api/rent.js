import { apiRequest } from './client'

export async function fetchMyRentPayments(getAuthHeader) {
  return apiRequest('/rent/payments', { method: 'GET' }, getAuthHeader())
}

export async function recordRentPayment(payload, getAuthHeader) {
  return apiRequest(
    '/rent/payments',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    getAuthHeader()
  )
}

import { request } from '../auth/auth'

export const getTenants = async () => {
  const response = await request('/admin/users')
  return response.data
}

export const getTenantRents = async (userId) => {
  const response = await request(`/admin/users/${userId}/rents`)
  return response.data
}

export const updateRent = async (rentId, data) => {
  const response = await request(`/admin/rents/${rentId}`, {
    method: 'PUT',
    body: JSON.stringify(data)
  })
  return response.data
}

export const createRent = async (tenantId, data) => {
  const response = await request(`/admin/tenants/${tenantId}/rents`, {
    method: 'POST',
    body: JSON.stringify(data)
  })
  return response.data
}

export const deleteRent = async (rentId) => {
  const response = await request(`/admin/rents/${rentId}`, {
    method: 'DELETE'
  })
  return response.data
}

export const getAllPayments = async () => {
  const response = await request('/admin/payments')
  return response.data
}

export const getAllRents = async () => {
  const response = await request('/admin/rents')
  return response.data
}

export const approvePayment = async (paymentId) => {
  const response = await request(`/admin/payments/${paymentId}/approve`, {
    method: 'PUT'
  })
  return response.data
}

export const deletePayment = async (paymentId) => {
  const res = await request(`/admin/payments/${paymentId}`, {
    method: 'DELETE'
  })
  return res.data
}

export const rejectPayment = async (paymentId) => {
  const response = await request(`/admin/payments/${paymentId}/reject`, {
    method: 'PUT'
  })
  return response.data
}

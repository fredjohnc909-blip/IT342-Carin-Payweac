import { apiRequest } from './client'

async function request(endpoint, options = {}) {
  return apiRequest(endpoint, options, {})
}

export async function register(payload) {
  return request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export async function login(email, password) {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

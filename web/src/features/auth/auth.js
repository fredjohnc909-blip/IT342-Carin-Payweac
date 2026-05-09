const API_BASE = '/api/v1'
const STORAGE_KEY = 'payweac_auth'

export async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`
  
  // Get token from localStorage if it exists
  const stored = localStorage.getItem(STORAGE_KEY)
  let authHeader = {}
  if (stored) {
    try {
      const { accessToken } = JSON.parse(stored)
      if (accessToken) {
        authHeader = { Authorization: `Bearer ${accessToken}` }
      }
    } catch (e) {}
  }

  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...authHeader,
      ...options.headers,
    },
    ...options,
  })

  // Handle No Content responses
  if (res.status === 244 || res.status === 204) return { success: true }
  
  const data = await res.json()
  if (!res.ok) {
    let msg = data?.error?.message || 'Request failed'
    const details = data?.error?.details
    if (details) {
      msg = typeof details === 'string' ? details : Object.values(details).join('. ')
    }
    throw new Error(msg)
  }
  return data
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

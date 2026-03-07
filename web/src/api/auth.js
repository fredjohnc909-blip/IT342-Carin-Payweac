const API_BASE = '/api/v1'

async function request(endpoint, options = {}) {
  const url = `${API_BASE}${endpoint}`
  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  })
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

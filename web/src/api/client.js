const API_BASE = '/api/v1'

export async function apiRequest(endpoint, options = {}, extraHeaders = {}) {
  const url = `${API_BASE}${endpoint}`
  const res = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...extraHeaders,
      ...options.headers,
    },
    ...options,
  })

  const contentType = res.headers.get('content-type') || ''
  const rawText = await res.text()
  const data =
    rawText && contentType.includes('application/json') ? JSON.parse(rawText) : rawText || null

  if (!res.ok) {
    if (typeof data === 'object' && data) {
      let msg = data?.error?.message || 'Request failed'
      const details = data?.error?.details
      if (details) {
        msg = typeof details === 'string' ? details : Object.values(details).join('. ')
      }
      throw new Error(msg)
    }
    throw new Error(typeof data === 'string' && data ? data : `Request failed (${res.status})`)
  }
  return data
}

import { createContext, useContext, useState, useEffect } from 'react'

const AuthContext = createContext(null)

const STORAGE_KEY = 'payweac_auth'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [accessToken, setAccessToken] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      try {
        const { user: u, accessToken: t } = JSON.parse(stored)
        setUser(u)
        setAccessToken(t)
      } catch {
        localStorage.removeItem(STORAGE_KEY)
      }
    }
    setLoading(false)
  }, [])

  const login = (userData, token) => {
    setUser(userData)
    setAccessToken(token)
    localStorage.setItem(STORAGE_KEY, JSON.stringify({ user: userData, accessToken: token }))
  }

  const logout = () => {
    setUser(null)
    setAccessToken(null)
    localStorage.removeItem(STORAGE_KEY)
  }

  const getAuthHeader = () => (accessToken ? { Authorization: `Bearer ${accessToken}` } : {})

  return (
    <AuthContext.Provider value={{ user, accessToken, login, logout, getAuthHeader, loading }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

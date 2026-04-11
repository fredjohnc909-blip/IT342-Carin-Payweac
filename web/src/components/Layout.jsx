import { AuthProvider } from '../context/AuthContext'

export default function Layout({ children }) {
  return (
    <AuthProvider>
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        {children}
      </div>
    </AuthProvider>
  )
}

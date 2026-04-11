import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import Register from './pages/Register'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import PaymentPage from './pages/PaymentPage'
import PaymentHistory from './pages/PaymentHistory'

function ProtectedRoute({ children }) {
  const { user } = useAuth()
  if (!user) {
    return <Navigate to="/login" replace />
  }
  return children
}

function PublicRoute({ children }) {
  const { user } = useAuth()
  if (user) {
    return <Navigate to="/dashboard" replace />
  }
  return children
}

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route
          path="/register"
          element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          }
        />
        <Route
          path="/login"
          element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          }
        />
        <Route
          path="/pay/:rentId"
          element={
            <ProtectedRoute>
              <PaymentPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/history"
          element={
            <ProtectedRoute>
              <PaymentHistory />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Layout>
  )
}

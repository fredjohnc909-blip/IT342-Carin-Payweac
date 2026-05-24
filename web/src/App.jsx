import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Layout from './components/Layout'
import Register from './features/auth/Register'
import Login from './features/auth/Login'
import Dashboard from './features/dashboard/Dashboard'
import PaymentPage from './features/payment/PaymentPage'
import PaymentHistory from './features/payment/PaymentHistory'
import AdminDashboard from './features/admin/AdminDashboard'

function ProtectedRoute({ children }) {
  const { user } = useAuth()
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (user.role === 'ADMIN') {
    return <Navigate to="/admin" replace />
  }
  return children
}

function PublicRoute({ children }) {
  const { user } = useAuth()
  if (user) {
    return user.role === 'ADMIN' ? <Navigate to="/admin" replace /> : <Navigate to="/dashboard" replace />
  }
  return children
}

function AdminRoute({ children }) {
  const { user } = useAuth()
  if (!user) {
    return <Navigate to="/login" replace />
  }
  if (user.role !== 'ADMIN') {
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
        <Route
          path="/admin"
          element={
            <AdminRoute>
              <AdminDashboard />
            </AdminRoute>
          }
        />
      </Routes>
    </Layout>
  )
}

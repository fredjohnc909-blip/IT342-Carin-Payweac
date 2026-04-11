import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getMyDues } from '../api/rents'
import styles from './Dashboard.module.css'

export default function Dashboard() {
  const { user, logout } = useAuth()
  const [dues, setDues] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchDues = async () => {
      try {
        const res = await getMyDues()
        if (res.success) {
          setDues(res.data)
        }
      } catch (err) {
        console.error('Failed to fetch dues', err)
      } finally {
        setLoading(false)
      }
    }
    fetchDues()
  }, [])

  const pendingDues = dues.filter(d => d.status === 'PENDING')
  const totalDue = pendingDues.reduce((sum, d) => sum + d.amount, 0)

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>PayWEAC Dashboard</h1>
        <div className={styles.userInfo}>
          <Link to="/history" className={styles.historyLink}>Payment History</Link>
          <span>{user?.firstName} {user?.lastName}</span>
          <button onClick={logout} className={styles.logout}>Logout</button>
        </div>
      </header>

      <main className={styles.main}>
        <div className={styles.welcome}>
          <h2>Welcome, {user?.firstName}!</h2>
          <p>Track your rent and utilities here. Room: {user?.roomNumber || 'N/A'}</p>
        </div>

        <div className={styles.cards}>
          <div className={styles.card}>
            <h3>Total Balance Due</h3>
            <p className={styles.amount}>₱{totalDue.toLocaleString()}</p>
          </div>
          <div className={styles.card}>
            <h3>Status</h3>
            <p className={pendingDues.length > 0 ? styles.unpaid : styles.paid}>
              {pendingDues.length > 0 ? 'Action Required' : 'Up to date'}
            </p>
          </div>
        </div>

        <section className={styles.duesSection}>
          <div className={styles.sectionHeader}>
            <h3>Your Rent Dues</h3>
          </div>
          {loading ? (
            <p>Loading dues...</p>
          ) : dues.length === 0 ? (
            <p className={styles.empty}>No rent records found.</p>
          ) : (
            <div className={styles.duesList}>
              {dues.map(due => (
                <div key={due.id} className={styles.dueItem}>
                  <div className={styles.dueInfo}>
                    <strong>{due.month} {due.year}</strong>
                    <span>Due: {new Date(due.dueDate).toLocaleDateString()}</span>
                  </div>
                  <div className={styles.dueAmount}>
                    ₱{due.amount.toLocaleString()}
                  </div>
                  <div className={styles.dueStatus}>
                    <span className={due.status === 'PAID' ? styles.statusPaid : styles.statusPending}>
                      {due.status}
                    </span>
                    {due.status === 'PENDING' && (
                      <Link to={`/pay/${due.id}`} className={styles.payBtn}>Pay Now</Link>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  )
}

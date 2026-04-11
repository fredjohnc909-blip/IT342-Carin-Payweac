import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getMyPaymentHistory } from '../api/payments'
import styles from './PaymentHistory.module.css'

export default function PaymentHistory() {
  const [history, setHistory] = useState([])
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const res = await getMyPaymentHistory()
        if (res.success) {
          setHistory(res.data)
        }
      } catch (err) {
        console.error('Failed to fetch history', err)
      } finally {
        setLoading(false)
      }
    }
    fetchHistory()
  }, [])

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <button onClick={() => navigate('/dashboard')} className={styles.backBtn}>
          ← Back to Dashboard
        </button>
        <h1>Payment History</h1>
      </header>

      <main className={styles.main}>
        {loading ? (
          <p>Loading payments...</p>
        ) : history.length === 0 ? (
          <div className={styles.empty}>
            <p>You haven't submitted any payments yet.</p>
          </div>
        ) : (
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Date</th>
                <th>Rent Period</th>
                <th>Method</th>
                <th>Reference #</th>
                <th>Amount</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {history.map(item => (
                <tr key={item.id}>
                  <td>{new Date(item.paymentDate).toLocaleDateString()}</td>
                  <td>{item.rentMonthYear}</td>
                  <td>{item.paymentMethod}</td>
                  <td className={styles.ref}>{item.referenceNumber}</td>
                  <td className={styles.amount}>₱{item.amount.toLocaleString()}</td>
                  <td>
                    <span className={`${styles.status} ${styles[item.status.toLowerCase()]}`}>
                      {item.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </main>
    </div>
  )
}

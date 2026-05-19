import { useState } from 'react'
import styles from './AdminDashboard.module.css'

export default function AddRentModal({ onClose, onSave }) {
  const [month, setMonth] = useState('January')
  const [year, setYear] = useState(new Date().getFullYear())
  const [amount, setAmount] = useState('5000')
  const [dueDate, setDueDate] = useState('')
  const [loading, setLoading] = useState(false)

  const months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ]

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await onSave({
        month,
        year: parseInt(year),
        amount,
        dueDate
      })
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <h2>Add Rent Due</h2>
        <form onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label>Month</label>
            <select value={month} onChange={(e) => setMonth(e.target.value)} required>
              {months.map(m => (
                <option key={m} value={m}>{m}</option>
              ))}
            </select>
          </div>
          
          <div className={styles.formGroup}>
            <label>Year</label>
            <input 
              type="number" 
              value={year} 
              onChange={(e) => setYear(e.target.value)} 
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label>Amount (₱)</label>
            <input 
              type="number" 
              value={amount} 
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label>Due Date</label>
            <input 
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              required
            />
          </div>

          <div className={styles.modalActions}>
            <button type="button" onClick={onClose} className={styles.cancelBtn}>
              Cancel
            </button>
            <button type="submit" className={styles.saveBtn} disabled={loading}>
              {loading ? 'Adding...' : 'Add Rent'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

import { useState } from 'react'
import styles from './AdminDashboard.module.css'

export default function EditRentModal({ rent, onClose, onSave }) {
  const getInitialDate = (date) => {
    if (!date) return '';
    if (Array.isArray(date)) {
      const [y, m, d] = date;
      return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
    }
    return date.split('T')[0];
  }

  const [amount, setAmount] = useState(rent.amount)
  const [status, setStatus] = useState(rent.status)
  const [startDate, setStartDate] = useState(getInitialDate(rent.startDate))
  const [dueDate, setDueDate] = useState(getInitialDate(rent.dueDate))
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await onSave(rent.id, { amount, status, startDate, dueDate })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.modalOverlay}>
      <div className={styles.modalContent}>
        <h2>Edit Rent</h2>
        <p>{rent.month} {rent.year}</p>

        <form onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label>Amount (₱)</label>
            <input
              type="number"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </div>

          <div className={styles.formGroup}>
            <label>Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
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

          <div className={styles.formGroup}>
            <label>Status</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option value="PENDING">PENDING</option>
              <option value="PARTIALLY_PAID">PARTIALLY_PAID</option>
              <option value="PAID">PAID</option>
              <option value="MISSED">MISSED</option>
            </select>
          </div>

          <div className={styles.modalActions}>
            <button
              type="button"
              onClick={onClose}
              className={styles.cancelBtn}
              disabled={loading}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className={styles.saveBtn}
            >
              {loading ? 'Saving...' : 'Save Changes'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

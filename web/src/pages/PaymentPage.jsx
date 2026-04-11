import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getMyDues } from '../api/rents'
import { submitPayment } from '../api/payments'
import styles from './PaymentPage.module.css'

export default function PaymentPage() {
  const { rentId } = useParams()
  const navigate = useNavigate()
  const [rent, setRent] = useState(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  const [formData, setFormData] = useState({
    paymentMethod: 'GCASH',
    amount: '',
    referenceNumber: '',
    receipt: null
  })

  useEffect(() => {
    const fetchRent = async () => {
      try {
        const res = await getMyDues()
        const selected = res.data.find(d => d.id === parseInt(rentId))
        if (selected) {
          setRent(selected)
          setFormData(prev => ({ ...prev, amount: selected.amount }))
        }
      } catch (err) {
        setError('Failed to load rent details.')
      } finally {
        setLoading(false)
      }
    }
    fetchRent()
  }, [rentId])

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleFileChange = (e) => {
    setFormData(prev => ({ ...prev, receipt: e.target.files[0] }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)

    try {
      const res = await submitPayment({
        rentId,
        ...formData
      })
      if (res.success) {
        setSuccess(true)
        setTimeout(() => navigate('/dashboard'), 2000)
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to submit payment.')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <div className={styles.loading}>Loading...</div>

  return (
    <div className={styles.container}>
      <div className={styles.formCard}>
        <h2>Submit Payment</h2>
        <p className={styles.subtitle}>Paying for {rent?.month} {rent?.year}</p>

        {error && <div className={styles.error}>{error}</div>}
        {success && <div className={styles.success}>Payment submitted successfully! Redirecting...</div>}

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Payment Method</label>
            <select name="paymentMethod" value={formData.paymentMethod} onChange={handleInputChange}>
              <option value="GCASH">GCash</option>
              <option value="CASH">Cash</option>
            </select>
          </div>

          <div className={styles.field}>
            <label>Amount (₱)</label>
            <input 
              type="number" 
              name="amount" 
              value={formData.amount} 
              onChange={handleInputChange} 
              required 
            />
          </div>

          <div className={styles.field}>
            <label>Reference Number</label>
            <input 
              type="text" 
              name="referenceNumber" 
              placeholder="Enter reference or receipt #"
              value={formData.referenceNumber} 
              onChange={handleInputChange} 
              required 
            />
          </div>

          <div className={styles.field}>
            <label>Upload Receipt (Image)</label>
            <input 
              type="file" 
              accept="image/*" 
              onChange={handleFileChange} 
              required 
            />
          </div>

          <div className={styles.actions}>
            <button type="button" onClick={() => navigate('/dashboard')} className={styles.cancelBtn}>
              Cancel
            </button>
            <button type="submit" disabled={submitting} className={styles.submitBtn}>
              {submitting ? 'Submitting...' : 'Confirm Payment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

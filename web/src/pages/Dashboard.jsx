import { useCallback, useEffect, useMemo, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { fetchMyRentPayments, recordRentPayment } from '../api/rent'
import styles from './Dashboard.module.css'

const PERIOD_RE = /^\d{4}-(0[1-9]|1[0-2])$/

function formatPhp(amount) {
  const n = Number(amount)
  if (Number.isNaN(n)) return '—'
  return new Intl.NumberFormat('en-PH', { style: 'currency', currency: 'PHP' }).format(n)
}

function defaultBillingPeriod() {
  const d = new Date()
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
}

export default function Dashboard() {
  const { user, logout, getAuthHeader } = useAuth()
  const [payments, setPayments] = useState([])
  const [loadError, setLoadError] = useState('')
  const [loadingList, setLoadingList] = useState(true)
  const [form, setForm] = useState({
    amount: '',
    billingPeriod: defaultBillingPeriod(),
  })
  const [formError, setFormError] = useState('')
  const [successMsg, setSuccessMsg] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const loadPayments = useCallback(async () => {
    setLoadError('')
    setLoadingList(true)
    try {
      const res = await fetchMyRentPayments(getAuthHeader)
      if (res.success && Array.isArray(res.data)) {
        setPayments(res.data)
      } else {
        setLoadError('Could not load rent payments.')
      }
    } catch (e) {
      setLoadError(e.message || 'Could not load rent payments.')
    } finally {
      setLoadingList(false)
    }
  }, [getAuthHeader])

  useEffect(() => {
    loadPayments()
  }, [loadPayments])

  const summary = useMemo(() => {
    const now = new Date()
    const ym = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    const paidThisMonth = payments.some((p) => p.billingPeriod === ym)
    const totalPaid = payments.reduce((sum, p) => sum + Number(p.amount || 0), 0)
    const unpaidPrior = payments.length === 0 ? 'No payments on file yet' : 'See payment history below'
    return { paidThisMonth, totalPaid, ym, unpaidPrior }
  }, [payments])

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setFormError('')
    setSuccessMsg('')
  }

  const validateForm = () => {
    const amountStr = form.amount.trim()
    if (!amountStr) {
      setFormError('Amount is required.')
      return null
    }
    const amount = Number(amountStr)
    if (Number.isNaN(amount) || amount < 0.01) {
      setFormError('Enter a valid amount of at least 0.01.')
      return null
    }
    const period = form.billingPeriod.trim()
    if (!PERIOD_RE.test(period)) {
      setFormError('Billing period must be YYYY-MM (e.g. 2026-04).')
      return null
    }
    return { amount, billingPeriod: period }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSuccessMsg('')
    const payload = validateForm()
    if (!payload) return

    setSubmitting(true)
    try {
      const res = await recordRentPayment(payload, getAuthHeader)
      if (res.success && res.data) {
        setSuccessMsg(
          `Payment recorded for ${res.data.billingPeriod}. Confirmation: ${res.data.confirmationCode}`
        )
        setForm((prev) => ({ ...prev, amount: '' }))
        await loadPayments()
      } else {
        setFormError(res.error?.message || 'Payment could not be recorded.')
      }
    } catch (err) {
      setFormError(err.message || 'Payment could not be recorded.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>PayWEAC</h1>
        <div className={styles.userInfo}>
          <span>
            {user?.firstName} {user?.lastName}
            {user?.roomNumber ? ` · Room ${user.roomNumber}` : ''}
          </span>
          <span className={styles.email}>{user?.email}</span>
          <button type="button" onClick={logout} className={styles.logout}>
            Logout
          </button>
        </div>
      </header>

      <main className={styles.main}>
        <section className={styles.welcome}>
          <h2>Rent payment &amp; tracking</h2>
          <p>
            Record rent payments for a billing month (YYYY-MM). Each month can only be marked paid once per
            account. Data is stored in the PayWEAC database.
          </p>
        </section>

        <div className={styles.cards}>
          <div className={styles.card}>
            <h3>Current month ({summary.ym})</h3>
            <p className={styles.cardStat}>
              {summary.paidThisMonth ? (
                <span className={styles.ok}>Recorded as paid</span>
              ) : (
                <span className={styles.warn}>Not yet recorded</span>
              )}
            </p>
          </div>
          <div className={styles.card}>
            <h3>Total recorded</h3>
            <p className={styles.cardStat}>{formatPhp(summary.totalPaid)}</p>
            <p className={styles.cardHint}>{payments.length} payment(s) on file</p>
          </div>
          <div className={styles.card}>
            <h3>History</h3>
            <p className={styles.cardHint}>{summary.unpaidPrior}</p>
          </div>
        </div>

        <section className={styles.panel}>
          <h3>Record a rent payment</h3>
          <form className={styles.payForm} onSubmit={handleSubmit}>
            {formError && <div className={styles.error}>{formError}</div>}
            {successMsg && <div className={styles.success}>{successMsg}</div>}

            <div className={styles.row}>
              <div className={styles.field}>
                <label htmlFor="amount">Amount (PHP)</label>
                <input
                  id="amount"
                  name="amount"
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={form.amount}
                  onChange={handleChange}
                  placeholder="e.g. 8500"
                  disabled={submitting}
                />
              </div>
              <div className={styles.field}>
                <label htmlFor="billingPeriod">Billing month</label>
                <input
                  id="billingPeriod"
                  name="billingPeriod"
                  type="text"
                  value={form.billingPeriod}
                  onChange={handleChange}
                  placeholder="YYYY-MM"
                  disabled={submitting}
                />
                <span className={styles.hint}>Format: YYYY-MM (e.g. 2026-04)</span>
              </div>
            </div>

            <button type="submit" className={styles.primaryBtn} disabled={submitting}>
              {submitting ? 'Saving…' : 'Record payment'}
            </button>
          </form>
        </section>

        <section className={styles.panel}>
          <h3>Your payment history</h3>
          {loadError && <div className={styles.error}>{loadError}</div>}
          {loadingList ? (
            <p className={styles.muted}>Loading…</p>
          ) : payments.length === 0 ? (
            <p className={styles.muted}>No payments yet. Use the form above to record your first payment.</p>
          ) : (
            <div className={styles.tableWrap}>
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th>Billing month</th>
                    <th>Amount</th>
                    <th>Paid at</th>
                    <th>Confirmation</th>
                  </tr>
                </thead>
                <tbody>
                  {payments.map((p) => (
                    <tr key={p.id}>
                      <td>{p.billingPeriod}</td>
                      <td>{formatPhp(p.amount)}</td>
                      <td>{p.paidAt ? new Date(p.paidAt).toLocaleString() : '—'}</td>
                      <td className={styles.mono}>{p.confirmationCode}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
    </div>
  )
}

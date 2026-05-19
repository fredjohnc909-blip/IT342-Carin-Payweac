import { useState, useEffect } from 'react'
import { getTenants, getAllPayments, getAllRents, updateRent, approvePayment, rejectPayment } from './admin'
import TenantDetails from './TenantDetails'
import EditRentModal from './EditRentModal'
import styles from './AdminDashboard.module.css'
import { useAuth } from '../../context/AuthContext'

export default function AdminDashboard() {
  const { logout } = useAuth()
  const [activeTab, setActiveTab] = useState('tenants')
  const [tenants, setTenants] = useState([])
  const [payments, setPayments] = useState([])
  const [allRents, setAllRents] = useState([])
  const [loading, setLoading] = useState(true)
  const [selectedTenant, setSelectedTenant] = useState(null)
  const [editingRent, setEditingRent] = useState(null)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      setLoading(true)
      const [tenantsData, paymentsData, rentsData] = await Promise.all([
        getTenants(),
        getAllPayments(),
        getAllRents()
      ])
      setTenants(tenantsData)
      setPayments(paymentsData)
      setAllRents(rentsData)
    } catch (err) {
      console.error('Failed to load admin data:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleSaveRent = async (rentId, data) => {
    try {
      await updateRent(rentId, data)
      setEditingRent(null)
      loadData()
    } catch (err) {
      alert('Failed to update rent')
    }
  }

  const handleApprovePayment = async (paymentId) => {
    try {
      await approvePayment(paymentId)
      loadData()
    } catch (err) {
      alert('Failed to approve payment')
    }
  }

  const handleRejectPayment = async (paymentId) => {
    try {
      await rejectPayment(paymentId)
      loadData()
    } catch (err) {
      alert('Failed to reject payment')
    }
  }

  if (selectedTenant) {
    return (
      <div className={styles.container}>
        <header className={styles.header}>
          <h1>System Admin Dashboard</h1>
          <div className={styles.userInfo}>
            <span>System Admin</span>
            <button onClick={logout} className={styles.logout}>Logout</button>
          </div>
        </header>
        <main className={styles.main}>
          <TenantDetails 
            tenant={selectedTenant} 
            onBack={() => {
              setSelectedTenant(null)
              loadData()
            }} 
          />
        </main>
      </div>
    )
  }

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>System Admin Dashboard</h1>
        <div className={styles.userInfo}>
          <span>System Admin</span>
          <button onClick={logout} className={styles.logout}>Logout</button>
        </div>
      </header>

      <main className={styles.main}>
        <div className={styles.welcome}>
          <h2>Overview</h2>
          <p>Manage tenants, track rents, and monitor payments.</p>
        </div>

        <div className={styles.tabs}>
          <button
            onClick={() => setActiveTab('tenants')}
            className={`${styles.tab} ${activeTab === 'tenants' ? styles.activeTab : ''}`}
          >
            Tenants
          </button>
          <button
            onClick={() => setActiveTab('rents')}
            className={`${styles.tab} ${activeTab === 'rents' ? styles.activeTab : ''}`}
          >
            All Rents
          </button>
          <button
            onClick={() => setActiveTab('payments')}
            className={`${styles.tab} ${activeTab === 'payments' ? styles.activeTab : ''}`}
          >
            Payment History
          </button>
        </div>

        {loading ? (
          <div className={styles.loading}>Loading...</div>
        ) : activeTab === 'tenants' ? (
          <div className={styles.card}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Room</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {tenants.map(tenant => (
                  <tr key={tenant.id}>
                    <td>{tenant.firstName} {tenant.lastName}</td>
                    <td>{tenant.email}</td>
                    <td>{tenant.roomNumber || '-'}</td>
                    <td>
                      <button
                        onClick={() => setSelectedTenant(tenant)}
                        className={styles.actionBtn}
                      >
                        View Details
                      </button>
                    </td>
                  </tr>
                ))}
                {tenants.length === 0 && (
                  <tr>
                    <td colSpan="4" className={styles.loading}>
                      No tenants found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        ) : activeTab === 'payments' ? (
          <div className={styles.card}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Tenant</th>
                  <th>Room</th>
                  <th>Rent Period</th>
                  <th>Amount</th>
                  <th>Method / Ref</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {payments.map(payment => (
                  <tr key={payment.id}>
                    <td>{new Date(payment.paymentDate).toLocaleDateString()}</td>
                    <td>{payment.tenantName || 'Unknown'}</td>
                    <td>{payment.roomNumber || '-'}</td>
                    <td>{payment.rentMonthYear}</td>
                    <td>₱{payment.amount.toLocaleString()}</td>
                    <td>
                      <div>{payment.paymentMethod}</div>
                      <div style={{fontSize: '0.8rem', color: 'var(--text-muted)'}}>{payment.referenceNumber}</div>
                    </td>
                    <td>
                      <span className={
                        payment.status === 'APPROVED' ? styles.statusPaid :
                        payment.status === 'REJECTED' ? styles.statusMissed :
                        styles.statusPending
                      }>
                        {payment.status}
                      </span>
                    </td>
                    <td>
                      {payment.status === 'PENDING' ? (
                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button
                            onClick={() => handleApprovePayment(payment.id)}
                            className={styles.actionBtn}
                            style={{ background: '#dcfce7', color: '#166534' }}
                          >
                            Approve
                          </button>
                          <button
                            onClick={() => handleRejectPayment(payment.id)}
                            className={styles.actionBtn}
                            style={{ background: '#fee2e2', color: '#991b1b' }}
                          >
                            Reject
                          </button>
                        </div>
                      ) : '-'}
                    </td>
                  </tr>
                ))}
                {payments.length === 0 && (
                  <tr>
                    <td colSpan="8" className={styles.loading}>
                      No payments found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        ) : activeTab === 'rents' ? (
          <div className={styles.card}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Tenant</th>
                  <th>Room</th>
                  <th>Rent Period</th>
                  <th>Amount</th>
                  <th>Due Date</th>
                  <th>Method</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {allRents.map(rent => (
                  <tr key={rent.id}>
                    <td>
                      <div>{rent.tenantName}</div>
                      <div style={{fontSize: '0.8rem', color: 'var(--text-muted)'}}>{rent.tenantEmail}</div>
                    </td>
                    <td>{rent.roomNumber || '-'}</td>
                    <td>{rent.month} {rent.year}</td>
                    <td>₱{rent.amount.toLocaleString()}</td>
                    <td>{rent.dueDate ? new Date(rent.dueDate).toLocaleDateString() : '-'}</td>
                    <td>
                      {rent.paymentMethod ? (
                        <>
                          <div>{rent.paymentMethod}</div>
                          {rent.referenceNumber && <div style={{fontSize: '0.8rem', color: 'var(--text-muted)'}}>{rent.referenceNumber}</div>}
                        </>
                      ) : '-'}
                    </td>
                    <td>
                      <span className={
                        rent.status === 'PAID' ? styles.statusPaid :
                        rent.status === 'MISSED' ? styles.statusMissed :
                        styles.statusPending
                      }>
                        {rent.status}
                      </span>
                    </td>
                    <td>
                      <button
                        onClick={() => setEditingRent(rent)}
                        className={styles.editBtn}
                      >
                        Edit
                      </button>
                    </td>
                  </tr>
                ))}
                {allRents.length === 0 && (
                  <tr>
                    <td colSpan="8" className={styles.loading}>
                      No rents found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        ) : null}
      </main>

      {editingRent && (
        <EditRentModal
          rent={editingRent}
          onClose={() => setEditingRent(null)}
          onSave={handleSaveRent}
        />
      )}
    </div>
  )
}

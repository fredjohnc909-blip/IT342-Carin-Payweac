import { useState, useEffect } from 'react'
import { getTenantRents, updateRent, createRent, deleteRent } from './admin'
import EditRentModal from './EditRentModal'
import AddRentModal from './AddRentModal'
import styles from './AdminDashboard.module.css'

export default function TenantDetails({ tenant, onBack }) {
  const [rents, setRents] = useState([])
  const [loading, setLoading] = useState(true)
  const [editingRent, setEditingRent] = useState(null)
  const [isAddingRent, setIsAddingRent] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    loadRents()
  }, [tenant.id])

  const loadRents = async () => {
    try {
      setLoading(true)
      const data = await getTenantRents(tenant.id)
      setRents(data)
    } catch (err) {
      setError('Failed to load rents for this tenant.')
    } finally {
      setLoading(false)
    }
  }

  const handleSaveRent = async (rentId, data) => {
    try {
      const updated = await updateRent(rentId, data)
      setRents(rents.map(r => r.id === updated.id ? updated : r))
      setEditingRent(null)
    } catch (err) {
      alert('Failed to update rent')
    }
  }

  const handleCreateRent = async (data) => {
    try {
      const newRent = await createRent(tenant.id, data)
      setRents([...rents, newRent])
      setIsAddingRent(false)
    } catch (err) {
      alert('Failed to create rent')
    }
  }

  const handleDeleteRent = async (rentId) => {
    if (!window.confirm('Are you sure you want to delete this rent record? This will also delete any associated payment history.')) {
      return
    }
    try {
      await deleteRent(rentId)
      setRents(rents.filter(r => r.id !== rentId))
    } catch (err) {
      alert('Failed to delete rent')
    }
  }

  const getStatusClass = (status) => {
    switch (status) {
      case 'PAID': return styles.statusPaid
      case 'MISSED': return styles.statusMissed
      default: return styles.statusPending
    }
  }

  return (
    <div>
      <div className={styles.tenantHeader}>
        <button onClick={onBack} className={styles.backBtn}>
          ←
        </button>
        <div className={styles.tenantInfo}>
          <h2>{tenant.firstName} {tenant.lastName}</h2>
          <p>Room: {tenant.roomNumber || 'Unassigned'} | {tenant.email}</p>
        </div>
      </div>

      {error && <div className={styles.error}>{error}</div>}

      <div className={styles.card}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3 style={{marginTop: 0, marginBottom: 0}}>Rent Dues</h3>
          <button 
            onClick={() => setIsAddingRent(true)}
            className={styles.actionBtn}
            style={{ background: 'var(--primary-color)', color: 'white' }}
          >
            + Add Rent Due
          </button>
        </div>
        {loading ? (
          <div className={styles.loading}>Loading rents...</div>
        ) : rents.length === 0 ? (
          <p className={styles.loading}>No rent records found.</p>
        ) : (
          <div className={styles.rentList}>
            {rents.map(rent => (
              <div key={rent.id} className={styles.rentItem}>
                <div className={styles.rentInfo}>
                  <strong>{rent.month} {rent.year}</strong>
                  <span>Start: {rent.startDate ? new Date(rent.startDate).toLocaleDateString() : '-'}</span>
                  <span>Due: {rent.dueDate ? new Date(rent.dueDate).toLocaleDateString() : '-'}</span>
                  {rent.paymentMethod && (
                    <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      Paid via: {rent.paymentMethod} {rent.referenceNumber ? `(${rent.referenceNumber})` : ''}
                    </span>
                  )}
                </div>
                <div className={styles.rentActions}>
                  <span className={styles.rentAmount}>₱{rent.amount.toLocaleString()}</span>
                  <span className={getStatusClass(rent.status)}>
                    {rent.status}
                  </span>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button
                      onClick={() => setEditingRent(rent)}
                      className={styles.editBtn}
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDeleteRent(rent.id)}
                      className={styles.editBtn}
                      style={{ background: '#fee2e2', color: '#991b1b', border: '1px solid #fca5a5' }}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {editingRent && (
        <EditRentModal
          rent={editingRent}
          onClose={() => setEditingRent(null)}
          onSave={handleSaveRent}
        />
      )}

      {isAddingRent && (
        <AddRentModal
          onClose={() => setIsAddingRent(false)}
          onSave={handleCreateRent}
        />
      )}
    </div>
  )
}

import { useAuth } from '../context/AuthContext'
import styles from './Dashboard.module.css'

export default function Dashboard() {
  const { user, logout } = useAuth()

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>PayWEAC Dashboard</h1>
        <div className={styles.userInfo}>
          <span>{user?.firstName} {user?.lastName}</span>
          <span className={styles.email}>{user?.email}</span>
          <button onClick={logout} className={styles.logout}>Logout</button>
        </div>
      </header>

      <main className={styles.main}>
        <div className={styles.welcome}>
          <h2>Welcome, {user?.firstName}!</h2>
          <p>You have successfully logged in. Rent tracking and payment features will be available in the next phase.</p>
        </div>

        <div className={styles.cards}>
          <div className={styles.card}>
            <h3>Current Month Rent</h3>
            <p>Coming soon</p>
          </div>
          <div className={styles.card}>
            <h3>Past Unpaid</h3>
            <p>Coming soon</p>
          </div>
          <div className={styles.card}>
            <h3>Upcoming Rent</h3>
            <p>Coming soon</p>
          </div>
        </div>
      </main>
    </div>
  )
}

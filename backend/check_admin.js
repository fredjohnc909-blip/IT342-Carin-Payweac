const { Client } = require('pg');

const client = new Client({
  connectionString: 'postgres://neondb_owner:npg_aIOy7gqerhR9@ep-restless-smoke-an8zs3xi-pooler.c-6.us-east-1.aws.neon.tech/neondb?sslmode=require'
});

client.connect()
  .then(() => client.query("SELECT email, password_hash, role FROM users WHERE role = 'ADMIN'"))
  .then(res => {
    console.log(res.rows);
    client.end();
  })
  .catch(err => {
    console.error(err);
    client.end();
  });

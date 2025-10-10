import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

export default function Register() {
  const nav = useNavigate()
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [err, setErr] = useState('')
  const [ok, setOk] = useState('')

  const onChange = e => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setErr(''); setOk('')
    if (!form.username || !form.password) {
      setErr('Username and password are required'); return
    }
    try {
      await api.register(form.username, form.email, form.password)
      setOk('Account created. You can log in now.')
      // Optional: auto-login after register
      // await api.login(form.username, form.password)
      // nav('/')
      setTimeout(() => nav('/login'), 1200)
    } catch (e) {
      setErr(e.message)
    }
  }

  return (
    <form onSubmit={submit} style={{padding:16, maxWidth:420, display:'grid', gap:8}}>
      <h3>Create Account</h3>
      {err && <div style={{color:'red'}}>{err}</div>}
      {ok &&  <div style={{color:'green'}}>{ok}</div>}
      <label>Username</label>
      <input name="username" value={form.username} onChange={onChange} />
      <label>Email (optional)</label>
      <input name="email" type="email" value={form.email} onChange={onChange} />
      <label>Password</label>
      <input name="password" type="password" value={form.password} onChange={onChange} />
      <button type="submit">Create Account</button>
    </form>
  )
}
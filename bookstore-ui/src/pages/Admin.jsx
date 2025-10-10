import { useState } from 'react'
import { api } from '../api'

export default function Admin() {
  const [form, setForm] = useState({
    id: '', title: '', author: '', genre: '', price: '', stockQuantity: ''
  })
  const [msg, setMsg] = useState('')
  const onChange = e => setForm({ ...form, [e.target.name]: e.target.value })

  const submit = async (e) => {
    e.preventDefault()
    setMsg('')
    try {
      // POST /api/books (admin-only; credentials must be included inside api.js for auth endpoints)
      const res = await fetch('/api/books', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        credentials: 'include',
        body: JSON.stringify({
          id: form.id || undefined,
          title: form.title,
          author: form.author,
          genre: form.genre,
          price: Number(form.price),
          stockQuantity: Number(form.stockQuantity)
        })
      })
      if (!res.ok) throw new Error(await res.text())
      setMsg('Saved!')
    } catch (e) { setMsg(`Error: ${e.message}`) }
  }

  return (
    <form onSubmit={submit} style={{padding:16, display:'grid', gap:8, maxWidth:500}}>
      <h3>Admin: Add / Update Book</h3>
      {msg && <div>{msg}</div>}
      <label>ID (optional)</label>
      <input name="id" value={form.id} onChange={onChange} placeholder="b-..." />
      <label>Title *</label>
      <input name="title" value={form.title} onChange={onChange} required />
      <label>Author</label>
      <input name="author" value={form.author} onChange={onChange} />
      <label>Genre</label>
      <input name="genre" value={form.genre} onChange={onChange} />
      <label>Price</label>
      <input name="price" type="number" step="0.01" value={form.price} onChange={onChange} />
      <label>Stock</label>
      <input name="stockQuantity" type="number" min="0" value={form.stockQuantity} onChange={onChange} />
      <button type="submit">Save</button>
      <a href="/admin/seed">Go to Bulk Seed</a>
    </form>
  )
}

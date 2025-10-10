import { useState } from 'react'

export default function AdminSeed() {
  const [text, setText] = useState('[{"id":"b-1","title":"Demo","author":"A","genre":"G","price":9.99,"stockQuantity":5}]')
  const [msg, setMsg] = useState('')

  const submit = async () => {
    setMsg('')
    try {
      const payload = JSON.parse(text)
      const res = await fetch('/api/books/bulk', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        credentials: 'include',
        body: JSON.stringify(payload)
      })
      if (!res.ok) throw new Error(await res.text())
      const json = await res.json()
      setMsg(`Seeded/updated: ${json.count}`)
    } catch (e) { setMsg(`Error: ${e.message}`) }
  }

  return (
    <div style={{padding:16}}>
      <h3>Admin: Bulk Seed Books</h3>
      <textarea style={{width:'100%',height:200}} value={text} onChange={e=>setText(e.target.value)} />
      <div><button onClick={submit}>Upload</button></div>
      {msg && <div style={{marginTop:8}}>{msg}</div>}
    </div>
  )
}
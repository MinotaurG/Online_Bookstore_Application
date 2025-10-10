import { BrowserRouter, Routes, Route, Link, useNavigate } from 'react-router-dom'
import { useEffect, useMemo, useState } from 'react'
import { api } from './api'

function Nav({ user, onLogout }) {
  return (
    <nav style={{display:'flex', gap:12, padding:12, borderBottom:'1px solid #ddd'}}>
      <Link to="/">Catalog</Link>
      <Link to="/cart">Cart</Link>
      <Link to="/orders">My Orders</Link>
      <Link to="/history">History</Link>
      <Link to="/recs">Recommendations</Link>
      <div style={{marginLeft:'auto'}}>
        {user ? (
          <>
            <span style={{marginRight:8}}>Hi, {user.username}</span>
            <button onClick={onLogout}>Logout</button>
          </>
        ) : (
          <Link to="/login"><button>Login</button></Link>
        )}
      </div>
    </nav>
  )
}

function Login({ setUser }) {
  const nav = useNavigate()
  const [username,setUsername] = useState('')
  const [password,setPassword] = useState('')
  const [err,setErr] = useState('')
  const submit = async (e) => {
    e.preventDefault()
    setErr('')
    try {
      await api.login(username, password)
      const me = await api.me()
      setUser(me)
      nav('/')
    } catch (e) { setErr(e.message) }
  }
  return (
    <form onSubmit={submit} style={{padding:16, maxWidth:360}}>
      <h3>Login</h3>
      {err && <div style={{color:'red', marginBottom:8}}>{err}</div>}
      <input placeholder="username" value={username} onChange={e=>setUsername(e.target.value)} />
      <input placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)} />
      <button type="submit">Login</button>
    </form>
  )
}

function Catalog({ addToCart }) {
  const [q,setQ] = useState('')
  const [books,setBooks] = useState([])
  const [err,setErr] = useState('')
  const load = async () => {
    try {
      const data = q ? await api.searchBooks(q) : await api.listBooks()
      setBooks(data); setErr('')
    } catch (e) { setErr(e.message) }
  }
  useEffect(() => { load() }, []) // load on mount
  return (
    <div style={{padding:16}}>
      <div style={{display:'flex', gap:8}}>
        <input placeholder="Search books..." value={q} onChange={e=>setQ(e.target.value)} />
        <button onClick={load}>Search</button>
      </div>
      {err && <div style={{color:'red'}}>{err}</div>}
      <ul>
        {books.map(b => (
          <li key={b.id} style={{margin:'12px 0'}}>
            <b>{b.title}</b> — {b.author} — ₹{b.price} — Stock: {b.stockQuantity} &nbsp;
            <button onClick={()=>addToCart(b.id, 1)}>Add</button>
            <button onClick={()=>api.viewBook(b.id)}>Mark as Viewed</button>
          </li>
        ))}
      </ul>
    </div>
  )
}

function Cart({ cart, setCart, setPreview }) {
  const remove = (bookId) => setCart(prev => prev.filter(i => i.bookId !== bookId))
  const changeQty = (bookId, qty) => setCart(prev => prev.map(i => i.bookId===bookId ? {...i, quantity: qty} : i))
  const preview = async () => {
    if (cart.length===0) { setPreview(null); return; }
    const p = await api.previewCart(cart)
    setPreview(p)
  }
  useEffect(()=>{ preview() }, [cart]) // preview whenever cart changes

  return (
    <div style={{padding:16}}>
      <h3>Cart</h3>
      {cart.length===0 && <div>Cart is empty.</div>}
      {cart.map(i => (
        <div key={i.bookId} style={{display:'flex', gap:8, alignItems:'center', margin:'8px 0'}}>
          <code>{i.bookId}</code>
          <input type="number" min="1" value={i.quantity} onChange={e=>changeQty(i.bookId, parseInt(e.target.value||'1',10))} />
          <button onClick={()=>remove(i.bookId)}>Remove</button>
        </div>
      ))}
    </div>
  )
}

function Checkout({ cart, preview, clearCart }) {
  const [msg,setMsg] = useState('')
  const doCheckout = async () => {
    setMsg('')
    try {
      const order = await api.checkout(cart)
      setMsg(`Order placed: ${order.orderId} total ₹${order.total}`)
      clearCart()
    } catch (e) { setMsg(e.message) }
  }
  return (
    <div style={{padding:16}}>
      <h3>Checkout</h3>
      {preview && (
        <>
          <div>Total: ₹{preview.total}</div>
          <ul>
            {preview.items?.map(li => (
              <li key={li.bookId}>{li.title} x {li.quantity} (₹{li.unitPrice})</li>
            ))}
          </ul>
        </>
      )}
      <button disabled={!cart.length} onClick={doCheckout}>Place Order</button>
      {msg && <div style={{marginTop:8}}>{msg}</div>}
    </div>
  )
}

function Orders() {
  const [orders,setOrders] = useState([])
  const [err,setErr] = useState('')
  const load = async () => {
    try { setOrders(await api.myOrders()); setErr('') }
    catch (e) { setErr(e.message) }
  }
  useEffect(()=>{ load() },[])
  return (
    <div style={{padding:16}}>
      <h3>My Orders</h3>
      {err && <div style={{color:'red'}}>{err}</div>}
      <ul>
        {orders.map(o => (
          <li key={o.orderId}>
            {o.orderId} — total ₹{o.total}
          </li>
        ))}
      </ul>
    </div>
  )
}

function History() {
  const [items,setItems] = useState([])
  const [err,setErr] = useState('')
  useEffect(() => {
    (async () => {
      try { setItems(await api.history()); setErr('') }
      catch (e) { setErr(e.message) }
    })()
  }, [])
  return (
    <div style={{padding:16}}>
      <h3>Recently Viewed</h3>
      {err && <div style={{color:'red'}}>{err}</div>}
      <ul>
        {items.map(b => <li key={b.id}>{b.title} — {b.author}</li>)}
      </ul>
    </div>
  )
}

function Recs() {
  const [recs,setRecs] = useState([])
  const [err,setErr] = useState('')
  useEffect(() => {
    (async () => {
      try { setRecs(await api.recommendations()); setErr('') }
      catch (e) { setErr(e.message) }
    })()
  }, [])
  return (
    <div style={{padding:16}}>
      <h3>Recommended for you</h3>
      {err && <div style={{color:'red'}}>{err}</div>}
      <ul>
        {recs.map(b => <li key={b.id}>{b.title} — ₹{b.price}</li>)}
      </ul>
    </div>
  )
}

export default function App() {
  const [user,setUser] = useState(null)
  const [cart,setCart] = useState([]) // [{ bookId, quantity }]
  const [preview,setPreview] = useState(null)

  const addToCart = (bookId, quantity=1) => {
    setCart(prev => {
      const found = prev.find(i => i.bookId === bookId)
      if (found) return prev.map(i => i.bookId===bookId ? {...i, quantity: i.quantity + quantity} : i)
      return [...prev, { bookId, quantity }]
    })
  }

  const clearCart = () => { setCart([]); setPreview(null) }

  const onLogout = async () => {
    await api.logout()
    setUser(null)
  }

  useEffect(() => {
    (async () => {
      try { const me = await api.me(); setUser(me) } catch {}
    })()
  }, [])

  return (
    <BrowserRouter>
      <Nav user={user} onLogout={onLogout} />
      <Routes>
        <Route path="/" element={<Catalog addToCart={addToCart} />} />
        <Route path="/login" element={<Login setUser={setUser} />} />
        <Route path="/cart" element={
          <div style={{display:'grid', gridTemplateColumns:'1fr 1fr'}}>
            <Cart cart={cart} setCart={setCart} setPreview={setPreview} />
            <Checkout cart={cart} preview={preview} clearCart={clearCart} />
          </div>
        } />
        <Route path="/orders" element={<Orders />} />
        <Route path="/history" element={<History />} />
        <Route path="/recs" element={<Recs />} />
      </Routes>
    </BrowserRouter>
  )
}
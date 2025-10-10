const jsonHeaders = { 'Content-Type': 'application/json' };

async function handle(res) {
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`${res.status} ${res.statusText} ${text}`);
  }
  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) return res.json();
  return null;
}

export const api = {
  // AUTH
  login: (username, password) =>
    fetch('/api/users/login', {
      method: 'POST',
      headers: jsonHeaders,
      credentials: 'include',
      body: JSON.stringify({ username, password })
    }).then(handle),

  logout: () =>
    fetch('/api/users/logout', {
      method: 'POST',
      credentials: 'include'
    }).then(handle),

  me: () =>
    fetch('/api/users/me', { credentials: 'include' }).then(handle),

  register: (username, email, password) =>
    fetch('/api/users/register', {
      method: 'POST',
      headers: jsonHeaders,
      body: JSON.stringify({ username, email, password })
    }).then(handle),

  // BOOKS
  listBooks: () => fetch('/api/books').then(handle),
  searchBooks: (q) => fetch(`/api/books/search?q=${encodeURIComponent(q)}`).then(handle),
  getBook: (id) => fetch(`/api/books/${id}`).then(handle),

  // DELETE BOOKS (refactored to use handle)
  deleteBookById: (id) =>
    fetch(`/api/books/${encodeURIComponent(id)}`, {
      method: 'DELETE',
      credentials: 'include'
    }).then(handle),

  deleteBookByTitle: (title) =>
    fetch(`/api/books/by-title?title=${encodeURIComponent(title)}`, {
      method: 'DELETE',
      credentials: 'include'
    }).then(handle),

  // CART
  previewCart: (items) =>
    fetch('/api/cart/preview', {
      method: 'POST',
      headers: jsonHeaders,
      body: JSON.stringify({ items })
    }).then(handle),

  checkout: (items) =>
    fetch('/api/cart/checkout', {
      method: 'POST',
      headers: jsonHeaders,
      credentials: 'include',
      body: JSON.stringify({ items })
    }).then(handle),

  // ORDERS
  myOrders: () => fetch('/api/orders/me', { credentials: 'include' }).then(handle),

  // HISTORY
  viewBook: (bookId) =>
    fetch('/api/history/view', {
      method: 'POST',
      headers: jsonHeaders,
      credentials: 'include',
      body: JSON.stringify({ bookId })
    }).then(handle),
  history: () => fetch('/api/history', { credentials: 'include' }).then(handle),

  // RECOMMENDATIONS
  recommendations: () => fetch('/api/recommendations', { credentials: 'include' }).then(handle),
};
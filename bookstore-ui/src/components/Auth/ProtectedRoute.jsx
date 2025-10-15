import { Navigate } from 'react-router-dom';

export default function ProtectedRoute({ user, requireAdmin = false, children }) {
  // No user at all - redirect to login
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  // User exists but admin required and user is not admin
  if (requireAdmin && !user.isAdmin) {
    return <Navigate to="/" replace />;
  }
  
  // All checks passed
  return children;
}
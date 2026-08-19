import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuthState } from "../hooks/useAuth";

/**
 * Protected Route Component
 * Redirects to login if user is not authenticated
 */
function ProtectedRoute({ children, requiredRole = null }) {
  const { isAuthenticated, user } = useAuthState();
  const location = useLocation();

  if (!isAuthenticated) {
    // Redirect to login, but save the location they were trying to access
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check if user has required role
  if (requiredRole && user?.role !== requiredRole) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default ProtectedRoute;

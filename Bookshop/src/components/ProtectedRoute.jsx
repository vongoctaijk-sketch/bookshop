import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuthState } from "../hooks/useAuth";

function ProtectedRoute({ children, requiredRole = null }) {
  const { isAuthenticated, user } = useAuthState();
  const location = useLocation();

  if (isAuthenticated && !user) {
    return null;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  const hasRequiredRole =
    !requiredRole ||
    user?.role === requiredRole ||
    user?.authorities?.some(
      (authority) =>
        authority.toUpperCase() === `ROLE_${requiredRole.toUpperCase()}`,
    );

  if (requiredRole && !hasRequiredRole) {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default ProtectedRoute;

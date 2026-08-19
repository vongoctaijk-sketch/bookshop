import { useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./Pages/Login/LoginPage";
import Register from "./Pages/Login/RegisterPage";
import Dashboard from "./Pages/Admin/DashboardPage";
import Order from "./Pages/Admin/OrderPage";
import AdminLayout from "./Component/Layout/AdminLayout/AdminLayout";
import ShopLayout from "./Component/Layout/ShopLayout/ShopLayout";
import BookDetailsPage from "./Pages/Shop/BookDetailsPage";
import ShopPage from "./Pages/Shop/ShopPage";
import ProtectedRoute from "./components/ProtectedRoute";
import "./App.css";

function App() {
  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<ShopLayout />}>
        <Route index element={<ShopPage />} />
        <Route path="books/:id" element={<BookDetailsPage />} />
      </Route>

      {/* Auth Routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/signup" element={<Register />} />

      {/* Protected Admin Routes */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute requiredRole="admin">
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="order" element={<Order />} />
      </Route>

      {/* Fallback Route */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;

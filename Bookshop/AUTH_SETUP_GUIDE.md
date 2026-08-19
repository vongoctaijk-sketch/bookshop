# Redux + React Query + Auth Configuration Guide

## Overview

This guide explains the new authentication system setup with Redux, React Query, Zod, and React Hook Form.

## File Structure

```
src/
├── schemas/
│   └── authSchema.js          # Zod validation schemas
├── services/
│   └── authService.js         # API service methods
├── store/
│   ├── index.js               # Redux store configuration
│   └── slices/
│       └── authSlice.js       # Auth reducer and actions
├── hooks/
│   └── useAuth.js             # Custom hooks for auth operations
├── components/
│   └── ProtectedRoute.jsx     # Protected route wrapper
└── config/
    └── axios.js               # Axios instance with interceptors
```

## Key Features

### 1. Redux Authentication State Management

- **File**: `src/store/slices/authSlice.js`
- Manages user, token, isAuthenticated, loading, and error states
- Automatically persists tokens to localStorage
- Provides reducers for login, register, logout, and profile updates

### 2. React Query Integration

- **File**: `src/hooks/useAuth.js`
- Combines Redux actions with React Query mutations
- Automatic caching and request management
- Better performance with stale-while-revalidate pattern

### 3. Zod Validation Schemas

- **File**: `src/schemas/authSchema.js`
- Define validation rules for:
  - Login form
  - Registration form
  - Profile updates
  - Password changes

### 4. API Service Layer

- **File**: `src/services/authService.js`
- Centralized API endpoints
- Easy to test and maintain
- Consistent error handling

### 5. Axios Interceptors

- **File**: `src/config/axios.js`
- Automatic token injection in requests
- Token refresh handling (401 responses)
- Request/response logging for debugging

## Usage Examples

### Login with Form Validation

```jsx
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginSchema } from "../../schemas/authSchema";
import { useLoginMutation } from "../../hooks/useAuth";

function LoginPage() {
  const loginMutation = useLoginMutation();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      password: "",
      remember: false,
    },
  });

  const onSubmit = async (data) => {
    try {
      await loginMutation.mutateAsync({
        email: data.email,
        password: data.password,
      });
      // Redirect happens automatically via Redux state
    } catch (error) {
      console.error("Login failed:", error);
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <Controller
        name="email"
        control={control}
        render={({ field }) => (
          <input {...field} type="email" placeholder="Email" />
        )}
      />
      {errors.email && <span>{errors.email.message}</span>}

      <button type="submit" disabled={loginMutation.isPending}>
        {loginMutation.isPending ? "Logging in..." : "Login"}
      </button>
    </form>
  );
}
```

### Use Auth State in Components

```jsx
import { useAuthState } from "../../hooks/useAuth";

function Dashboard() {
  const { user, isAuthenticated, loading } = useAuthState();

  if (loading) return <div>Loading...</div>;

  if (!isAuthenticated) return <div>Not authenticated</div>;

  return <div>Welcome, {user.fullName}!</div>;
}
```

### Protected Routes

```jsx
import ProtectedRoute from "./components/ProtectedRoute";
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute requiredRole="admin">
            <Dashboard />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
```

### Register with Zod Validation

```jsx
import { useRegisterMutation } from "../../hooks/useAuth";
import { registerSchema } from "../../schemas/authSchema";

function RegisterPage() {
  const registerMutation = useRegisterMutation();

  const { control, handleSubmit } = useForm({
    resolver: zodResolver(registerSchema),
  });

  const onSubmit = (data) => {
    registerMutation.mutateAsync(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {/* Form fields */}
      <button type="submit" disabled={registerMutation.isPending}>
        Register
      </button>
    </form>
  );
}
```

### Update Profile

```jsx
import { useUpdateProfileMutation } from '../../hooks/useAuth';
import { updateProfileSchema } from '../../schemas/authSchema';

function EditProfile() {
  const updateMutation = useUpdateProfileMutation();

  const onSubmit = (data) => {
    updateMutation.mutateAsync(data);
  };

  return (
    // Form implementation
  );
}
```

### Logout

```jsx
import { useLogout } from "../../hooks/useAuth";

function Header() {
  const logoutMutation = useLogout();

  return <button onClick={() => logoutMutation.mutate()}>Logout</button>;
}
```

## API Endpoints Expected

The system expects the following API endpoints:

```
POST   /api/auth/login                  - Login user
POST   /api/auth/register               - Register new user
POST   /api/auth/logout                 - Logout user
GET    /api/auth/me                     - Get current user info
POST   /api/auth/refresh                - Refresh access token
PUT    /api/auth/profile                - Update user profile
POST   /api/auth/change-password        - Change password
POST   /api/auth/forgot-password        - Forgot password
POST   /api/auth/reset-password         - Reset password with token
```

### Expected Response Format for Login/Register:

```json
{
  "user": {
    "id": "user_id",
    "email": "user@example.com",
    "fullName": "User Name",
    "role": "user",
    "avatar": "url",
    "phone": "phone_number",
    "address": "address"
  },
  "token": "access_token_jwt",
  "refreshToken": "refresh_token_jwt"
}
```

## Token Refresh Flow

1. Request is made with access token
2. If response is 401 (Unauthorized)
3. Axios interceptor automatically:
   - Calls `/api/auth/refresh` with refresh token
   - Updates tokens in localStorage
   - Retries the original request
   - If refresh fails, redirects to login

## Environment Variables

Add to `.env` file:

```
VITE_API_URL=http://localhost:8080/api
```

## Redux Store Structure

```javascript
{
  auth: {
    user: {
      id: string,
      email: string,
      fullName: string,
      role: string,
      avatar: string,
      phone: string,
      address: string
    },
    token: string | null,
    refreshToken: string | null,
    isAuthenticated: boolean,
    loading: boolean,
    error: string | null
  }
}
```

## React Query Configuration

Default settings in `main.jsx`:

```javascript
{
  queries: {
    staleTime: 5 * 60 * 1000,        // 5 minutes
    gcTime: 10 * 60 * 1000,          // 10 minutes (formerly cacheTime)
    retry: 1,
  },
  mutations: {
    retry: 1,
  },
}
```

## Best Practices

1. **Use hooks for auth operations**
   - Always use `useLoginMutation()` instead of calling API directly
   - Use `useAuthState()` to access auth state

2. **Error Handling**
   - Errors are automatically stored in Redux state
   - Use error message from mutation or Redux state
   - Clear errors with `useClearAuthError()`

3. **Token Management**
   - Never manually set/remove tokens
   - Let Redux and interceptors handle it
   - Tokens are automatically refreshed on 401

4. **Loading States**
   - Use mutation loading state for forms
   - Use Redux loading state for app-wide indicators
   - Show appropriate UI feedback

5. **Protected Routes**
   - Always wrap admin/protected pages with `<ProtectedRoute>`
   - Pass `requiredRole` prop for role-based access
   - Handles redirect to login automatically

## Troubleshooting

### Token not being sent

- Check localStorage has `authToken` key
- Verify axios interceptor is running
- Check API request headers in browser DevTools

### Infinite redirect loop

- Verify refresh endpoint is correct
- Check refresh token is valid
- Clear localStorage and login again

### CORS issues

- Check backend CORS configuration
- Verify credentials are being sent
- Check allowed origins match frontend URL

### Zod validation not working

- Import schema correctly from `/schemas/authSchema.js`
- Use `zodResolver(schema)` in useForm
- Check field names match schema exactly

## Additional Resources

- [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
- [React Query Documentation](https://tanstack.com/query/latest)
- [Zod Validation](https://zod.dev/)
- [React Hook Form](https://react-hook-form.com/)

/**
 * Purpose: Frontend routing configuration (Router), mapping URLs to page components.
 * Interactions:
 * - Called by: main.tsx (the backbone of the entire application)
 * - Calls: All Page components (LoginPage, PetsPage, etc.), ProtectedRoute
 */
import {
  Navigate,
  Route,
  Routes
} from "react-router";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import PetsPage from "./pages/PetsPage";
import RegisterPage from "./pages/RegisterPage";
import { isAuthenticated } from "./auth/tokenStorage";

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <Navigate
            to={isAuthenticated() ? "/pets" : "/login"}
            replace
          />
        }
      />

      <Route path="/login" element={<LoginPage />} />

      <Route
        path="/register"
        element={<RegisterPage />}
      />

      <Route
        path="/pets"
        element={
          <ProtectedRoute>
            <PetsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="*"
        element={<Navigate to="/" replace />}
      />
    </Routes>
  );
}

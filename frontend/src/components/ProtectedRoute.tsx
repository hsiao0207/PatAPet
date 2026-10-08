/**
 * Purpose: Route guard component that intercepts unauthenticated users and redirects them to the login page.
 * Interactions:
 * - Called by: App.tsx (wraps protected routes, e.g., PetsPage)
 * - Calls: tokenStorage.ts (checks for the presence of a Token)
 */
import type { ReactNode } from "react";
import { Navigate } from "react-router";
import { isAuthenticated } from "../auth/tokenStorage";

interface ProtectedRouteProps {
    children: ReactNode;
}

export default function ProtectedRoute({
    children
}: ProtectedRouteProps) {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }

    return children;
}
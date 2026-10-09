/**
 * Purpose: Unified HTTP request engine for the frontend, wrapping the native fetch API.
 * Interactions:
 * - Called by: authApi.ts, petApi.ts (all API requests are routed through this engine)
 * - Calls: tokenStorage.ts (to retrieve and inject the Token into Headers, or clear it on 401 Unauthorized)
 */
import { getToken, removeToken } from "../auth/tokenStorage";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

interface ApiErrorBody {
    message?: string;
    errors?: Record<string, string>;
}

/**
 * Core API Request Engine
 * @param path - API endpoint path
 * @param options - Fetch options
 * @param authenticated - Whether to inject the JWT token (default: false)
 */
export async function apiRequest<T>(
    path: string,
    options: RequestInit = {},
    authenticated = false
): Promise<T> {
    const token = getToken();
    const headers = new Headers(options.headers);

    // Auto-set JSON Content-Type if body exists and is not FormData
    if (options.body && !(options.body instanceof FormData)) {
        if (!headers.has("Content-Type")) {
            headers.set("Content-Type", "application/json");
        }
    }

    // Inject JWT token for protected routes
    if (authenticated && token) {
        headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers
    });

    if (!response.ok) {
        // Global interceptor for 401 Unauthorized
        if (response.status === 401 && authenticated) {
            removeToken();
            window.location.href = "/login";
            throw new Error("Session expired. Please log in again.");
        }

        let errorMessage = `Request failed: ${response.status}`;
        
        try {
            const body = (await response.json()) as ApiErrorBody;
            if (body.message) {
                errorMessage = body.message;
            } else if (body.errors) {
                errorMessage = Object.values(body.errors).join(", ");
            }
        } catch {
            // Ignore parse errors, fallback to default message
        }

        throw new Error(errorMessage);
    }

    if (response.status === 204) {
        return undefined as T;
    }

    return response.json() as Promise<T>;
}
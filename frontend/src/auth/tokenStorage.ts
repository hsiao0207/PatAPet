/**
 * Purpose: JWT storage utility, responsible for interacting with the browser's LocalStorage.
 * Interactions:
 * - Called by (save): LoginPage.tsx, RegisterPage.tsx
 * - Called by (retrieve): http.ts (on every API request)
 * - Called by (remove): PetsPage.tsx (on logout), http.ts (when Token expires)
 */
const TOKEN_KEY = "patapet_token";

export function saveToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function removeToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

export function isAuthenticated(): boolean {
  return getToken() !== null;
}
/**
 * Purpose: Login page component allowing users to input Email and Password to authenticate.
 * Interactions:
 * - Called by: App.tsx (bound to the /login route)
 * - Calls: authApi.ts (sends login request), tokenStorage.ts (saves Token)
 */
import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";
import { login } from "../api/authApi";
import { saveToken } from "../auth/tokenStorage";

export default function LoginPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);

    async function handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        
        setError(null);
        setSubmitting(true);

        try {
            const response = await login({
                email,
                password
            });

            // On success, persist the JWT token and redirect to the dashboard
            saveToken(response.token);
            navigate("/pets");
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Login failed"
            );
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <main className="page">
            <section className="panel">
                <h1>Log in to PatAPet</h1>

                <form onSubmit={handleSubmit}>
                    <label>
                        Email
                        <input
                            type="email"
                            value={email}
                            onChange={event => setEmail(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Password
                        <input
                            type="password"
                            value={password}
                            onChange={event => setPassword(event.target.value)}
                            required
                        />
                    </label>

                    {error && <p className="error">{error}</p>}

                    <button type="submit" disabled={submitting}>
                        {submitting ? "Logging in..." : "Log in"}
                    </button>
                </form>

                <p>
                    No account? <Link to="/register">Register</Link>
                </p>
            </section>
        </main>
    );
}
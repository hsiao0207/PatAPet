/**
 * Purpose: Registration page component allowing users to create a new account.
 * Interactions:
 * - Called by: App.tsx (bound to the /register route)
 * - Calls: authApi.ts (sends registration request), tokenStorage.ts (saves Token upon successful login)
 */
import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router";
import { register } from "../api/authApi";
import { saveToken } from "../auth/tokenStorage";

export default function RegisterPage() {
    const navigate = useNavigate();

    const [fullName, setFullName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const [submitting, setSubmitting] = useState(false);

    async function handleSubmit(
        event: FormEvent<HTMLFormElement>
    ) {
        event.preventDefault();
        setError(null);
        setSubmitting(true);

        try {
            const response = await register({
                fullName,
                email,
                password
            });

            saveToken(response.token);
            navigate("/pets");
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Registration failed"
            );
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <main className="page">
            <section className="panel">
                <h1>Create an account</h1>

                <form onSubmit={handleSubmit}>
                    <label>
                        Full name
                        <input
                            type="text"
                            value={fullName}
                            onChange={event => setFullName(event.target.value)}
                            required
                        />
                    </label>

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
                            minLength={8}
                            value={password}
                            onChange={event => setPassword(event.target.value)}
                            required
                        />
                    </label>

                    {error && <p className="error">{error}</p>}

                    <button type="submit" disabled={submitting}>
                        {submitting ? "Creating..." : "Register"}
                    </button>
                </form>

                <p>
                    Already registered? <Link to="/login">Log in</Link>
                </p>
            </section>
        </main>
    );
}
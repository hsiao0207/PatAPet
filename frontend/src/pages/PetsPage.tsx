/**
 * Purpose: Pet list and management page, serves as the main dashboard after login.
 * Interactions:
 * - Called by: App.tsx (bound to the /pets route, protected by ProtectedRoute)
 * - Calls: petApi.ts (CRUD operations), tokenStorage.ts (logout action)
 */
import {
    useEffect,
    useState,
    type FormEvent
} from "react";
import { useNavigate } from "react-router";
import {
    createPet,
    deletePet,
    getMyPets
} from "../api/petApi";
import { removeToken } from "../auth/tokenStorage";
import type { PetResponse } from "../types/pet";

export default function PetsPage() {
    const navigate = useNavigate();

    const [pets, setPets] = useState<PetResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [name, setName] = useState("");
    const [breed, setBreed] = useState("");
    const [weightKg, setWeightKg] = useState("");
    const [photoUrl, setPhotoUrl] = useState("");
    const [tags, setTags] = useState("");
    const [creating, setCreating] = useState(false);

    useEffect(() => {
        async function loadPets() {
            try {
                const response = await getMyPets();
                setPets(response);
            } catch (error) {
                setError(
                    error instanceof Error
                        ? error.message
                        : "Unable to load pets"
                );
            } finally {
                setLoading(false);
            }
        }

        void loadPets();
    }, []);

    async function handleCreate(
        event: FormEvent<HTMLFormElement>
    ) {
        event.preventDefault();
        setError(null);
        setCreating(true);

        try {
            const newPet = await createPet({
                name,
                breed,
                weightKg: Number(weightKg),
                photoUrl,
                tags: tags
                    .split(",")
                    .map(tag => tag.trim())
                    .filter(Boolean)
            });

            setPets(current => [...current, newPet]);

            setName("");
            setBreed("");
            setWeightKg("");
            setPhotoUrl("");
            setTags("");
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to create pet"
            );
        } finally {
            setCreating(false);
        }
    }

    async function handleDelete(petId: string) {
        try {
            await deletePet(petId);

            setPets(current =>
                current.filter(pet => pet.id !== petId)
            );
        } catch (error) {
            setError(
                error instanceof Error
                    ? error.message
                    : "Unable to delete pet"
            );
        }
    }

    function handleLogout() {
        removeToken();
        navigate("/login");
    }

    if (loading) {
        return <p className="status">Loading pets...</p>;
    }

    return (
        <main className="page pets-page">
            <header className="page-header">
                <div>
                    <h1>My Pets</h1>
                    <p>Manage your PatAPet profiles.</p>
                </div>

                <button type="button" onClick={handleLogout}>
                    Log out
                </button>
            </header>

            {error && <p className="error">{error}</p>}

            <section className="panel">
                <h2>Add a pet</h2>

                <form onSubmit={handleCreate}>
                    <label>
                        Name
                        <input
                            value={name}
                            onChange={event => setName(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Breed
                        <input
                            value={breed}
                            onChange={event => setBreed(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Weight in kg
                        <input
                            type="number"
                            min="0.1"
                            step="0.1"
                            value={weightKg}
                            onChange={event => setWeightKg(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Photo URL
                        <input
                            type="url"
                            value={photoUrl}
                            onChange={event => setPhotoUrl(event.target.value)}
                            required
                        />
                    </label>

                    <label>
                        Tags, separated by commas
                        <input
                            value={tags}
                            onChange={event => setTags(event.target.value)}
                            placeholder="Friendly, Playful"
                        />
                    </label>

                    <button type="submit" disabled={creating}>
                        {creating ? "Adding..." : "Add pet"}
                    </button>
                </form>
            </section>

            <section className="pet-grid">
                {pets.length === 0 ? (
                    <p>No pets yet. Add your first pet above.</p>
                ) : (
                    pets.map(pet => (
                        <article className="pet-card" key={pet.id}>
                            <img src={pet.photoUrl} alt={pet.name} />

                            <h2>{pet.name}</h2>
                            <p>{pet.breed}</p>
                            <p>
                                {pet.weightKg} kg · {pet.sizeCategory}
                            </p>

                            <div className="tags">
                                {pet.tags.map(tag => (
                                    <span key={tag}>{tag}</span>
                                ))}
                            </div>

                            <button
                                type="button"
                                className="danger"
                                onClick={() => void handleDelete(pet.id)}
                            >
                                Delete
                            </button>
                        </article>
                    ))
                )}
            </section>
        </main>
    );
}
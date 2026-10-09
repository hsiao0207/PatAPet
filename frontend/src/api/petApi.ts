/**
 * Purpose: API client library for pet-related business logic.
 * Interactions:
 * - Called by: PetsPage.tsx
 * - Calls: http.ts (sends authenticated requests via the underlying engine), pet.ts (uses defined data types)
 */
import { apiRequest } from "./http";
import type {
    PetRequest,
    PetResponse
} from "../types/pet";

export function getMyPets(): Promise<PetResponse[]> {
    return apiRequest<PetResponse[]>(
        "/api/pets/me",
        {},
        true
    );
}

export function createPet(
    request: PetRequest
): Promise<PetResponse> {
    return apiRequest<PetResponse>(
        "/api/pets",
        {
            method: "POST",
            body: JSON.stringify(request)
        },
        true
    );
}

export function deletePet(petId: string): Promise<void> {
    return apiRequest<void>(
        `/api/pets/${petId}`,
        {
            method: "DELETE"
        },
        true
    );
}
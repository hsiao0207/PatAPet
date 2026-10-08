/**
 * Purpose: Defines TypeScript type contracts related to Pets.
 * Interactions:
 * - Imported by: petApi.ts, PetsPage.tsx
 */
export interface PetRequest {
    name: string;
    breed: string;
    weightKg: number;
    photoUrl: string;
    tags: string[];
}

export interface PetResponse extends PetRequest {
    id: string;
    sizeCategory: "SMALL" | "MEDIUM" | "LARGE";
    ownerId: string;
}
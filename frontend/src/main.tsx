/**
 * Purpose: The entry point of the React application. Mounts React to index.html and initializes the Router.
 * Interactions:
 * - Calls: App.tsx (loads the routing map), index.css (loads global styles)
 */
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router";
import App from "./App";
import "./index.css";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
);

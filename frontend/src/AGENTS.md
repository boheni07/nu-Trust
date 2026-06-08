# nu_Trust — Frontend

**Stack:** Vue 3.5.34 + Vite 8.0.12 + Vue Router 4.6.4 — ES Modules, JavaScript only.
**State:** Composition API (`ref`/`computed`/`provide`/`inject`). Pinia TBD.
**Language:** UI all 한국어, source code English.

## STRUCTURE

```
frontend/src/
├── main.js                     # App mount, router, global CSS (tokens.css)
├── App.vue                     # <router-view> root shell
├── components/                 # Reusable UI (Sidebar, Table, etc.)
├── router/index.js             # Lazy-loaded routes, nested layout
├── views/                      # Page-level SFCs — highest file count dir (9 files)
├── styles/tokens.css           # Global tokens: colors, spacing, typography, base reset
└── utils/mockData.js           # Hardcoded data for prototype iteration
```

## WHERE TO LOOK

| Task | File |
|------|------|
| Routing map, guards, chunk split | `router/index.js` |
| App shell (sidebar, header, nav) | `components/Sidebar.vue`, `views/MainLayout.vue` |
| Ticket/Kanban/List views | `views/` |
| Global design | `styles/tokens.css` — single source of truth |
| API placeholder/mock | `utils/mockData.js` — replace on backend hookup |

## CONVENTIONS

- **Components:** `<script setup>`, single-file, kebab-case filenames
- **Views:** one SFC per route, `onMounted` fetch (mock), `ref`/`computed` state
- **Router:** `createWebHistory` (no hash), lazy `() => import('@/views/...')`, nested child routes under `MainLayout`
- **CSS:** global tokens only. No scoped, no CSS modules, no CSS-in-JS
- **Paths:** `@/` alias → `frontend/src/` via Vite `@` resolver
- **No TypeScript yet.** Backend integration triggers TS migration.

## NOTES

- No Pinia/Vuex. State lives in component `ref` or `provide`/`inject`.
- `dist/` excluded — build artifact.
- Dev server: `npm run dev` → localhost:5173.

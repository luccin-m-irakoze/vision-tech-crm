# =============================================================================
# Stage 1 — Build the static SPA bundle with Vite.
# =============================================================================
FROM node:18-alpine AS build
WORKDIR /app

COPY package.json ./
RUN npm install

COPY . .
RUN npm run build

# =============================================================================
# Stage 2 — Serve /dist with NGINX, proxy /api to the backend.
# =============================================================================
FROM nginx:alpine AS runtime

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=20s --timeout=5s --retries=5 \
  CMD wget -qO- http://localhost/ || exit 1

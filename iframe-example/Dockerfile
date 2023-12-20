FROM caddy:2-alpine

RUN apk add gettext

WORKDIR /app

COPY static /app
COPY src/manifest-v0.1.json /app

COPY Caddyfile /app
RUN MANIFEST_FILE=$(cat manifest-v0.1.json) envsubst < /app/Caddyfile > /etc/caddy/Caddyfile
RUN rm /app/Caddyfile

FROM alpine:3.19.0 AS genkeys

RUN apk add --no-cache openssl

RUN openssl req -x509 \
    -nodes -days 3650 -newkey rsa:2048 \
    -keyout /nginx.key \
    -out /nginx.crt \
    -subj "/C=PL/ST=Denial/L=Springfield/O=Dis/CN=localhost"

FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm install --legacy-peer-deps

COPY . .

RUN npm run build

FROM nginx:stable-alpine3.17-slim AS prod

COPY --from=build /app/dist/bank-frontend /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=genkeys /nginx.key /etc/nginx/ssl/nginx.key
COPY --from=genkeys /nginx.crt /etc/nginx/ssl/nginx.crt
COPY ./nginx.conf /etc/nginx/nginx.conf

EXPOSE 443
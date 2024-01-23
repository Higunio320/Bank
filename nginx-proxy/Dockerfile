FROM alpine:3.19.0 AS build

RUN apk add --no-cache openssl

RUN openssl req -x509 \
    -nodes -days 3650 -newkey rsa:2048 \
    -keyout /nginx.key \
    -out /nginx.crt \
    -subj "/C=PL/ST=Denial/L=Springfield/O=Dis/CN=localhost"

FROM nginx:alpine

COPY --from=build /nginx.key /etc/nginx/ssl/nginx.key
COPY --from=build /nginx.crt /etc/nginx/ssl/nginx.crt
COPY ./nginx.conf /etc/nginx/nginx.conf

EXPOSE 443


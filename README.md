# Bank

# How to run

Copy the content of example.env into .env:
```
cp example.env .env
```
and change the variables if you need to. Then run
```
docker compose build
docker compose up -d
```
When all containers are ready (it takes a while for backend to start due to hashing) visit https://localhost

# Example users data
User 1:
```
username: user123
password: abcdefghijklmnoprsuwxyz
```

User 2:
```
username: user234
password: 1234567890abcdeghijkl
```

# mini-marqetplace 
 
## tech stack
- java 25
- spring boot 4.x
- postgresql (16) (used pessimistic locking for order consistency (`FOR UPDATE`),used JdbcClient for database connectivity)
- redis
- docker and docker-compose support
 

 ### how to execute
```bash

git clone https://github.com/Jaxongirshoh/mini-marqetplace.git
cd mini-marqetplace
docker compose up -d --build
Dummy Readme for Task2

connecting to database:
- docker ps
- psql -h localhost -p 5432 -U user -d financedb
- enter the user password (123)

running docker
- ./gradlew clean shadowJar
- docker-compose up --build
- docker attach hint1k_finance
- press enter or any key except 0 or 1 or 2
# Budujemy wszystkie mikroserwisy (bez testów)
mvn clean package -T 1C -DskipTests

# Restartujemy wszystkie usługi w Dockerze
docker-compose down
docker-compose up --build -d
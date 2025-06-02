FROM eclipse-temurin:21-jdk

# Ustaw katalog roboczy
WORKDIR /app

# Skopiuj aplikację
COPY target/products-service-0.0.1-SNAPSHOT.jar app.jar

# Ustaw zmienną środowiskową z opcjami JVM (przydział pamięci)
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Otwórz porty
EXPOSE 8081 9090

# Uruchom aplikację z przekazaniem opcji JVM
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar

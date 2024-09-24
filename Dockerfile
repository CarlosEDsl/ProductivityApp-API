# Etapa de build usando uma imagem Maven com JDK 22
FROM maven:3.9.5-openjdk-22 AS build

# Copiar o código da aplicação para o container
COPY . .

# Compilar a aplicação com Maven
RUN mvn clean install

# Imagem final usando OpenJDK 22
FROM openjdk:22-jdk-slim

# Expor a porta onde a aplicação será executada
EXPOSE 8080

# Copiar o JAR gerado na fase de build
COPY --from=build /target/productivityApp-0.0.1-SNAPSHOT.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT [ "java", "-jar", "app.jar" ]
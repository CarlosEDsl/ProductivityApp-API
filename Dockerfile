FROM ubuntu:latest AS build

# Instalar o JDK 22
RUN apt-get update
RUN apt-get install openjdk-22-jdk -y

# Copiar o código da aplicação para o container
COPY . .

# Instalar o Maven para compilar a aplicação
RUN apt-get install maven -y

# Compilar a aplicação
RUN mvn clean install

# Segunda etapa para uma imagem mais leve
FROM openjdk:22-jdk-slim

# Expor a porta onde a aplicação será executada (ajuste para a porta que a aplicação utiliza)
EXPOSE 8080

# Copiar o JAR gerado na fase de build para o container final
COPY --from=build /target/productivityApp-0.0.1-SNAPSHOT.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT [ "java", "-jar", "app.jar" ]

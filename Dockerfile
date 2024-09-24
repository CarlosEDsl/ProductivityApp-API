# Etapa de build
FROM openjdk:22-jdk AS build

# Instalar Maven e outras dependências
RUN apt-get update && apt-get install -y maven

# Copiar o código da aplicação para o container
COPY . .

# Compilar a aplicação com Maven
RUN mvn clean install

# Imagem final
FROM openjdk:22-jdk-slim

# Expor a porta onde a aplicação será executada
EXPOSE 8080

# Copiar o JAR gerado no estágio de build
COPY --from=build /target/productivityApp-0.0.1-SNAPSHOT.jar app.jar

# Comando para iniciar a aplicação
ENTRYPOINT [ "java", "-jar", "app.jar" ]

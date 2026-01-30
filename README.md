# 🚀 Desafio Bolt — Backend

Projeto backend desenvolvido em **Kotlin**, com foco em boas práticas de arquitetura e organização.  
A aplicação utiliza **PostgreSQL**, **Flyway** para versionamento de banco de dados e **Docker** para subir o ambiente de forma simples e padronizada.

---

## 🛠️ Tecnologias utilizadas

- **Kotlin**
- **Spring Boot**
- **PostgreSQL**
- **Flyway (migrations de banco de dados)**
- **Docker & Docker Compose**
- **Gradle**
- **Git**

---

## 📦 Pré-requisitos

Antes de começar, você precisa ter instalado na sua máquina:

- [Docker](https://www.docker.com/)
- JDK 21

---

## Primeiro  passo  

Clonar o repositório:

```bash
  git clone https://github.com/giovannadiniz/desafio-bolt-backend.git
```

---

## 🐘 Subindo o banco de dados com Docker

Na pasta raiz do projeto **`desafio-bolt-backend`**, execute:

```bash
  docker compose up -d
```

Banco de dados vai rodar na porta `5432`
- Nome do banco: `geracao`
- Usuário: `postgres`
- Senha: `postgres`
- Schemas: `public` e `energia`


---

## Rodando a aplicação



Na pasta raiz do projeto **`desafio-bolt-backend`**, execute:

 **`desafio-bolt-backend`**, execute:

```bash
  ./gradlew bootRun
```

Ou dê start na aplicação através da sua IDE.




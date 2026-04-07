# Plataforma de Vendas Corporativas (Backend)

API RESTful desenvolvida para gerenciamento de um marketplace corporativo, permitindo que empresas
cadastrem produtos e usuários realizem compras em um único ambiente.

---

## Sobre o projeto

Este projeto implementa o backend de uma plataforma multi-tenant onde:

* Empresas podem gerenciar seus próprios catálogos de produtos
* Usuários podem atuar como **consumidores** ou **colaboradores**
* Compras podem ser realizadas entre diferentes empresas
* O sistema garante **autorização baseada em regras de negócio**

A aplicação segue os princípios de:

* RESTful API
* Stateless (uso de JWT)
* Separação em camadas (Controller, Service, Repository)

---

## Funcionalidades principais

### Autenticação e Autorização

* Cadastro de usuários (com ou sem vínculo com empresa)
* Login com geração de token JWT
* Autenticação stateless (sem sessão no servidor)
* Autorização baseada em:

    * Papel do usuário (consumer / collaborator)
    * Contexto de domínio (empresa do usuário vs empresa do produto)

---

### Gestão de Usuários

* Cadastro de novos usuários
* Identificação do usuário autenticado
* Associação opcional com empresa

---

### Gestão de Empresas

* Cadastro de empresas
* Listagem de empresas
* Associação de usuários a empresas

---

### Gestão de Produtos

* Criação de produtos (somente colaboradores)
* Listagem de produtos (todos os usuários autenticados)
* Atualização de produtos (restrito à própria empresa)
* Exclusão de produtos (restrito à própria empresa)

#### Regras importantes:

* Um co só pode modificar produtos da sua própria empresa
* Produtos pertencem obrigatoriamente a uma empresa

---

### Transações (Compras)

* Registro de compras
* Associação entre usuário e produto
* Armazenamento do preço no momento da compra
* Listagem das Transações

---

## Regras de Negócio

* Usuários podem ou não estar vinculados a uma empresa
* Apenas usuários vinculados podem gerenciar produtos
* Usuários só podem alterar produtos da sua própria empresa
* Todos os usuários autenticados podem:

    * Visualizar produtos
    * Realizar compras

---

## Modelo de Autorização

A aplicação utiliza uma abordagem híbrida:

* **RBAC (Role-Based Access Control)**

    * Define permissões por tipo de usuário

* **ABAC (Attribute-Based Access Control)**

    * Garante regras contextuais, como:

        * `user.company_id == product.company_id`

---

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
Controller → Service → Repository
```

* **Controller**: entrada HTTP
* **Service**: regras de negócio e autorização
* **Repository**: acesso a dados

---

## Fluxo de Autenticação

1. Usuário realiza login
2. API retorna um token JWT
3. Cliente envia o token em cada requisição:

   ```
   Authorization: Bearer <token>
   ```
4. O backend valida o token e identifica o usuário
5. As regras de autorização são aplicadas no Service

---

## Principais Endpoints

### Auth

* `POST /auth/register`
* `POST /auth/login`

### Produtos

* `GET /products`
* `GET /products/{id}`
* `POST /products`
* `PUT /products/{id}`

### Transações

* `POST /transactions`
* `GET /transactions`
* `GET /transactions/id`

### Empresas (opcional)

* `POST /companies`
* `GET /companies`
* `GET /companies/{id}`

---

## Tecnologias utilizadas

* Java 21
* Spring Boot
* Spring Security
* JWT (JSON Web Token)
* JPA / Hibernate
* PostgreSQL

---

## Objetivo do projeto

Este projeto foi desenvolvido com foco em:

* Aplicar boas práticas de backend
* Demonstrar controle de autenticação e autorização
* Modelar regras de negócio reais
* Construir uma API limpa, organizada e escalável

---

## Diferenciais técnicos

* Autorização baseada em contexto (empresa vs produto)
* Separação clara de responsabilidades
* Uso de JWT com abordagem stateless
* Modelagem consistente de domínio
* Código estruturado para evolução futura

---

## Licença

Este projeto é de uso educacional e para fins de avaliação técnica.

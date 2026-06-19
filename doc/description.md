O objetivo é **testar minhas capacidades atuais como desenvolvedor Backend Java**, eu vou construir o PDV como um sistema próximo do que uma pequena rede de lojas realmente utilizaria.

A ideia não é criar apenas um CRUD de produtos e vendas, mas um sistema com regras de negócio, segurança, auditoria e consistência transacional.

# Visão Geral

**Nome do projeto:** PDV

Um sistema responsável por:
- Gestão de produtos
- Controle de estoque
- Controle de caixa
- Gestão de clientes
- Registro de vendas
- Relatórios gerenciais
- Controle de usuários e permissões

---

# Arquitetura

Vou utilizar:

```text
Java 21
Spring Boot
Spring Security
JWT
PostgreSQL
Docker
Swagger
JUnit
Mockito
Flyway
MapStruct
```

E estruturar seguindo Arquitetura em Camadas:

```text
controller
service
repository
```

---

# Módulo de Autenticação

## Usuários

```java
User
```

Atributos:

```text
id
nome
email
senha
ativo
```

---

## Perfis

```text
ADMIN
GERENTE
OPERADOR
```

### ADMIN

Pode:
- Gerenciar usuários
- Cancelar vendas
- Fechar caixas
- Acessar relatórios

### GERENTE

Pode:
- Gerenciar produtos
- Consultar relatórios
- Abrir e fechar caixa

### OPERADOR

Pode:
- Registrar vendas
- Consultar produtos
- Consultar clientes

---

# Módulo de Produtos

## Produto

```java
Produto
```

Campos:

```text
id
nome
descricao
codigoBarras
precoVenda
custo
estoqueAtual
estoqueMinimo
ativo
```

---

## Categoria

```java
Categoria
```

Exemplos:

```text
Bebidas
Limpeza
Alimentos
Eletrônicos
```

---

# Módulo de Estoque

Aqui começa a parte interessante.

Ao invés de alterar diretamente:

```java
produto.setEstoque(...)
```

crie movimentações.

---

## Movimentação de Estoque

```java
MovimentacaoEstoque
```

Campos:

```text
id
produto
tipo
quantidade
dataHora
observacao
```

Tipos:

```text
ENTRADA
SAIDA
AJUSTE
```

---

### Benefício

Você terá histórico completo.

Exemplo:

```text
Produto: Coca-Cola

+100 Entrada fornecedor
-2 Venda
-1 Venda
+10 Ajuste
```

Isso é muito utilizado em sistemas reais.

---

# Módulo de Caixa

## Caixa

```java
Caixa
```

Campos:

```text
id
operador
dataAbertura
dataFechamento
saldoInicial
saldoFinal
status
```

Status:

```text
ABERTO
FECHADO
```

---

## Regra Importante

Um operador só pode possuir:

```text
1 caixa aberto
```

---

## Fluxo

```text
Abrir Caixa

↓

Realizar vendas

↓

Registrar movimentações

↓

Fechar Caixa
```

---

# Módulo de Clientes

## Cliente

Campos:

```text
id
nome
cpf
email
telefone
```

---

## Histórico

O cliente poderá possuir:

```text
compras realizadas
valor gasto
última compra
```

---

# Módulo Principal: Vendas

Esse é o coração do sistema.

---

## Venda

```java
Venda
```

Campos:

```text
id
cliente
caixa
usuario
dataHora
valorTotal
status
```

Status:

```text
ABERTA
FINALIZADA
CANCELADA
```

---

## ItemVenda

```java
ItemVenda
```

Campos:

```text
produto
quantidade
precoUnitario
subtotal
```

---

# Fluxo de Venda

```text
Selecionar Cliente

↓

Adicionar Produtos

↓

Calcular Total

↓

Informar Pagamento

↓

Confirmar Venda

↓

Baixar Estoque

↓

Gerar Movimentação

↓

Finalizar Venda
```

Tudo dentro de uma única transação.

```java
@Transactional
```

---

# Pagamentos

## Pagamento

```java
Pagamento
```

Campos:

```text
id
venda
valor
formaPagamento
```

Formas:

```text
PIX
DINHEIRO
DEBITO
CREDITO
```

---

## Desafio Interessante

Permitir pagamento misto.

Exemplo:

```text
R$ 50 PIX
R$ 30 Dinheiro
```

Total:

```text
R$ 80
```

Isso adiciona uma camada extra de modelagem.

---

# Cancelamento de Venda

Somente:

```text
ADMIN
```

ou

```text
GERENTE
```

---

## Regras

Ao cancelar:

```text
Restaurar estoque

Criar movimentação de entrada

Registrar auditoria

Alterar status da venda
```

---

# Auditoria

Crie uma entidade:

```java
Auditoria
```

Campos:

```text
usuario
acao
entidade
entidadeId
dataHora
```

---

Exemplos:

```text
VENDA_CANCELADA

PRODUTO_CRIADO

CAIXA_FECHADO
```

---

# Relatórios

## Produtos mais vendidos

Retornar:

```text
Produto
Quantidade Vendida
Valor Vendido
```

---

## Faturamento Diário

```text
Data
Quantidade de vendas
Valor total
```

---

## Faturamento Mensal

```text
Mês
Receita
```

---

## Produtos Sem Estoque

```text
estoqueAtual = 0
```

---

## Produtos Abaixo do Estoque Mínimo

```text
estoqueAtual < estoqueMinimo
```

---

# Funcionalidades que Demonstram Maturidade

## Specifications

Filtros:

```text
Produto por nome
Produto por categoria
Produto ativo
Produto sem estoque
```

---

## Paginação

Todos os endpoints de consulta.

---

## Tratamento Global de Exceções

```java
@RestControllerAdvice
```

---

## Flyway

Versionamento do banco.

---

## Testes

### Unitários

- Service
- Regras de negócio

### Integração

- Repositories
- Controllers


---

# Evolução para um Projeto de Portfólio Forte

Após concluir a versão principal:

### Redis

Cache para consultas de produtos.

### RabbitMQ

Eventos:

```text
Venda Finalizada

Venda Cancelada

Estoque Baixo
```

### Dashboard

Indicadores:

```text
Faturamento do dia

Produtos vendidos

Caixas abertos

Clientes cadastrados
```

### Multi-Loja

Adicionar:

```java
Loja
```

e permitir:

```text
Loja A

Loja B

Loja C
```

Cada uma com:

- Estoque próprio

- Caixa próprio

- Funcionários próprios


---

Se você implementar esse PDV completo com Clean Architecture, DDD tático básico (Entities, Value Objects, Aggregates), testes automatizados, autenticação JWT e documentação Swagger, ele deixará de ser um projeto de estudo e passará a se parecer com um sistema corporativo real desenvolvido por um backend Java júnior avançado ou pleno inicial.
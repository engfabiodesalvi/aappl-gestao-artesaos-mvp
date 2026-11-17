# AAPPL — Gestão de Feiras e Repasses para Artesãos (MVP)

![Java](https://img.shields.io/badge/Java-21%20LTS-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue?style=flat-square&logo=postgresql)
![ODS 8.3](<https://img.shields.io/badge/ONU-ODS%208%20(Meta%208.3)-A21942?style=flat-square>)

Sistema web responsivo (_mobile-first_) desenvolvido para automatizar o controle de estoque consignado, o registro de vendas presenciais em feiras comunitárias e o rateio financeiro da **Associação de Artesãos e Pequenos Produtores Locais (AAPPL)**.

---

## Stack

Java 21 • Spring Boot 3.2 • Spring Security (BCrypt) • Thymeleaf • Spring Data JPA • PostgreSQL • JUnit 5 • GitHub Actions

## Como executar

Pré-requisitos: JDK 21, PostgreSQL ativo.

    git clone https://github.com/engfabiodesalvi/aappl-gestao-artesaos-mvp.git
    cd aappl-gestao-artesaos-mvp
    export DB_URL=jdbc:postgresql://localhost:5432/aappl_mvp
    export DB_USERNAME=aappl
    export DB_PASSWORD=sua_senha
    ./mvnw spring-boot:run

Acesse http://localhost:8080

## Testes

    ./mvnw verify

---

## 🎯 Problema e Impacto Social (ODS 8 — Meta 8.3)

Atualmente, o fechamento de caixa das feiras da AAPPL demanda cerca de **1h45min de conferência manual**, gerando riscos de divergência no estoque consignado e atrasos nos repasses aos **45 artesãos associados**.

Este MVP promove o trabalho decente, a formalização e a geração de renda sustentável (**ODS 8, Meta 8.3**), oferecendo uma frente de caixa rápida, transparente e acessível para voluntários e coordenadores.

---

## 🛠️ Arquitetura e Pilha Tecnológica

O sistema adota uma **Arquitetura Monolítica em Camadas** (`Controller` → `Service` → `Repository` → `Model`), dimensionada para alta coesão, facilidade de manutenção e baixa latência em dispositivos móveis:

- **Linguagem e Runtime:** Java 21 (LTS)
- **Framework Web e Segurança:** Spring Boot 3.2, Spring Security (autenticação com hash adaptativo **BCrypt**), Spring Validation
- **Motor de Templates (UI):** Thymeleaf + HTML5/CSS3 responsivo (_mobile-first_)
- **Persistência e Banco de Dados:** Spring Data JPA / Hibernate + PostgreSQL (hospedado no Supabase)
- **Qualidade e CI:** JUnit 5, Mockito, Spring Boot Test e GitHub Actions

---

## 📋 Escopo Funcional e Requisitos do MVP

| Código    | Descrição                                                                                | Prioridade  |
| :-------- | :--------------------------------------------------------------------------------------- | :---------- |
| **RF01**  | Autenticação segura de voluntários e coordenadores com controle de perfil                | Obrigatório |
| **RF02**  | Cadastro e consulta rápida de produtos consignados vinculados ao artesão                 | Obrigatório |
| **RF03**  | Registro de vendas presenciais (Frente de Caixa) com baixa automática de estoque         | Obrigatório |
| **RF04**  | Relatório consolidado de fechamento da feira e cálculo automático de repasse por artesão | Obrigatório |
| **RF05**  | Catálogo comunitário público para divulgação das peças                                   | Desejável   |
| **RNF01** | Segurança alinhada ao OWASP Top 10, senhas com BCrypt e minimização de dados (LGPD)      | Obrigatório |
| **RNF03** | Resposta das transações críticas em até 2 segundos em conexões móveis 4G                 | Obrigatório |
| **RNF04** | Acessibilidade: áreas de toque ≥ 48x48px, contraste ≥ 4.5:1 e alertas multimodais        | Obrigatório |

---

## 📂 Estrutura do Projeto

```text
aappl-gestao-artesaos-mvp/
├── .github/
│   └── workflows/
│       └── ci.yml                  # Pipeline de Integração Contínua (Build + Testes)
├── src/
│   ├── main/
│   │   ├── java/br/org/aappl/mvp/
│   │   │   ├── config/             # Configurações de Segurança (Spring Security / BCrypt)
│   │   │   ├── controller/         # Controladores Web MVC (Thymeleaf)
│   │   │   ├── model/              # Entidades JPA (Artesao, Produto, Venda, Usuario)
│   │   │   ├── repository/         # Interfaces Spring Data JPA
│   │   │   └── service/            # Regras de negócio (baixa de estoque e rateio)
│   │   └── resources/
│   │       ├── static/             # CSS acessível (contraste 4.5:1) e ícones PWA
│   │       ├── templates/          # Telas Thymeleaf (Login, Caixa, Fechamento)
│   │       └── application.yml     # Configurações de ambiente e banco de dados
│   └── test/
│       └── java/br/org/aappl/mvp/  # Testes unitários (JUnit 5) e de integração (MockMvc)
├── .gitignore
├── pom.xml
└── README.md

```

## 🔑 Acesso para avaliação

Aplicação em produção: https://aappl-gestao-artesaos.onrender.com

| Perfil       | Usuário                        | Acesso                    |
| ------------ | ------------------------------ | ------------------------- |
| Voluntário   | voluntario.balcao@aappl.org.br | Login e Frente de Caixa   |
| Coordenadora | coordenadora@aappl.org.br      | Login, Caixa e Fechamento |

Senha dos dois usuários de demonstração: `Aappl@Avaliacao2026`

Todos os dados (artesãos, produtos e vendas) são fictícios. O plano gratuito do Render hiberna por inatividade, então o primeiro acesso pode levar cerca de 1 minuto.

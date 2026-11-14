# AAPPL — Gestão de Feiras e Repasses para Artesãos (MVP)

![Java](https://img.shields.io/badge/Java-21%20LTS-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-blue?style=flat-square&logo=postgresql)
![ODS 8.3](https://img.shields.io/badge/ONU-ODS%208%20(Meta%208.3)-A21942?style=flat-square)

Sistema web responsivo (*mobile-first*) desenvolvido para automatizar o controle de estoque consignado, o registro de vendas presenciais em feiras comunitárias e o rateio financeiro da **Associação de Artesãos e Pequenos Produtores Locais (AAPPL)**.

---

## 🎯 Problema e Impacto Social (ODS 8 — Meta 8.3)

Atualmente, o fechamento de caixa das feiras da AAPPL demanda cerca de **1h45min de conferência manual**, gerando riscos de divergência no estoque consignado e atrasos nos repasses aos **45 artesãos associados**. 

Este MVP promove o trabalho decente, a formalização e a geração de renda sustentável (**ODS 8, Meta 8.3**), oferecendo uma frente de caixa rápida, transparente e acessível para voluntários e coordenadores.

---

## 🛠️ Arquitetura e Pilha Tecnológica

O sistema adota uma **Arquitetura Monolítica em Camadas** (`Controller` → `Service` → `Repository` → `Model`), dimensionada para alta coesão, facilidade de manutenção e baixa latência em dispositivos móveis:

- **Linguagem e Runtime:** Java 21 (LTS)
- **Framework Web e Segurança:** Spring Boot 3.2, Spring Security (autenticação com hash adaptativo **BCrypt**), Spring Validation
- **Motor de Templates (UI):** Thymeleaf + HTML5/CSS3 responsivo (*mobile-first*)
- **Persistência e Banco de Dados:** Spring Data JPA / Hibernate + PostgreSQL (hospedado no Supabase)
- **Qualidade e CI:** JUnit 5, Mockito, Spring Boot Test e GitHub Actions

---

## 📋 Escopo Funcional e Requisitos do MVP

| Código | Descrição | Prioridade |
| :--- | :--- | :--- |
| **RF01** | Autenticação segura de voluntários e coordenadores com controle de perfil | Obrigatório |
| **RF02** | Cadastro e consulta rápida de produtos consignados vinculados ao artesão | Obrigatório |
| **RF03** | Registro de vendas presenciais (Frente de Caixa) com baixa automática de estoque | Obrigatório |
| **RF04** | Relatório consolidado de fechamento da feira e cálculo automático de repasse por artesão | Obrigatório |
| **RF05** | Catálogo comunitário público para divulgação das peças | Desejável |
| **RNF01** | Segurança alinhada ao OWASP Top 10, senhas com BCrypt e minimização de dados (LGPD) | Obrigatório |
| **RNF03** | Resposta das transações críticas em até 2 segundos em conexões móveis 4G | Obrigatório |
| **RNF04** | Acessibilidade: áreas de toque ≥ 48x48px, contraste ≥ 4.5:1 e alertas multimodais | Obrigatório |

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
└── README.md'
# My SUS History API
Essa api é o MVP desenvolvido para o **Hackaton - Phase 5** da Especialização em Arquitetura e Desenvolvimento Java 
da FIAP. O sistema foi projetado para que pacientes do SUS mantenham seu histórico de atendimentos médicos sempre 
acessível, e para que profissionais de saúde possam, de maneira simples e segura, visualizar esse histórico e registrar 
novos atendimentos.
A aplicação utiliza **Java 17**, **Spring Boot** e **Maven**, com um banco de dados **H2** em memória para testes, e 
conta com **Mockito** e **JUnit 5** para cobertura de testes unitários. O **Lombok** acelera o desenvolvimento, e a 
documentação interativa é gerada automaticamente via **Swagger**.

## Descrição do Projeto
Este MVP demonstra uma solução prática e inovadora para o desafio de unificar o prontuário do paciente do SUS.  
Com ele, é possível:

- **Cadastrar** pacientes e seus dados básicos (nome, CPF e data de nascimento);
- **Gerar** tokens de acesso temporário para compartilhamento seguro do histórico;
- **Consultar** atendimentos por CPF (quando for o próprio paciente consultando) ou por token, sem expor informações sensíveis;
- **Registrar** novos atendimentos via token, garantindo rastreabilidade e segurança.

A proposta é resolver um problema real de conectividade e dispersão de dados em múltiplas unidades de saúde, tornando o 
histórico médico verdadeiramente portátil e de responsabilidade do paciente.

## Tecnologias Utilizadas
- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **Maven**
- **Banco de Dados H2**
- **Banco de Dados Mysql**
- **Mockito** e **JUnit 5**
- **Lombok**
- **Swagger**
- **Spotless**
- **Jacoco**

## Estrutura do Projeto
O projeto segue uma arquitetura modularizada, organizada nas seguintes camadas:
- `core`: Contém as regras de negócio do sistema.
- `domain`: Define as entidades principais do domínio.
- `domain.exception`: Exceções personalizadas para regras de negócio.
- `gateway`: Interfaces para interação com o banco de dados.
- `usecase`: Contém os casos de uso do sistema.
- `usecase.exception`: Exceções personalizadas para regras de negócio.
- `entrypoint.configuration`: Configurações do sistema, incluindo tratamento de exceções.
- `entrypoint.controller`: Controladores responsáveis por expor os endpoints da API.
- `infrastructure.gateway`: Implementações das interfaces de gateway.
- `infrastructure.persistence.entity`: Representação das entidades persistidas no banco de dados.
- `infrastructure.persistence.repository`: Interfaces dos repositórios Spring Data JPA.
- `presenter`: Representação dos dados de saída para a API.

## Pré-requisitos
- Java 17
- Maven 3.6+
- IDE como IntelliJ IDEA ou Eclipse

## Configuração e Execução
1. **Clone o repositório**:
   ```bash
   git clone https://github.com/GabiFerraz/MySusHistory-API.git
   ```
2. **Instale as dependências:**
   ```bash
   mvn clean install
   ```
3. **Execute o projeto:**
   ```bash
   mvn spring-boot:run
   ```

## Uso da API
Para visualização dos dados da api no banco de dados H2, rodar o comando: **mvn "-Dspring-boot.run.profiles=h2" spring-boot:run**
e acessar localmente o banco através do endpoint:
- **Banco H2**: http://localhost:8080/h2-console
- **Driver Class**: org.h2.Driver
- **JDBC URL**: jdbc:h2:mem:mysushistory
- **User Name**: gm
- **Password**:

Os endpoints estão documentados via **Swagger**:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Swagger JSON**: http://localhost:8080/v3/api-docs

### Possibilidades de Chamadas da API
1. **Cadastro do Paciente:**
```json
curl --location --request POST 'localhost:8080/api/patients?name=Gabis&cpf=12345678901&birthDate=1990-09-15' \
--header 'Content-Type: application/json' \
--data ''
```

2. **Geração de Token:**
```json
curl --location --request POST 'localhost:8080/api/patients/12345678901/token?expiresInMinutes=10' \
--header 'Content-Type: application/json' \
--data ''
```

3. **Busca do Histórico do Paciente pelo CPF:**
```json
curl --location 'localhost:8080/api/patients/12345678901/history' \
--header 'Content-Type: application/json' \
--data ''
```

4. **Criação de Atendimento pelo Token:**
```json
curl --location 'localhost:8080/api/public/medical-records?token=a374d8f4-f9a3-4dad-9b74-cb0f3d1558ad' \
--header 'Content-Type: application/json' \
--data '{
"unit": "UBS Jardim das Flores",
"professionalName": "Dra. Mariana Silva",
"diagnosis": "Hipertensão arterial",
"treatment": "Uso contínuo de losartana 50mg",
"notes": "Paciente será reavaliado em 30 dias"
}'
```

5. **Busca do Histórico do Paciente pelo Token:**
```json
curl --location 'localhost:8080/api/public/medical-records?token=a374d8f4-f9a3-4dad-9b74-cb0f3d1558ad' \
--header 'Content-Type: application/json' \
--data ''
```


## Testes
Para rodar os testes unitários:
```bash
mvn test
```

**Rodar o coverage:**
   ```bash
   mvn clean package
   ```
Depois, acessar pasta target/site/jacoco/index.html

O projeto inclui testes unitários, testes de integração e testes de arquitetura para garantir a qualidade e
confiabilidade do MVP.


## Desenvolvedora:
- **Gabriela de Mesquita Ferraz** - RM: 358745
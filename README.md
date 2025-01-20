# Projeto: ScheduleApp

## Visão Geral

ScheduleApp é um aplicativo de gerenciamento de compromissos que está sendo desenvolvido em Kotlin. Ele utiliza tecnologias modernas como Jetpack Compose para a interface do usuário, Room para persistência local e Retrofit para comunicação com APIs remotas. O projeto segue rigorosamente os princípios de Clean Architecture, separando responsabilidades em camadas bem definidas: **Data**, **Domain** e **Presentation**. Além disso, um sistema de cache robusto otimiza a performance ao reduzir acessos redundantes ao banco e à API, realiza testes com JUnit 5 e resolve problemas de sincronização entre diferentes fontes de dados.

## Tecnologias Utilizadas

- **Kotlin**: Linguagem moderna e segura que oferece sintaxe concisa e interoperabilidade com Java.
- **Jetpack Compose**: Framework de UI declarativa que facilita a criação de interfaces modernas e responsivas com menos código e maior flexibilidade.
- **Room**: Biblioteca para persistência local baseada em SQLite. Inclui suporte a consultas avançadas, migrações de banco de dados e integração com LiveData e Flow para observar mudanças nos dados em tempo real.
- **Retrofit**: Biblioteca poderosa para comunicação com APIs REST. Suporta serialização/deserialização automática de dados e integrações com bibliotecas como Moshi e Gson para manipulação de JSON.
- **Hilt**: Ferramenta de injeção de dependências simplificada que promove modularidade e facilita testes ao gerenciar automaticamente o ciclo de vida das dependências.
- **Coroutines**: Usadas para lidar com operações assíncronas de maneira eficiente e sem bloqueios, garantindo uma experiência de usuário fluida.
- **Flow**: Utilizado para lidar com streams de dados assíncronos e responsivos, essencial para sincronização de dados entre camadas.
- **JUnit 5**: Framework para criação e execução de testes unitários e de integração, permitindo a validação robusta das funcionalidades implementadas.
- **Clean Architecture**: Arquitetura que separa responsabilidades em camadas distintas (Data, Domain, Presentation), promovendo escalabilidade, testabilidade e manutenibilidade.

## Estrutura do Projeto

```plaintext
|-- ScheduleApp
    |-- app
        |-- src
            |-- main
                |-- kotlin
                    |-- di
                    |-- feature_schedule
                        |-- common
                        |-- data
                            |-- data_source
                                |-- cache
                                |-- local
                                |-- remote
                            |-- mapper
                            |-- repository
                        |-- domain
                            |-- model
                            |-- repository
                            |-- use_case
                        |-- presentation
                            |-- schedule_menu
                            |-- view_add_edit_appointment
                            |-- ui/theme
```

### Camadas

#### 1. **Camada Data**

##### a) **Data Sources**

A camada de dados é responsável por interagir com as fontes de dados, que incluem:

- **Cache**: Implementado para armazenar temporariamente os dados frequentemente acessados. A biblioteca de cache usada é integrada ao repositório para validação de dados.
- **Local**: Baseado em Room, possui os seguintes componentes:
  - `AppointmentDao`: Interface que define métodos de consulta ao banco de dados.
  - `AppointmentEntity`: Representação das tabelas do banco de dados local.
  - `AppointmentDatabase`: Configuração principal do banco.
- **Remote**: Utiliza Retrofit para chamadas à API remota, com os seguintes elementos:
  - `AppointmentApi`: Interface que define os endpoints da API.
  - `AppointmentDto`: Objeto de transferência de dados usado para serialização/deserialização.

##### b) **Mapper**

- Os mapeadores convertem dados entre diferentes camadas, garantindo consistência ao sincronizar os modelos do banco, cache e API.

##### c) **Repository**

- `AppointmentRepositoryImpl`: Implementação concreta do repositório, responsável por consolidar dados das fontes locais, cache e remotas. Ele implementa estratégias de fallback, garantindo que dados do cache sejam utilizados em casos de falha da API remota.
  - **Estratégias de fallback**:

    - Quando os dados remotos estão indisponíveis (por exemplo, devido à falta de conexão), o repositório utiliza os dados armazenados localmente no Room ou no cache.
    - Durante a sincronização, o sistema compara os timestamps das entradas no banco de dados local e na API para identificar alterações. Dados desatualizados são descartados ou atualizados automaticamente, dependendo do caso de uso.
    - Uma política de "Cache First" é aplicada para reduzir o número de chamadas desnecessárias à API. Apenas em caso de ausência de dados no cache ou inconsistências detectadas, uma chamada remota é feita.

  - **Benefícios Técnicos**:

    - **Alta Performance**: Minimiza o uso de recursos de rede, reduzindo a latência para o usuário final.
    - **Confiabilidade**: O sistema continua funcional mesmo em ambientes com conectividade instável.
    - **Consistência**: Estratégias de sincronização robustas garantem que as fontes de dados (local, cache, remoto) estejam sempre alinhadas.

#### 2. **Camada Domain**

##### a) **Model**

- Contém modelos de domínio puros, como `Appointment`, que representam dados no nível mais alto de abstração.

##### b) **Repository**

- Define interfaces que garantem contratos claros para acesso a dados. Exemplo: `AppointmentRepository`.

##### c) **Use Cases**

- Implementa lógica de negócios dividida em diferentes casos de uso, cada um representando um aspecto específico da funcionalidade. Esses casos de uso são projetados para encapsular regras de negócios importantes, mantendo o código desacoplado e testável:
  - **Cache**: Lida com operações rápidas e eficientes usando dados armazenados temporariamente. Esse caso de uso otimiza a recuperação de informações frequentemente acessadas e evita solicitações desnecessárias ao banco de dados ou à API.
  - **Local**: Gerencia operações CRUD no banco de dados Room, assegurando persistência confiável e integração com outras camadas. O caso de uso local verifica automaticamente a validade dos dados antes de acessá-los.
  - **Remote**: Centraliza a comunicação com a API externa, implementando lógica para lidar com erros de rede, autenticação e serialização de dados.
  - **Sync**: Realiza sincronização de dados entre o banco local e a API remota. Este caso de uso implementa um algoritmo de detecção e resolução de conflitos, que compara timestamps e prioriza a versão mais recente dos dados. Além disso, utiliza estratégias de reconciliação para resolver diferenças entre fontes de dados, garantindo consistência e integridade.

### Benefícios Técnicos dos Use Cases:

1. **Desacoplamento**: Cada caso de uso é responsável por um aspecto específico da lógica de negócios, tornando o código mais modular e fácil de manter.
2. **Testabilidade**: A separação de preocupações permite testar cada funcionalidade de forma isolada, aumentando a cobertura de testes unitários.
3. **Reusabilidade**: Casos de uso podem ser reutilizados em diferentes partes da aplicação, como ViewModels ou outros componentes de apresentação.
4. **Escalabilidade**: A estrutura modular facilita a adição de novas funcionalidades sem impactar o código existente.

#### 4. **Injeção de Dependências (DI)**

- Configurado via Hilt, o projeto utiliza módulos como `AppModule` para fornecer dependências e gerenciar ciclos de vida com eficiência. Os módulos são organizados para disponibilizar instâncias únicas (singletons) de classes essenciais, como repositórios, data sources e use cases, assegurando consistência em toda a aplicação.

### Benefícios Técnicos da DI com Hilt:

1. **Redução de Boilerplate**: O Hilt automatiza a geração de código para a criação de dependências, simplificando configurações complexas.
2. **Testabilidade Aprimorada**: A DI facilita a substituição de dependências reais por mocks ou fakes durante os testes unitários e instrumentados.
3. **Escalabilidade**: Módulos bem definidos permitem adicionar novas dependências sem impacto no código existente.
4. **Ciclo de Vida Otimizado**: O Hilt gerencia automaticamente as dependências com base no ciclo de vida do componente (como Activities, Fragments e ViewModels), evitando vazamentos de memória.
5. **Consistência Modular**: As dependências são centralizadas, promovendo um design coeso e desacoplado.

### Estratégias Avançadas

- **Sincronização de Dados**: O sistema de sincronização utiliza timestamps e verificações de integridade para garantir consistência entre cache, banco local e API.
- **Performance**: Redução de chamadas desnecessárias à API, priorizando acesso a dados locais/cached quando possível.
- **Clean Code**: Organização modular que facilita manutenção e escalabilidade.

## Testes Unitários
  - **Casos de uso**: São rigorosamente testados com foco em validar as regras de negócios implementadas. Os testes incluem cenários como sincronização de dados, resolução de conflitos e integração entre diferentes fontes de dados.
  - **Camada de dados**: Testes unitários robustos garantem que as consultas no Room retornem os resultados esperados e que os mapeadores convertam os dados corretamente entre as camadas.
  - **Comunicação com API**: Mocking de respostas HTTP permite testar falhas de rede, autenticação e erros específicos da API sem a necessidade de chamadas reais.
  - **Cobertura**: A estrutura modular facilita a cobertura completa de testes em cada camada, assegurando confiança no comportamento esperado do sistema em diferentes cenários.
  ### Benefícios Técnicos dos Testes:
  - **Confiabilidade**: Garante que funcionalidades críticas sejam validadas em diferentes condições, prevenindo falhas em produção.
  - **Identificação de Erros Precoces**: A detecção antecipada de problemas reduz custos e acelera o desenvolvimento.
  - **Manutenibilidade**: Mudanças no código são validadas imediatamente por meio de testes abrangentes.
  - **Foco em Performance**: Cenários de uso que envolvem cache e sincronização são otimizados e monitorados com testes específicos para garantir alta performance.;
  - **Testes de lógica crítica**: Sincronização e resolução de conflitos, verificam o comportamento em cenários de concorrência e conflitos de dados, garantindo que os algoritmos de reconciliação mantenham a integridade das informações.
  - **Camada de dados**: Validação de consultas e operações no Room assegura que as transações no banco de dados sejam executadas corretamente, incluindo a verificação de relações, integridade referencial e consistência dos dados armazenados.
  - **Comunicação com API**: Mock de respostas para verificar comportamento em condições diversas, como erros de rede, respostas com códigos de erro HTTP e formatos inesperados de payload, assegurando que a aplicação lide com falhas de forma resiliente.

## Conclusão

O ScheduleApp é um exemplo prático de Clean Architecture aplicada, com foco em escalabilidade, performance e manutenibilidade. A combinação de tecnologias modernas e estratégias avançadas de sincronização garante uma experiência robusta tanto para desenvolvedores quanto para usuários finais.


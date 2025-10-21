# Launcher de Executáveis

## Visão Geral

O **Launcher de Executáveis** é uma aplicação de desktop para Windows, desenvolvida em Java Swing, que permite aos usuários automatizar a execução de múltiplos programas em uma sequência pré-definida. A ferramenta foi projetada para otimizar fluxos de trabalho, permitindo configurar, salvar e executar listas de tarefas com atrasos e monitoramento de processos.

Com uma interface gráfica intuitiva, o usuário pode adicionar "blocos" de executáveis, definir um tempo de espera (delay) entre cada um, e monitorar o status de execução em tempo real através de um painel de log.

## ✨ Recursos

- **Gerenciamento de Blocos**: Adicione, configure e remova até 6 blocos de executáveis.
- **Execução Sequencial**: Inicie múltiplos programas na ordem definida.
- **Atraso (Delay) Configurável**: Defina um tempo de espera em segundos entre a execução de cada programa.
- **Monitoramento Avançado de Processos**:
    - Acompanha o status de cada executável (`Executando`, `Executado`, `Fechado`).
    - Detecta e monitora processos filhos ou processos que são chamados por outros.
- **Painel de Log**: Exibe em tempo real o status da execução, atrasos e encerramento de processos.
- **Persistência de Dados**:
    - Salve e carregue automaticamente a lista de executáveis configurados.
    - As configurações do usuário (idioma, tema, etc.) são salvas e recuperadas.
- **Configurações Personalizáveis**:
    - **Multi-idioma**: Suporte para Português, Inglês e Espanhol.
    - **Temas**: Alterne entre os modos Claro (Light) e Escuro (Dark).
    - **Timeout de Execução**: Defina um tempo máximo para a execução total da sequência.
- **Controle de Execução**:
    - **Auto-Start**: Inicie a execução de todos os blocos automaticamente ao abrir o programa.
    - **Reiniciar ao Fechar**: Reinicie toda a sequência de execução se um dos processos monitorados for fechado.

## 🚀 Como Usar (Para Usuários)

Para executar a aplicação, você precisa ter o **Java (versão 8 ou superior)** instalado em seu sistema.

1.  Navegue até a pasta `target/`.
2.  Execute o arquivo `launcher-1.0-SNAPSHOT.jar` com um duplo clique ou através do terminal:
    ```bash
    java -jar launcher-1.0-SNAPSHOT.jar
    ```
3.  Na interface principal, adicione os executáveis desejados, configure os atrasos e nomes de processo, e clique em "Iniciar Execução".

## ⚙️ Configuração

A aplicação utiliza os seguintes arquivos para gerenciar dados e configurações:

-   `executables.dat`: Armazena a lista de blocos de executáveis que você configurou.
-   `settings.dat`: Salva as configurações do usuário, como idioma, tema, e estado dos interruptores (Salvar Dados, Auto-Start).
-   `src/main/resources/launcher/config.properties`: Arquivo de configuração para desenvolvedores. Permite definir URLs de ajuda e o timeout padrão.
-   `src/main/resources/launcher/lang_*.properties`: Arquivos de tradução para os idiomas suportados.

## 👨‍💻 Para Desenvolvedores

### Pré-requisitos

-   **JDK (Java Development Kit)** - Versão 8 ou superior.
-   **Apache Maven** - Para gerenciamento de dependências e compilação do projeto.

### Compilando e Executando

1.  Clone ou faça o download deste repositório.
2.  Abra um terminal na pasta raiz do projeto.
3.  Execute o seguinte comando Maven para compilar o projeto e gerar o arquivo `.jar`:
    ```bash
    mvn clean install
    ```
4.  Após a compilação bem-sucedida, o arquivo `launcher-1.0-SNAPSHOT.jar` estará disponível na pasta `target/`.
5.  Execute o JAR como descrito na seção "Como Usar".

## 📂 Estrutura do Projeto

O projeto segue a estrutura padrão de um projeto Maven:

```
Launcher/
├── pom.xml                 # Arquivo de configuração do Maven
├── executables.dat         # Dados dos executáveis salvos
├── settings.dat            # Configurações do usuário salvas
├── src/
│   ├── main/
│   │   ├── java/launcher/  # Código-fonte principal da aplicação
│   │   │   ├── model/      # Classes de dados (ExecutableInfo, Settings)
│   │   │   ├── service/    # Lógica de negócio (ProcessMonitorService, PersistenceService)
│   │   │   └── ui/         # Classes da interface gráfica (MainFrame, SettingsDialog)
│   │   └── resources/      # Recursos da aplicação
│   │       ├── launcher/   # Arquivos de propriedades (config, idiomas) e ícones
│   │       └── META-INF/
│   └── test/               # Código-fonte para testes
└── target/                 # Arquivos gerados pela compilação (incluindo o .jar)
```

## Licença

Este projeto é distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.


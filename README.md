# 🖥️ SuperSistema

Sistema desktop de gestão de usuários desenvolvido em **Java Swing**, com autenticação, CRUD completo e auditoria de ações. Conecta-se a um banco de dados **MySQL** remoto via JDBC.

https://superneuromancer.github.io/index4.html
SuperSistema
Documentação Técnica

// 

https://superneuromancer.github.io/index5.html

SuperSistema · Java
Guia Completo · 9 Classes - linha por linha


---

## 📋 Sumário

- [Visão Geral](#visão-geral)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Estrutura de Arquivos](#estrutura-de-arquivos)
- [Descrição Detalhada de Cada Classe](#descrição-detalhada-de-cada-classe)
  - [Conexao.java](#conexaojava)
  - [AuditoriaUtil.java](#auditoriaUtiljava)
  - [LoginDialog.java](#logindialogJava)
  - [FormPrincipal.java](#formprincipaljava)
  - [FormUsuariosListar.java](#formusuarioslistarjava)
  - [FormUsuariosIncluir.java](#formusuariosincluirjava)
  - [FormUsuarioEditar.java](#formusuarioeditarjava)
  - [FormUsuariosExcluir.java](#formusuariosExcluirjava)
  - [FormLogAuditoria.java](#formlogauditoriajava)
- [Banco de Dados](#banco-de-dados)
- [Log de Auditoria](#log-de-auditoria)
- [Scripts de Build](#scripts-de-build)
- [Pré-requisitos e Instalação](#pré-requisitos-e-instalação)
- [Como Executar](#como-executar)
- [Fluxo de Uso](#fluxo-de-uso)
- [Segurança — Pontos de Atenção](#segurança--pontos-de-atenção)
- [Melhorias Sugeridas](#melhorias-sugeridas)

---

## Visão Geral

O **SuperSistema** é uma aplicação desktop Java com interface gráfica (Swing) que oferece:

- **Login seguro** com seleção de empresa/filial e validação no banco de dados
- **CRUD de usuários**: incluir, editar, excluir e listar usuários cadastrados
- **Log de auditoria** persistente em arquivo texto, registrando login, logout e todas as operações de usuário
- **Visualizador de auditoria** embutido na própria aplicação
- **Menus dinâmicos**: funcionalidades extras só aparecem após o login

---

## Arquitetura do Projeto

```
SuperSistema
│
├── Camada de Apresentação (Swing/GUI)
│   ├── FormPrincipal        ← Janela principal + menu dinâmico
│   ├── LoginDialog          ← Diálogo modal de autenticação
│   ├── FormUsuariosListar   ← Grade de usuários (CRUD hub)
│   ├── FormUsuarioEditar    ← Formulário modal (inclusão/edição)
│   └── FormLogAuditoria     ← Visualizador do log de auditoria
│
├── Camada de Serviço / Utilitários
│   ├── FormUsuariosIncluir  ← Delegador para abertura do formulário de inclusão
│   ├── FormUsuariosExcluir  ← Lógica de exclusão com confirmação
│   └── AuditoriaUtil        ← Gravação de log em arquivo texto
│
└── Camada de Infraestrutura
    └── Conexao              ← Fábrica de conexão JDBC com MySQL
```

O projeto segue uma arquitetura de **duas camadas**: a interface gráfica acessa o banco de dados diretamente via JDBC, sem camada de negócio intermediária (sem DAO/Service formais). A auditoria é tratada como uma preocupação transversal (*cross-cutting concern*) via chamadas estáticas a `AuditoriaUtil`.

---

## Estrutura de Arquivos

```
SS4-main/
├── compile.sh                        # Script de compilação (Linux/macOS)
├── run.sh                            # Script de execução (Linux/macOS)
└── src/
    ├── auditoria.log                 # Arquivo de log gerado em tempo de execução
    └── javasistema/
        ├── AuditoriaUtil.java        # Utilitário de gravação de auditoria
        ├── Conexao.java              # Fábrica de conexão JDBC
        ├── FormLogAuditoria.java     # Janela: visualizador do log
        ├── FormPrincipal.java        # Janela: tela principal (ponto de entrada)
        ├── FormUsuarioEditar.java    # Diálogo: formulário inclusão/edição
        ├── FormUsuariosExcluir.java  # Classe utilitária: exclusão de usuário
        ├── FormUsuariosIncluir.java  # Classe utilitária: inclusão de usuário
        ├── FormUsuariosListar.java   # Janela: grade de usuários
        ├── LoginDialog.java          # Diálogo: tela de login
        └── logo.jpg                  # Imagem de fundo da tela principal
```

---

## Descrição Detalhada de Cada Classe

---

### `Conexao.java`

**Responsabilidade:** Centralizar e fornecer conexões JDBC com o banco de dados MySQL.

**Como funciona:**

- Define as constantes `URL`, `USUARIO` e `SENHA` para o banco remoto.
- O bloco estático `static { ... }` carrega o driver `com.mysql.cj.jdbc.Driver` uma única vez quando a classe é carregada pela JVM.
- O método `getConnection()` retorna uma `java.sql.Connection` ativa, lançando exceção se falhar.
- O método `testarConexao()` é usado pelo `LoginDialog` antes de tentar autenticar — evita travamentos de UI caso o banco esteja offline.

**Parâmetros de conexão:**

| Parâmetro        | Valor                                          |
|------------------|------------------------------------------------|
| Host             | `bd_savir.mysql.dbaas.com.br`                  |
| Porta            | `3306`                                         |
| Banco            | `bd_savir`                                     |
| SSL              | desabilitado (`useSSL=false`)                  |
| Timezone         | `America/Fortaleza`                            |

> ⚠️ As credenciais estão em texto claro no código-fonte. Ver seção [Segurança](#segurança--pontos-de-atenção).

---

### `AuditoriaUtil.java`

**Responsabilidade:** Registrar eventos do sistema em um arquivo de log texto (`auditoria.log`).

**Como funciona:**

- Classe utilitária com método **100% estático** — não precisa ser instanciada.
- O método `registrar(String login, String evento, String detalhe)` monta uma linha no formato:

  ```
  yyyy-MM-dd HH:mm:ss | login | EVENTO | detalhe da ação
  ```

- Abre o arquivo em **modo append** (`FileWriter(arquivo, true)`), escreve a linha e fecha imediatamente.
- Em caso de erro de I/O, imprime no `System.err` mas **não lança exceção** — o sistema continua funcionando mesmo que o log falhe.

**Eventos registrados:**

| Evento            | Quando é registrado                        |
|-------------------|--------------------------------------------|
| `LOGIN`           | Login bem-sucedido                         |
| `LOGOUT`          | Usuário clica em "Logout" e confirma       |
| `INCLUIR_USUARIO` | Novo usuário salvo com sucesso             |
| `EDITAR_USUARIO`  | Usuário existente atualizado               |
| `EXCLUIR_USUARIO` | Usuário removido do banco                  |

---

### `LoginDialog.java`

**Responsabilidade:** Exibir o formulário de login e autenticar o usuário contra o banco de dados.

**Como funciona:**

- É um `JDialog` **modal** — bloqueia a janela principal enquanto está aberta.
- Apresenta três campos: **Empresa/Filial** (combobox), **Usuário** (texto) e **Senha** (campo oculto).
- Ao clicar em "Entrar", valida se todos os campos estão preenchidos antes de consultar o banco.
- Chama `Conexao.testarConexao()` primeiro; se falhar, exibe mensagem de erro e aborta.
- Executa `SELECT nome FROM tb_usuarios WHERE login = ? AND senha = ? LIMIT 1` via `PreparedStatement`.
- Se encontrar o usuário, seta `autenticado = true`, guarda o nome completo (`usuario`), o login (`loginDigitado`) e a empresa selecionada.
- Empresas disponíveis no combobox (lista estática):
  - Matriz - São Paulo
  - Filial - Rio de Janeiro
  - Filial - Belo Horizonte
  - Filial - Curitiba
  - Filial - Salvador

**Getters públicos expostos ao `FormPrincipal`:**

| Método                  | Retorna                                    |
|-------------------------|--------------------------------------------|
| `isAutenticado()`       | `true` se login foi aceito                 |
| `getUsuario()`          | Nome completo do usuário autenticado       |
| `getLoginAutenticado()` | Login digitado (username)                  |
| `getEmpresa()`          | Empresa/filial selecionada no combobox     |

---

### `FormPrincipal.java`

**Responsabilidade:** Janela principal do sistema — ponto de entrada da aplicação.

**Como funciona:**

- Estende `JFrame` e contém o método `main`, que inicializa a GUI via `SwingUtilities.invokeLater`.
- Exibe a imagem `logo.jpg` como fundo, desenhada via override de `paintComponent`.
- Abre **maximizado** (`JFrame.MAXIMIZED_BOTH`).
- Possui uma barra de menus com comportamento **dinâmico**:

  | Estado         | Menus visíveis                          |
  |----------------|-----------------------------------------|
  | Deslogado      | `Sistema → Logar`, `Sistema → Fechar`   |
  | Logado         | + `Usuários → Gestão de Usuários`       |
  |                | + `Segurança → Log de Auditoria`        |

- O método `aplicarEstadoAutenticacao()` adiciona ou remove os menus `Usuários` e `Segurança` conforme o estado de login, chamando `menuBar.revalidate()` e `menuBar.repaint()` para atualizar a UI.
- O método `realizarLogin()` instancia `LoginDialog`, aguarda o fechamento e, se autenticado, guarda os dados do usuário e registra o evento `LOGIN` na auditoria.
- Ao fazer logout, exibe confirmação, registra o evento `LOGOUT` e limpa todas as variáveis de sessão.

---

### `FormUsuariosListar.java`

**Responsabilidade:** Tela de listagem (grid) de todos os usuários cadastrados — hub central do CRUD.

**Como funciona:**

- Estende `JFrame`, abre em 1000×600 px, centralizada.
- Exibe uma `JTable` com as colunas: **ID**, **Nome**, **Login**, **E-mail**.
- A tabela é **somente leitura** (override de `isCellEditable` retorna `false`).
- Barra de ferramentas com 5 botões: `INCLUIR`, `EDITAR`, `EXCLUIR`, `ATUALIZAR`, `SAIR`.
- **Duplo clique** em uma linha também abre o formulário de edição.
- O método `carregarUsuarios()` executa `SELECT usuario_id, nome, login, email FROM tb_usuarios ORDER BY nome` e repopula a tabela; é `public` para que as classes de inclusão/exclusão possam disparar o recarregamento.
- Delega as ações para classes especializadas:
  - Inclusão → `FormUsuariosIncluir.abrir(this, loginOperador)`
  - Exclusão → `FormUsuariosExcluir.excluir(this, loginOperador)`
  - Edição → método privado `abrirFormEdicao()` que instancia `FormUsuarioEditar`

---

### `FormUsuariosIncluir.java`

**Responsabilidade:** Intermediário que abre o formulário de inclusão de novo usuário.

**Como funciona:**

- Classe utilitária com método **estático** `abrir(FormUsuariosListar, String loginOperador)`.
- Instancia `FormUsuarioEditar` passando `id = 0` (sinaliza modo inclusão).
- Seta o login do operador para auditoria via `setLoginOperador`.
- Após fechar, verifica `isSalvo()` e, se verdadeiro, chama `formListar.carregarUsuarios()` para atualizar a grade.

---

### `FormUsuarioEditar.java`

**Responsabilidade:** Formulário modal (diálogo) para **incluir** ou **editar** um usuário.

**Como funciona:**

- Estende `JDialog` com `modal = true`.
- O **modo** é determinado pelo parâmetro `usuarioId`:
  - `usuarioId == 0` → modo **inclusão** (título: "Incluir Usuário", executa `INSERT`)
  - `usuarioId > 0`  → modo **edição** (título: "Editar Usuário", executa `UPDATE`)
- Layout: `GridBagLayout` com 4 campos (Nome, Login, Senha, E-mail) e painel de botões (SALVAR / CANCELAR).
- No modo edição, `carregarDados()` busca o registro no banco e preenche os campos antes de exibir.
- O método `salvar()`:
  1. Lê e valida os campos (nome, login e senha são obrigatórios).
  2. Em caso de inclusão executa `INSERT INTO tb_usuarios (nome, login, senha, email) VALUES (?, ?, ?, ?)`.
  3. Em caso de edição executa `UPDATE tb_usuarios SET nome=?, login=?, senha=?, email=? WHERE usuario_id=?`.
  4. Detecta `Duplicate entry` para login duplicado e exibe mensagem específica.
  5. Registra auditoria (`INCLUIR_USUARIO` ou `EDITAR_USUARIO`).
  6. Seta `salvo = true` e chama `dispose()`.
- O getter `isSalvo()` permite que o chamador saiba se houve persistência.

---

### `FormUsuariosExcluir.java`

**Responsabilidade:** Executar a exclusão do usuário selecionado na grade.

**Como funciona:**

- Classe utilitária com método **estático** `excluir(FormUsuariosListar, String loginOperador)`.
- Verifica se há linha selecionada na tabela; se não houver, exibe aviso e retorna.
- Obtém `usuarioId` e `nomeUsuario` da linha selecionada.
- Exibe `JOptionPane.showConfirmDialog` de confirmação (com aviso de ação irreversível).
- Se confirmado, executa `DELETE FROM tb_usuarios WHERE usuario_id = ?`.
- Registra `EXCLUIR_USUARIO` na auditoria.
- Chama `formListar.carregarUsuarios()` para atualizar a grade.

---

### `FormLogAuditoria.java`

**Responsabilidade:** Exibir os registros do arquivo `auditoria.log` em uma tabela.

**Como funciona:**

- Estende `JFrame`, abre em 1000×600 px.
- Lê o arquivo `auditoria.log` linha a linha com `BufferedReader`.
- Cada linha é dividida pelo separador `|` em 4 partes: `Data/Hora`, `Login`, `Evento`, `Detalhe`.
- Somente linhas com exatamente 4 campos são adicionadas à tabela.
- A coluna `Data/Hora` usa fonte monoespaçada para melhor legibilidade.
- Após carregar, rola automaticamente para a **última linha** (registro mais recente).
- Se o arquivo não existir ainda (`FileNotFoundException`), exibe mensagem informativa amigável.
- Barra de ferramentas com botões `ATUALIZAR` (relê o arquivo) e `SAIR`.

---

## Banco de Dados

### Tabela `tb_usuarios`

```sql
CREATE TABLE tb_usuarios (
    usuario_id INT          NOT NULL AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    login      VARCHAR(50)  NOT NULL UNIQUE,
    senha      VARCHAR(100) NOT NULL,
    email      VARCHAR(150)     NULL,
    PRIMARY KEY (usuario_id)
);
```

| Coluna       | Tipo         | Obrigatório | Observação                              |
|--------------|--------------|-------------|-----------------------------------------|
| `usuario_id` | INT          | Sim         | Chave primária, auto incremento         |
| `nome`       | VARCHAR(100) | Sim         | Nome completo do usuário                |
| `login`      | VARCHAR(50)  | Sim         | Único — constraint `UNIQUE`             |
| `senha`      | VARCHAR(100) | Sim         | Armazenada em texto plano (ver atenção) |
| `email`      | VARCHAR(150) | Não         | Pode ser `NULL`                         |

---

## Log de Auditoria

O arquivo `auditoria.log` é criado automaticamente no **diretório de trabalho** (onde a aplicação é executada) na primeira ação registrada.

**Formato de cada linha:**

```
2026-05-14 00:53:54 | savir | INCLUIR_USUARIO | Usuário incluído: login=hackerman, nome=hackerman
```

**Exemplo real do arquivo:**

```
2026-05-07 09:01:50 | savir | LOGIN          | Acesso ao sistema. Usuário: Hiran Savir Junior. Empresa: Matriz - São Paulo
2026-05-07 09:27:06 | savir | LOGOUT         | Sessão encerrada. Usuário: Hiran Savir Junior. Empresa: Matriz - São Paulo
2026-05-14 00:53:54 | savir | INCLUIR_USUARIO| Usuário incluído: login=hackerman, nome=hackerman
2026-05-14 00:58:30 | savir | EDITAR_USUARIO | Usuário editado: id=137, login=ab, nome=ab
2026-05-14 00:58:46 | savir | EXCLUIR_USUARIO| Usuário excluído: id=137, nome=ab
```

> O log é **append-only** — nunca sobrescreve registros anteriores. O sistema nunca apaga o log automaticamente.

---

## Scripts de Build

### `compile.sh`

Compila todos os arquivos `.java` do pacote `javasistema` usando o driver JDBC do MySQL.

```bash
#!/bin/bash
# Verifica estrutura de diretórios
# Localiza o driver: /usr/share/java/mysql-connector-j-9.7.0.jar
# Remove .class antigos
# Compila: javac -cp ".:<driver>" javasistema/*.java
```

**Execução:**
```bash
chmod +x compile.sh
./compile.sh
```

### `run.sh`

Executa a aplicação. Se os `.class` não existirem, chama `compile.sh` automaticamente.

```bash
#!/bin/bash
# Verifica .class compilados (compila se necessário)
# Executa: java -cp ".:<driver>" javasistema.FormPrincipal
```

**Execução:**
```bash
./run.sh
```

---

## Pré-requisitos e Instalação

### Requisitos

| Componente       | Versão mínima | Observação                                        |
|------------------|---------------|---------------------------------------------------|
| Java (JDK/JRE)   | 8+            | `java -version` para verificar                    |
| MySQL Connector/J| 9.7.0         | Instalado em `/usr/share/java/` no Linux          |
| Conexão Internet | —             | Necessária para acessar o banco remoto            |

### Instalação do driver MySQL (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install mysql-connector-j
```

### Verificar instalação

```bash
ls /usr/share/java/mysql-connector-j*.jar
```

---

## Como Executar

```bash
# 1. Clonar / descompactar o projeto
unzip SS4-main.zip
cd SS4-main

# 2. Compilar
./compile.sh

# 3. Executar
./run.sh
```

Ou manualmente:

```bash
cd src
javac -cp ".:/usr/share/java/mysql-connector-j-9.7.0.jar" javasistema/*.java
java  -cp ".:/usr/share/java/mysql-connector-j-9.7.0.jar" javasistema.FormPrincipal
```

---

## Fluxo de Uso

```
1. Iniciar → FormPrincipal abre maximizado com logo de fundo
              Menu: [Sistema → Logar] [Sistema → Fechar]

2. Logar   → LoginDialog abre (modal)
              Selecionar empresa → digitar usuário → digitar senha → Entrar
              Autenticação contra tb_usuarios no MySQL
              ✓ Sucesso: menu expande com [Usuários] e [Segurança]
              ✗ Falha: mensagem de erro, campos limpos

3. Gestão  → Usuários → Gestão de Usuários → FormUsuariosListar
              [INCLUIR] → formulário em branco → salvar → INSERT no banco
              [EDITAR]  → formulário pré-preenchido → salvar → UPDATE no banco
              [EXCLUIR] → confirmação → DELETE no banco
              [ATUALIZAR] → recarrega lista do banco

4. Auditoria → Segurança → Log de Auditoria → FormLogAuditoria
               Visualiza auditoria.log em tabela
               [ATUALIZAR] relê o arquivo

5. Logout  → Sistema → Logout → confirmação → sessão encerrada
              Menu volta ao estado inicial (sem [Usuários] e [Segurança])
```

---

## Segurança — Pontos de Atenção

Os itens abaixo são **limitações conhecidas** do projeto atual, importantes para quem for evoluí-lo:

| # | Problema                           | Risco                                      | Solução Recomendada                              |
|---|------------------------------------|--------------------------------------------|--------------------------------------------------|
| 1 | Senha do BD em texto claro no código | Qualquer pessoa com acesso ao `.java` ou `.class` vê a senha | Usar variáveis de ambiente ou arquivo `.properties` fora do repositório |
| 2 | Senhas dos usuários em texto plano no banco | Vazamento do banco expõe todas as senhas | Aplicar hash com BCrypt ou SHA-256+salt antes de persistir |
| 3 | Sem limite de tentativas de login   | Suscetível a força bruta                  | Bloquear conta após N tentativas falhas          |
| 4 | Sem controle de permissões por perfil | Qualquer usuário logado vê todos os menus | Adicionar coluna `perfil` na `tb_usuarios` (admin/operador) |
| 5 | Log de auditoria editável manualmente | O arquivo pode ser alterado ou apagado   | Gravar log em tabela no banco ou usar sistema de log assinado |
| 6 | Credenciais do BD no repositório   | Exposição em repositórios públicos        | Adicionar `Conexao.java` ou `.env` ao `.gitignore` |

---

## Melhorias Sugeridas

- **Hash de senha:** implementar `BCrypt` ou `MessageDigest` (SHA-256) antes de salvar e na autenticação.
- **Arquivo de configuração externo:** mover URL/usuário/senha para `config.properties` lido em runtime.
- **Padrão DAO:** criar `UsuarioDAO` para desacoplar SQL da UI e facilitar testes.
- **Controle de perfil:** adicionar coluna `perfil ENUM('admin','operador')` e exibir menus conforme perfil.
- **Paginação:** implementar paginação na listagem para grandes volumes de usuários.
- **Busca/filtro:** adicionar campo de busca na tela de listagem.
- **Log em banco:** mover auditoria de arquivo para tabela `tb_auditoria` para persistência e consulta mais robustas.
- **Tratamento de recursos:** usar `try-with-resources` consistentemente em todos os pontos de acesso ao banco.
- **Internacionalização:** extrair strings para `ResourceBundle` para suporte a múltiplos idiomas.

---

## Informações do Projeto

| Item          | Detalhe                   |
|---------------|---------------------------|
| Linguagem     | Java 8+                   |
| Interface     | Java Swing (javax.swing)  |
| Banco         | MySQL (JDBC)              |
| Driver JDBC   | mysql-connector-j 9.7.0   |
| Plataforma    | Linux (scripts `.sh`)     |
| Pacote Java   | `javasistema`             |

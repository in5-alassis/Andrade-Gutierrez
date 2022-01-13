<div align="center">
    <img src="https://infive.net.br/assets/img/logo-new-nobkg.png" width=300 />
    <h1>Template para definir workspace para projetos de nossos clientes</h1>
    <h5>author: @rmmarquini</h4>
</div>

## 💻 Sobre este projeto

![](https://img.shields.io/badge/java-71.6%25-b07219?style=flat-square)
![](https://img.shields.io/badge/javascript-14.8%25-fcc000?style=flat-square)
![](https://img.shields.io/badge/HTML-11.4%25-e34c26?style=flat-square)
![](https://img.shields.io/badge/tsql-1.9%25-8ba3ef?style=flat-square)
![](https://img.shields.io/badge/shell-0.2%25-c1f12e?style=flat-square)

<p>Projeto template para inicialização de ambiente de desenvolvimento em um novo cliente da Infive.</p>

### Conteúdos
- [Tecnologias](#-tecnologias)
- [Como executar o projeto](#-como-executar-o-projeto)
	- [Pré-requisitos](#pré-requisitos)
	- [Executando em seu computador](#executando-em-seu-computador)
	- [Agora prepare o projeto no Gitlab referente ao cliente](#agora-prepare-o-projeto-no-gitlab-referente-ao-cliente)
	- [Agora prepare o readme do projeto do cliente para os desenvolvedores atuarem](#agora-prepare-o-readme-do-projeto-do-cliente-para-os-desenvolvedores-atuarem)
- [Boas práticas](#-boas-práticas)
	- [Saiba como estruturar sua workspace](#1-saiba-como-estruturar-sua-workspace)
	- [Descritivos e comentários em código](#2-descritivos-e-comentários-em-código)
	- [Estrutura do projeto](#3-estrutura-de-projeto)
	- [Codificação](#4-codificação)
	- [O que já existe por default nesse pacote e podem ser entregues como serviços aos clientes logo no início](#5-o-que-já-existe-por-default-nesse-pacote-e-podem-ser-entregues-como-serviços-aos-clientes-logo-no-início)
	- [Arquivos e documentos pertinentes ao cliente](#6-arquivos-e-documentos-pertinentes-ao-cliente)
- [Testes](#-testes)
- [Licença](#-licença)

## 🛠 Tecnologias

<p>As seguintes ferramentas foram usadas na construção do projeto:</p>

- Java
- Lecom API
- Javascript

## 🚀 Como executar o projeto

### Pré-requisitos

- Java 8;
- Git;
- Ter criado ou existir um subgrupo no repositório infive-lavoro com o nome do cliente;
- Ter um repositório para armazenar a workspace dentro do subgrupo do cliente;
- Ter um diretório em seu computador para criar/clonar o projeto <em>(veja mais na sessão de boas práticas)</em>.

### Executando em seu computador

~~~bash
# Clone este repositório no diretório de sua workspace
$ git clone https://gitlab.com/infive-lavoro/interno/templateProjetoCliente

# Renomeie o diretório do projeto clonado definindo-o com o nome do cliente
$ mv templateProjetoCliente workspace-nome-cliente

# Acesse o diretório do projeto
$ cd workspace-nome-cliente
~~~

### Agora prepare o projeto no Gitlab referente ao cliente
1. Acesse o Gitlab [Infive-Lavoro](https://gitlab.com/infive-lavoro), navegue até o subgrupo do cliente;
2. Criei um novo projeto com a mesma identificação padrão **workspace-nome-cliente**;
3. Copie a URL git do projeto;

~~~bash
# Agora edite o remote indicando o subgrupo e projeto do cliente criado no repositório infive-lavoro
$ git remote set-url origin https://gitlab.com/infive-lavoro/nome-subgrupo-cliente/workspace-nome-cliente.git

# Verifique se o remote foi alterado com sucesso
$ git remote -v
~~~

### Agora prepare o README do projeto do cliente para os desenvolvedores atuarem

1. Edite o descritivo do projeto;
2. Se houverem mais tecnologias envolvidas adicione-as <em>(e isto pode ser feito no decorrer do desenvolvimento)</em>;
3. Prepare o commit inicial do novo projeto: 

~~~bash
# Veja se os arquivos que serão adicionadas na work tree estão corretos e se não há nenhum arquivo desnecessário
$ git status

# Se houver algum arquivo ou diretório desnecessário, adicione-o no .gitignore

# Então, adicione os arquivos na work tree
$ git add .

# Defina uma mensagem de commit
$ git commit -m "Preparando o repositório do cliente NOME_CLIENTE"

# Crie os branches de qas e prd
$ git checkout -b qas
$ git checkout -b prd

# Faça o push para a origin --all
$ git push -u origin --all

# Mantenha-se na branch in5-dev
$ git checkout in5-dev
~~~

4. Agora, defina nas configurações do projeto os membros e o branch padrão sendo o in5-dev, permitindo total acesso nesse branch aos membros adicionados ao projeto.

5. Por fim, remova estes itens numéricos e o subtítulo "Agora prepare o ... ", deixando apenas o "Executando em seu computador".

###### 🔼 Remova o "how to" acima quando for disponibilizar o projeto cliente aos desenvolvedores, mantendo apenas o bash abaixo. 🔼

~~~bash
# Clone este repositório no diretório de sua workspace
$ git clone https://gitlab.com/infive-lavoro/nome-subgrupo-cliente/nomeProjetoCliente

# Mude para o branch in5-dev, pois no master ninguém brinca 😆
$ git checkout in5-dev

# Verifique se está no branch correto para desenvolvimento
$ git branch
~~~

## 😎 Boas práticas

### 1. Saiba como estruturar sua workspace:
> [Minha sugestão de workspace](https://gitlab.com/infive-lavoro/interno/workspace-template)

### 2. Descritivos e comentários em código
- Seja objetivo e sucinto nas informações registradas em descritivos e comentários;
- Matenha uma boa escrita no seu idioma ou em inglês - <em>é um bom momento para treinar o idioma oficial da TI</em>;

### 3. Estrutura de projeto
- De forma alguma faça commit de arquivos estruturais da sua IDE. Por exemplo:
	- No Eclipse temos: .classpath, .project e, algumas vezes, o diretórios /.settings;
	- No VS Code temos: .vscode;
- Também remova pastas indesejadas de dependências ou classes `bin`;
- Um bom e simples exemplo de `.gitignore` segue:
```bash
# Eclipse
/.settings/
.classpath
.project

# VS Code
.vscode

# Projeto Java
/bin/
```
- Para as bibliotecas utilizadas para desenvolvimento Java, compartilhamos os pacotes compilados (`.jar`) no diretório lib dentro de WEB-INF, assim facilita nosso processo de desenvolvimento.

### 4. Codificação
- Nomeie seus objetos (classes, scripts, pacotes) seguindo o padrão de Clean Code, nunca use `underlines` na nomeação deles, por exemplo; 
- Utiliza a tag <strong>TODO</strong> quando precisar voltar em uma sessão de código mais tarde;
- Pratique programação orientada à objetos (POO) e faça uso da cultura de reaproveitamento de objetos, classes, métodos, etc;
- Estude Git e domine-o;
- Sugira melhorias ou novas implementações.

> Lembre-se que somos um time e um ambiente colaborativo promove ganho para todos! 💪

### 5. O que já existe por default nesse pacote e podem ser entregues como serviços aos clientes logo no início?
- Robô para aprovar etapas genéricas: `RbExecutaEtapasGenerico`;
- Robô para cancelar processos ociosos e que permite ignorar modelos BPM que não devem seguir tal regra;
- Template para integrações e robôs;
- Classes para abertura de processos, execução de etapas, envio de notificações de e-mail;
- Classes com métodos genéricos para diversas funcionalidades e ações genéricas de banco de dados no ambiente Lecom;
- Classe para lidar com exceptions;
- Arquivo `.properties` que define variáveis de ambiente global para ambiente aceite e produção - `ParametrosGerais.properties`;
- Arquivos `.properties` templates para robôs e integrações;
- Templates de scripts JS para modelos e estilização de formulários de maneira genérica;
- Script JS com diversas funcionalidades para utilização no desenvolvimento front-end - `funcoesPersonalizadas.js`.

### 6. Arquivos e documentos pertinentes ao cliente
No diretório `assets` podemos incluir documentos, planilhas, arquivos .atos, procedimentos de banco de dados, dentre outros arquivos que desejamos armazenar e que sejam pertinentes ao projeto do cliente.

> Façam bom uso 😄

## 🚧 Testes
> em construção

## 📝 Licença

Este projeto esta sobe a licença GNU GPLv3.

Feito com ❤️ e ☕ por Rafael Marquini e Heitor Oliveira.
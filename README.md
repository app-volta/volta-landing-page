# VOLTA

Plataforma que conecta empresas e cooperativas de reciclagem para facilitar o gerenciamento e o direcionamento de resíduos plásticos. O VOLTA organiza a relação entre quem gera o resíduo e quem pode dar a ele um destino adequado.

```
EMPRESA  ->  VOLTA  ->  COOPERATIVA
```

Projeto Interdisciplinar do Instituto J&F Germinare Tech. Objetivo de Desenvolvimento Sustentável principal: ODS 12 (Consumo e Produção Responsáveis). O foco atual é o plástico.

Este repositório contém a parte web da disciplina Desenvolvimento 1: a landing page e as telas web do CRUD.

## Sumário

- [Problema e solução](#problema-e-solução)
- [Desenvolvimento 1](#desenvolvimento-1)
- [Páginas do projeto](#páginas-do-projeto)
- [Tecnologias](#tecnologias)
- [Estrutura de pastas](#estrutura-de-pastas)
- [Como executar](#como-executar)
- [Publicação](#publicação)
- [Identidade visual](#identidade-visual)
- [Equipe](#equipe)

## Problema e solução

**Problema.** Empresas têm dificuldade de saber o que geram, qual o destino adequado, quais cooperativas podem receber o material e como acompanhar as coletas. Cooperativas têm dificuldade de encontrar empresas e de manter uma comunicação eficiente.

**Solução.** Uma plataforma em que:

- a empresa se cadastra, procura cooperativas no mapa, analisa, entra em contato, registra os resíduos (foto, descrição e localização), agenda a coleta e aguarda a data;
- a cooperativa se cadastra, informa o tipo de resíduo, espera uma empresa entrar em contato, conversa e confirma o agendamento.

O acesso é por empresa ou cooperativa, não por usuário individual.

## Desenvolvimento 1

Requisitos da disciplina e onde cada um está neste repositório.

| Requisito | Descrição | Onde está |
| --- | --- | --- |
| M1 | Landing page com informações do projeto | [src/index.html](src/index.html), [src/css/landing.css](src/css/landing.css) e [src/js/landing.js](src/js/landing.js) |
| M2 | Telas web do CRUD: cadastro, consulta/listagem, edição e exclusão | Pastas `empresa`, `cooperativa`, `area`, `ocorrencia`, `coleta` e `usuario` em [src](src), cada uma com `lista.html` e `formulario.html` |
| M3 | Entrega pelo GitHub e publicação em hospedagem estática | Este repositório. Link do site publicado: a ser adicionado após a publicação no GitHub Pages |
| E1 | Responsivo e fiel ao padrão visual e ao protótipo do Figma | Media queries em [src/css/landing.css](src/css/landing.css) e [src/css/app.css](src/css/app.css); paleta e fonte em [src/css/base.css](src/css/base.css) |

**Observação sobre o CRUD.** As telas são feitas apenas com HTML e CSS. Os dados exibidos nas listas são exemplos escritos no próprio HTML. Os formulários enviam os dados por `POST` para servlets (por exemplo, `../empresa`), que fazem parte do back-end Java e não existem no site estático. A integração é feita convertendo as páginas para JSP.

## Páginas do projeto

| Página | Arquivo |
| --- | --- |
| Landing page | [src/index.html](src/index.html) |
| Login | [src/login.html](src/login.html) |
| Cadastro | [src/cadastrar.html](src/cadastrar.html) |
| Esqueci minha senha | [src/esqueci-senha.html](src/esqueci-senha.html) |
| Empresas | [src/empresa](src/empresa) |
| Cooperativas | [src/cooperativa](src/cooperativa) |
| Áreas | [src/area](src/area) |
| Ocorrências | [src/ocorrencia](src/ocorrencia) |
| Coletas | [src/coleta](src/coleta) |
| Usuários | [src/usuario](src/usuario) |

**Seções da landing page:** Início, O que é o VOLTA, Como funciona (empresas e cooperativas), Dashboards, Nosso objetivo, Voltinha (mascote), Nossa equipe e rodapé.

## Tecnologias

- HTML5, CSS3 e JavaScript puros, sem framework e sem etapa de build.
- Fonte Belanosima (Google Fonts).
- Font Awesome e Material Symbols para ícones.
- Protótipo de design no Figma.
- Git e GitHub para versionamento.

## Estrutura de pastas

```
volta-landing-page/
├── LICENSE
├── README.md
└── src/
    ├── index.html
    ├── login.html
    ├── cadastrar.html
    ├── esqueci-senha.html
    ├── css/
    │   ├── base.css
    │   ├── landing.css
    │   ├── login-cadastrar.css
    │   └── app.css
    ├── js/
    │   ├── landing.js
    │   └── cadastrar.js
    ├── assets/
    │   └── img/
    └── empresa/  cooperativa/  area/  ocorrencia/  coleta/  usuario/
        ├── lista.html
        └── formulario.html
```

## Como executar

Não é necessário instalar nada.

1. Clone o repositório:

   ```
   git clone https://github.com/app-volta/volta-landing-page.git
   ```

2. Abra `src/index.html` no navegador.

Para evitar problemas de caminhos relativos, abra os arquivos sempre a partir da pasta `src`. Se preferir um servidor local, na pasta `src` rode, por exemplo, `python -m http.server 8000` e acesse `http://localhost:8000`.

## Publicação

O site é publicado com GitHub Pages a partir da pasta `src`.

Link do site publicado: a ser adicionado.

## Identidade visual

- Transmite sustentabilidade, conexão, organização, confiança e profissionalismo.
- Somente tons de verde, definidos como variáveis em [src/css/base.css](src/css/base.css).
- Fonte: Belanosima.
- Logo: letra V com símbolo circular de retorno e reciclagem.
- Mascote: Voltinha, uma seta verde com carinha.

## Equipe

Integrantes do 1º ano:

| Integrante | Função |
| --- | --- |
| Lucca | Front-end |
| Gustavo Azenha | UX |
| Gustavo Melo | Back-end |
| Miguel Lapa | Dados |

O 2º ano é responsável pelo aplicativo.

## Licença

Consulte o arquivo [LICENSE](LICENSE).

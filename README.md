# Limbus Gacha API

API REST em **Spring Boot** que simula a **extração (gacha)** de *Limbus Company*. É a API consumida pelo app mobile: cuida do login dos jogadores, da moeda (Lunacy), do pity por banner, do inventário e do sorteio.

O catálogo (Sinners, Identities, E.G.O. e Banners) é cadastrado pela **Limbus Admin API**. Esta API só **lê** o catálogo do mesmo banco e **escreve** apenas os dados dos jogadores.

---

## Arquitetura do projeto

```
                       ┌──────────────── MySQL ────────────────┐
                       │  catálogo: sinner · identity · ego ·   │
                       │            banner                      │
                       │  jogadores: player · player_identity · │
                       │             player_ego · player_banner │
                       └──────▲───────────────────────▲─────────┘
                              │                       │
  limbus-admin-web ──► limbus-admin-api        limbus-gacha-api ◄── app mobile
  (Angular, painel)    (:8080, catálogo)       (este projeto, :8081)
```

| Projeto | Papel |
|---|---|
| limbus-admin-api | CRUD do catálogo: Identities, E.G.O. e Banners |
| limbus-admin-web | Painel Angular para cadastrar o catálogo com upload de mídia |
| **limbus-gacha-api** | Login, Lunacy, pity, inventário e sorteio |
| app mobile | Interface do jogador (Expo / React Native) |

---

## Tecnologias

- Java 21
- Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- MySQL 8
- Lombok
- springdoc-openapi (Swagger UI)

---

## Modelo de dados

### Tabelas próprias desta API

```
Player 1 ──── N PlayerBanner N ──── 1 Banner
Player N ──── N Identity   (tabela player_identity: inventário)
Player N ──── N Ego        (tabela player_ego: inventário)
```

| Entidade | Campos principais |
|---|---|
| **Player** | `id`, `username` (único), `currency` (Lunacy, começa em 13000), `ownedIdentities`, `ownedEgos` |
| **PlayerBanner** | `id`, `player`, `banner`, `pity` (único por player + banner) |

### Tabelas do catálogo (somente leitura)

`Sinner`, `Identity`, `Ego` e `Banner` são as mesmas entidades da admin-api. As classes precisam ficar **idênticas** nos dois projetos, porque os dois usam `ddl-auto=update` no mesmo banco.

---

## Regras do gacha

### Custo

| Extração | Lunacy |
|---|---|
| 1 giro | 130 |
| 10 giros | 1300 |

Todo jogador novo começa com **13000 Lunacy** (100 giros). O endpoint `POST /players/{id}/currency` adiciona 1300 por chamada, para testes.

### Taxas

Baseadas na [Regular Extraction](https://limbuscompany.wiki.gg/wiki/Extraction) do jogo:

| Resultado | Normal | Jogador já tem todos os E.G.O. do banner |
|---|---|---|
| E.G.O. | 1,3% | – |
| Identity 000 | 2,9% | 3% |
| Identity 00 | 12,8% | 13% |
| Identity 0 | 83% | 84% |

Um banner sem E.G.O. usa automaticamente a segunda tabela.

O sorteio acontece em duas etapas: primeiro a **raridade**, depois um item aleatório **dentro daquela raridade**, entre os itens do banner.

### Pity

- Cada jogador tem um contador de pity **por banner**, que continua entre giros de 1x e de 10x.
- A cada giro o contador sobe 1. Quando chega a **10**, aquele giro é **garantido 00 ou superior** e o contador volta a 0.
- No giro garantido, a faixa de 83% do 0 vira 00, o que dá: E.G.O. 1,3% · 000 2,9% · 00 95,8%.
- Num 10x, o giro garantido é aquele em que o contador chega a 10. Não é necessariamente o último do pacote.

Exemplo: com pity 3, um 10x tem o **7º** giro garantido e termina com pity 3.

### Transação

Cada extração acontece numa única transação: valida a Lunacy, desconta, sorteia, atualiza o pity e adiciona os itens ao inventário. Se qualquer passo falhar, nada é salvo.

O resultado é decidido no servidor **antes** da animação do app. Fechar o app no meio da animação não perde nada.

---

## Como rodar

### Pré-requisitos

- JDK 21
- MySQL 8 rodando (local ou em nuvem, como o Aiven)
- O catálogo cadastrado pela admin-api, com pelo menos um banner ativo

### Variáveis de ambiente

A conexão com o banco vem de variáveis de ambiente (veja `.env.exemple`). Use **o mesmo banco** da admin-api.

| Variável | Exemplo local | Exemplo Aiven |
|---|---|---|
| `DB_HOST` | `localhost` | `xxx.aivencloud.com` |
| `DB_PORT` | `3306` | `12345` |
| `DB_SSL` | `DISABLED` | `REQUIRED` |
| `DB_USER` | `root` | `avnadmin` |
| `DB_PASSWORD` | `root` | *(senha do serviço)* |

No IntelliJ, configure em *Run → Edit Configurations → Environment variables*.

### Executando

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A API sobe em **http://localhost:8081**. A porta é diferente da admin-api (8080) para as duas rodarem ao mesmo tempo.

### Acessando pelo celular

No celular, `localhost` é o próprio aparelho. Para o app mobile acessar a API:

1. Use o IP do computador na rede, por exemplo `http://192.168.0.15:8081`. No Windows, ele aparece com `ipconfig`.
2. Computador e celular precisam estar no mesmo Wi-Fi.
3. Se não conectar, libere a porta 8081 no firewall do Windows.

---

## Documentação interativa (Swagger)

| URL | Conteúdo |
|---|---|
| http://localhost:8081/docs.html | Swagger com o tema Limbus |
| http://localhost:8081/swagger-ui.html | Swagger com o visual padrão |
| http://localhost:8081/v3/api-docs | Especificação OpenAPI em JSON |

---

## Endpoints

### Jogador

| Método | Rota | Descrição |
|---|---|---|
| POST | `/players/login` | Entra na conta ou cria uma nova com o username |
| GET | `/players/{id}` | Perfil, Lunacy e inventário |
| GET | `/players/{playerId}/banners/{bannerId}/pity` | Pity atual naquele banner |
| POST | `/players/{id}/currency` | Adiciona 1300 Lunacy (para testes) |

### Banners

| Método | Rota | Descrição |
|---|---|---|
| GET | `/banners` | Lista os banners **ativos** |
| GET | `/banners/{id}` | Detalhe do banner, com identities e E.G.O. |

### Extração

| Método | Rota | Descrição |
|---|---|---|
| POST | `/players/{playerId}/banners/{bannerId}/extractions?amount=1` | 1 giro |
| POST | `/players/{playerId}/banners/{bannerId}/extractions?amount=10` | 10 giros |

---

## Exemplos

### Login

```http
POST /players/login
Content-Type: application/json

{ "username": "Kaio" }
```

```json
{
  "id": 1,
  "username": "kaio",
  "currency": 13000,
  "identities": [],
  "egos": []
}
```

O username é salvo sem espaços nas pontas e em minúsculas, então `"Kaio"`, `" kaio"` e `"KAIO"` entram na mesma conta. Não há senha: o projeto é uma demonstração.

### Pity

```http
GET /players/1/banners/1/pity
```

```json
{ "bannerId": 1, "pity": 3, "pullsUntilGuarantee": 7 }
```

### Extração 10x

```http
POST /players/1/banners/1/extractions?amount=10
```

```json
{
  "results": [
    {
      "position": 1,
      "rarity": "ZERO",
      "isNew": true,
      "guaranteed": false,
      "identity": {
        "id": 1,
        "name": "LCB Sinner Yi Sang",
        "rarity": "ZERO",
        "sinnerId": 1,
        "sinnerName": "Yi Sang",
        "uptie1ArtUrl": "https://res.cloudinary.com/...",
        "uptie3ArtUrl": "https://res.cloudinary.com/...",
        "uptieCutscene": null,
        "obtainmentVideoUrl": null
      },
      "ego": null
    },
    {
      "position": 7,
      "rarity": "DOUBLE",
      "isNew": true,
      "guaranteed": true,
      "identity": { "...": "..." },
      "ego": null
    }
  ],
  "pity": 3,
  "pullsUntilGuarantee": 7,
  "currency": 11700
}
```

*(lista resumida; um 10x sempre traz 10 itens em `results`)*

| Campo | Significado |
|---|---|
| `position` | Ordem do giro (1 a 10) |
| `rarity` | `ZERO`, `DOUBLE`, `TRIPLE` ou `EGO`. O app usa para escolher a cor da animação. |
| `isNew` | `true` se é a primeira vez que o jogador tira esse item |
| `guaranteed` | `true` no giro garantido pelo pity |
| `identity` / `ego` | Item sorteado. Só um dos dois vem preenchido. |
| `pity`, `pullsUntilGuarantee` | Estado do pity depois da extração |
| `currency` | Lunacy restante |

Itens repetidos não aparecem duas vezes no inventário.

---

## Erros

Todas as respostas de erro têm o mesmo formato:

```json
{ "error": "mensagem explicando o problema" }
```

| Status | Quando acontece |
|---|---|
| 400 Bad Request | Username inválido, `amount` diferente de 1 ou 10, banner inativo ou Lunacy insuficiente |
| 404 Not Found | Player ou banner inexistente |
| 409 Conflict | Conflito ao salvar, como dois cadastros simultâneos com o mesmo username |

Exemplo:

```json
{ "error": "Lunacy insuficiente: precisa de 1300, tem 520" }
```

---

## Estrutura do código

```
src/main/java/com/kaio/limbus_gacha_api
 ├─ config        configuração do OpenAPI
 ├─ controller    Player, Banner e Extraction
 ├─ dto           records de request e response
 ├─ entity        entidades do catálogo (cópia da admin) + Player e PlayerBanner
 ├─ enums         Rarity
 ├─ expection     exceções e tratamento global de erros
 ├─ repository    interfaces Spring Data JPA
 └─ service       PlayerService, BannerService e ExtractionService (sorteio)

src/main/resources
 ├─ application.properties
 └─ static        docs.html e swagger-limbus.css (Swagger com o tema Limbus)
```

---

*Projeto acadêmico sem fins comerciais. Limbus Company e seus personagens pertencem à Project Moon.*

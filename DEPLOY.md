# Deploy do Connect+ (Vercel + Railway + Neon)

Guia passo a passo para colocar o projeto no ar no domínio **connectplus.app.br**.

- **Front-end (Angular)** → Vercel
- **Back-end (Spring Boot)** → Railway
- **Banco (PostgreSQL)** → Neon

> Faça **na ordem**: Neon → Railway → Vercel → DNS → CORS final.
> Cada serviço gera HTTPS automático (Let's Encrypt) assim que o DNS resolver.

---

## 1. Neon (banco) — pegar a connection string

1. Acesse o projeto no [console.neon.tech](https://console.neon.tech).
2. Em **Dashboard → Connection Details**, copie a connection string. Ela vem assim:
   ```
   postgresql://USUARIO:SENHA@ep-xxx.sa-east-1.aws.neon.tech/connect_plus?sslmode=require
   ```
3. Anote separadamente (vai usar no Railway):
   - **Host:** `ep-xxx.sa-east-1.aws.neon.tech`
   - **Database:** `connect_plus`
   - **Usuário** e **Senha**

---

## 2. Railway (back-end)

1. [railway.app](https://railway.app) → **New Project** → **Deploy from GitHub repo** → escolha o repositório.
2. **Settings → Source → Root Directory:** `Projeto/Back-End`
   *(é monorepo; sem isso o Railway não acha o Dockerfile)*
3. **Variables** → **New Variable** → adicione uma a uma:
   | Nome | Valor |
   |------|-------|
   | `SPRING_DATASOURCE_URL` | `jdbc:postgresql://ep-xxx.sa-east-1.aws.neon.tech/connect_plus?sslmode=require` |
   | `SPRING_DATASOURCE_USERNAME` | usuário do Neon |
   | `SPRING_DATASOURCE_PASSWORD` | senha do Neon |
   | `APP_CORS_ALLOWED_ORIGINS` | `https://connectplus.app.br,https://www.connectplus.app.br,https://*.vercel.app` |

   > **Não** crie a variável `PORT` — o Railway injeta sozinho e o app já lê `${PORT}`.
   > Repare que o `SPRING_DATASOURCE_URL` começa com `jdbc:postgresql://` (não é a string crua do Neon).
4. Aguarde o deploy (ele builda pelo `Dockerfile`, leva alguns minutos na 1ª vez).
5. **Settings → Networking → Generate Domain** para ganhar uma URL pública `https://xxx.up.railway.app`.
6. **Teste:** abra `https://xxx.up.railway.app/swagger-ui.html` — tem que carregar a API.
7. **Custom domain:** **Settings → Networking → Custom Domain** → digite `api.connectplus.app.br`.
   O Railway mostra um **alvo CNAME** (algo como `xxx.up.railway.app`). Anote para o passo 4 do DNS.

---

## 3. Vercel (front-end)

1. [vercel.com](https://vercel.com) → **Add New… → Project** → importe o repositório.
2. Na tela de configuração:
   - **Root Directory:** `Projeto/Front-End/connect-plus` (clique em *Edit* e selecione)
   - **Framework Preset:** Angular (ou *Other* — o `vercel.json` já define build e output)
   - Não precisa mexer em Build Command / Output (vêm do `vercel.json`)
3. **Deploy.** Ao terminar, abre numa URL `https://connect-plus-xxx.vercel.app`.
   > Nesse momento o login/API ainda podem falhar se o back não estiver no domínio
   > `api.connectplus.app.br`. Isso se resolve nos passos 4 e 5.
4. **Settings → Domains** → adicione:
   - `connectplus.app.br`
   - `www.connectplus.app.br`

   A Vercel mostra os registros DNS exatos para cada um (use no passo 4 do DNS).

---

## 4. DNS (painel do registro.br do connectplus.app.br)

Crie os registros (os valores exatos a Vercel/Railway confirmam na tela):

| Tipo | Nome | Valor | Aponta para |
|------|------|-------|-------------|
| A | `@` | `76.76.21.21` | Vercel (front, domínio raiz) |
| CNAME | `www` | `cname.vercel-dns.com` | Vercel (front, www) |
| CNAME | `api` | *(alvo CNAME do Railway)* | Railway (back) |

> Propagação leva de alguns minutos até algumas horas. Os domínios ficam
> "Valid/Active" nos painéis da Vercel e do Railway quando resolverem.

---

## 5. Ajuste final de CORS

Depois que `connectplus.app.br` estiver respondendo pela Vercel, confirme que a
variável `APP_CORS_ALLOWED_ORIGINS` no Railway inclui o domínio final
(passo 2.3). Se mudar algo, o Railway redeploya sozinho ao salvar a variável.

---

## Checklist final

- [ ] `https://api.connectplus.app.br/swagger-ui.html` abre
- [ ] `https://connectplus.app.br` abre o front
- [ ] Login funciona (front fala com o back sem erro de CORS no console do navegador)
- [ ] Dar F5 em `/dashboard` não dá 404 (rewrite de SPA do `vercel.json`)

## Se algo falhar

- **Erro de CORS no console** → domínio do front não está em `APP_CORS_ALLOWED_ORIGINS`.
- **Front carrega mas API dá erro de conexão** → `apiBase` em
  `src/environments/environment.prod.ts` não bate com a URL real do back.
- **404 ao recarregar uma rota** → `vercel.json` / Root Directory errado na Vercel.
- **Build do back falha no Railway** → confira o Root Directory `Projeto/Back-End`.

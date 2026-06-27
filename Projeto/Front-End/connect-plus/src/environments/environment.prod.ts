// Ambiente de produção (deploy na Vercel).
// apiBase/wsBase apontam para o back-end no Railway via subdomínio próprio.
// Configure o domínio "api.connectplus.app.br" no Railway apontando para o
// serviço do back-end. Se preferir usar a URL gerada pelo Railway, troque por:
//   apiBase: 'https://SEU-SERVICO.up.railway.app',
//   wsBase:  'wss://SEU-SERVICO.up.railway.app',
export const environment = {
  production: true,
  apiBase: 'https://api.connectplus.app.br',
  wsBase: 'wss://api.connectplus.app.br',
};

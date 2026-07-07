export interface MensagemSinalizacao {
  tipo: 'offer' | 'answer' | 'ice' | 'sair';
  to?: string;
  from?: string;
  conteudo?: unknown;
}

export type TipoConversa = 'privada' | 'grupo';
export type TipoMensagem = 'texto' | 'arquivo' | 'imagem';

export interface ParticipanteConversa {
  idUsuarioEmpresa: number;
  idUsuario: number;
  idEmpresa: number;
  nome: string;
  email: string;
}

export interface MensagemAnexo {
  id?: number;
  filename: string;
  data: string;
  tipoMime?: string;
  tamanho?: number;
}

export interface Mensagem {
  id: number;
  remetente: ParticipanteConversa;
  tipo: TipoMensagem;
  conteudo: string;
  anexo?: MensagemAnexo;
  enviadaEm: string;
  editadaEm?: string;
  enviadaPeloUsuarioLogado: boolean;
  lidaPeloUsuarioLogado: boolean;
  quantidadeLeituras: number;
  totalParticipantes: number;
}

export interface ConversaResumo {
  id: number;
  tipo: TipoConversa;
  nome: string;
  criadoEm: string;
  atualizadoEm: string;
  participantes: ParticipanteConversa[];
  ultimaMensagem?: Mensagem;
}

export interface ConversaDetalhe extends ConversaResumo {
  mensagens: Mensagem[];
}

export interface ChatEvento {
  tipo: 'CONVERSA_CRIADA' | 'MENSAGEM_ENVIADA' | 'MENSAGEM_LIDA';
  idConversa: number;
  idMensagem?: number;
  idUsuarioEmpresaOrigem?: number;
  ocorridoEm: string;
}

export interface UsuarioChat {
  id?: number;
  idUsuario?: number;
  idUsuarioEmpresa?: number;
  idEmpresa?: number;
  nome: string;
  email: string;
  cargo?: string;
  departamento?: string;
  status?: string;
}

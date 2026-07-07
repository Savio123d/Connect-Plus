import { ElementRef, Injectable } from '@angular/core';
import { SinalizacaoService } from './sinalizacao.service';
import { MensagemSinalizacao } from './transmissao.model';

@Injectable({
  providedIn: 'root',
})
export class TransmissaoService {
  videoLocal?: ElementRef<HTMLVideoElement>;

  taxaFps = 15;
  compartilhandoTela = false;
  assistindo = false;

  private streamLocal?: MediaStream;
  private peer?: RTCPeerConnection;
  private destinatario?: string;
  private candidatosPendentes: RTCIceCandidateInit[] = [];

  private readonly configuracaoRtc: RTCConfiguration = {
    iceServers: [{ urls: 'stun:stun.l.google.com:19302' }],
  };

  constructor(private sinalizacao: SinalizacaoService) {
    this.sinalizacao.eventos$.subscribe((mensagem) => {
      this.processarSinal(mensagem);
    });
  }

  async iniciarCompartilhamento(destinatario: string): Promise<void> {
    try {
      const stream = await navigator.mediaDevices.getDisplayMedia({
        video: { frameRate: { ideal: this.taxaFps } },
      });

      this.streamLocal = stream;
      this.compartilhandoTela = true;
      this.destinatario = destinatario;

      if (this.videoLocal) {
        this.videoLocal.nativeElement.srcObject = stream;
      }

      stream.getVideoTracks()[0].addEventListener('ended', () => {
        this.pararCompartilhamento();
      });

      this.peer = this.criarPeer(destinatario);
      stream.getTracks().forEach((track) => this.peer!.addTrack(track, stream));

      const offer = await this.peer.createOffer();
      await this.peer.setLocalDescription(offer);
      this.sinalizacao.enviar({ tipo: 'offer', to: destinatario, conteudo: offer });
    } catch (erro) {
      console.error('Compartilhamento cancelado ou falhou:', erro);
    }
  }

  pararCompartilhamento(): void {
    if (this.destinatario) {
      this.sinalizacao.enviar({ tipo: 'sair', to: this.destinatario });
    }

    this.streamLocal?.getTracks().forEach((track) => track.stop());
    this.streamLocal = undefined;
    this.limparConexao();
  }

  private processarSinal(mensagem: MensagemSinalizacao): void {
    if (mensagem.tipo === 'offer') {
      this.responderOferta(mensagem);
      return;
    }

    if (mensagem.tipo === 'answer' && this.peer) {
      this.peer
        .setRemoteDescription(mensagem.conteudo as RTCSessionDescriptionInit)
        .then(() => this.aplicarCandidatosPendentes());
      return;
    }

    if (mensagem.tipo === 'ice') {
      this.receberCandidato(mensagem.conteudo as RTCIceCandidateInit);
      return;
    }

    if (mensagem.tipo === 'sair') {
      this.limparConexao();
    }
  }

  private async responderOferta(mensagem: MensagemSinalizacao): Promise<void> {
    if (!mensagem.from) {
      return;
    }

    this.destinatario = mensagem.from;
    this.peer = this.criarPeer(mensagem.from);

    await this.peer.setRemoteDescription(mensagem.conteudo as RTCSessionDescriptionInit);
    await this.aplicarCandidatosPendentes();

    const answer = await this.peer.createAnswer();
    await this.peer.setLocalDescription(answer);
    this.sinalizacao.enviar({ tipo: 'answer', to: mensagem.from, conteudo: answer });
  }

  private criarPeer(destino: string): RTCPeerConnection {
    const peer = new RTCPeerConnection(this.configuracaoRtc);

    peer.onicecandidate = (evento) => {
      if (evento.candidate) {
        this.sinalizacao.enviar({ tipo: 'ice', to: destino, conteudo: evento.candidate.toJSON() });
      }
    };

    peer.ontrack = (evento) => {
      this.assistindo = true;

      if (this.videoLocal) {
        this.videoLocal.nativeElement.srcObject = evento.streams[0];
      }
    };

    return peer;
  }

  private async receberCandidato(candidato: RTCIceCandidateInit): Promise<void> {
    if (this.peer?.remoteDescription) {
      await this.peer.addIceCandidate(candidato);
      return;
    }

    this.candidatosPendentes.push(candidato);
  }

  private async aplicarCandidatosPendentes(): Promise<void> {
    for (const candidato of this.candidatosPendentes) {
      await this.peer?.addIceCandidate(candidato);
    }

    this.candidatosPendentes = [];
  }

  private limparConexao(): void {
    this.peer?.close();
    this.peer = undefined;
    this.destinatario = undefined;
    this.candidatosPendentes = [];
    this.compartilhandoTela = false;
    this.assistindo = false;

    if (this.videoLocal) {
      this.videoLocal.nativeElement.srcObject = null;
    }
  }
}

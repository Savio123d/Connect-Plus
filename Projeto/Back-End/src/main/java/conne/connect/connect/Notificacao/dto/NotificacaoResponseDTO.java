package conne.connect.connect.Notificacao.dto;

import conne.connect.connect.Notificacao.enums.TipoNotificacao;
import conne.connect.connect.Notificacao.model.NotificacaoModel;
import java.time.LocalDateTime;

public class NotificacaoResponseDTO {

    private Long idNotificacao;
    private Long idEmpresa;
    private Long idUsuarioEmpresa;

    private TipoNotificacao tipo;
    private String titulo;
    private String mensagem;

    private Boolean lida;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataLeitura;

    public NotificacaoResponseDTO() {
    }

    public NotificacaoResponseDTO(
            Long idNotificacao,
            Long idEmpresa,
            Long idUsuarioEmpresa,
            TipoNotificacao tipo,
            String titulo,
            String mensagem,
            Boolean lida,
            LocalDateTime dataCriacao,
            LocalDateTime dataLeitura
    ) {
        this.idNotificacao = idNotificacao;
        this.idEmpresa = idEmpresa;
        this.idUsuarioEmpresa = idUsuarioEmpresa;
        this.tipo = tipo;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.lida = lida;
        this.dataCriacao = dataCriacao;
        this.dataLeitura = dataLeitura;
    }

    public static NotificacaoResponseDTO fromModel(NotificacaoModel notificacao) {
        if (notificacao == null) {
            return null;
        }

        Long idEmpresa = notificacao.getIdEmpresa() != null
                ? notificacao.getIdEmpresa().getIdEmpresa()
                : null;

        Long idUsuarioEmpresa = notificacao.getIdUsuarioEmpresa() != null
                ? notificacao.getIdUsuarioEmpresa().getIdUsuarioEmpresa()
                : null;

        return new NotificacaoResponseDTO(
                notificacao.getIdNotificacao(),
                idEmpresa,
                idUsuarioEmpresa,
                notificacao.getTipo(),
                notificacao.getTitulo(),
                notificacao.getMensagem(),
                notificacao.getLida(),
                notificacao.getDataCriacao(),
                notificacao.getDataLeitura()
        );
    }

    public Long getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(Long idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Long getIdUsuarioEmpresa() {
        return idUsuarioEmpresa;
    }

    public void setIdUsuarioEmpresa(Long idUsuarioEmpresa) {
        this.idUsuarioEmpresa = idUsuarioEmpresa;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacao tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataLeitura() {
        return dataLeitura;
    }

    public void setDataLeitura(LocalDateTime dataLeitura) {
        this.dataLeitura = dataLeitura;
    }
}

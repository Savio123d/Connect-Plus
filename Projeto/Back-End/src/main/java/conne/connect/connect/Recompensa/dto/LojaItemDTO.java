package conne.connect.connect.Recompensa.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LojaItemDTO {

    private Long id;
    private Long idLoja;
    private Long idEmpresa;
    private String nome;
    private String descricao;
    private Integer custoXp;
    private Integer quantidadeDisponivel;
    private String categoria;
    private String icone;
    private String cor;
    private Boolean ativa;
    private Boolean resgatada;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}

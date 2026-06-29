package conne.connect.connect.Imagem.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImagemUploadResponseDTO {

    private String filename;
    private String data;
    private String tipoMime;
    private Integer tamanho;
    private String s3Key;
}

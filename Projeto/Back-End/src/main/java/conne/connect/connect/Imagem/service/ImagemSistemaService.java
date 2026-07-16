package conne.connect.connect.Imagem.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Imagem.dto.ImagemUploadResponseDTO;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@Service
public class ImagemSistemaService {

    private final S3Client s3Client;
    private final AutorizacaoService autorizacaoService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.max-image-size-bytes:5242880}")
    private long maxImageSizeBytes;

    public ImagemSistemaService(S3Client s3Client, AutorizacaoService autorizacaoService) {
        this.s3Client = s3Client;
        this.autorizacaoService = autorizacaoService;
    }

    public ImagemUploadResponseDTO enviarImagemChat(
            MultipartFile arquivo,
            Long idEmpresa,
            Long idUsuarioEmpresa
    ) {
        validarArquivo(arquivo, idEmpresa, idUsuarioEmpresa);
        autorizacaoService.validarEmpresaAtual(idEmpresa);
        autorizacaoService.validarVinculoAtual(idUsuarioEmpresa);

        String tipoMime = arquivo.getContentType();
        String nomeOriginal = sanitizarNome(arquivo.getOriginalFilename());
        String s3Key = montarChave(idEmpresa, nomeOriginal);

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .contentType(tipoMime)
                    .contentLength(arquivo.getSize())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(arquivo.getBytes()));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao ler o arquivo enviado.", e);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY, "Falha ao enviar a imagem para o S3.", e);
        }

        ImagemUploadResponseDTO resposta = new ImagemUploadResponseDTO();
        resposta.setFilename(nomeOriginal);
        resposta.setData(urlPublica(s3Key));
        resposta.setTipoMime(tipoMime);
        resposta.setTamanho((int) arquivo.getSize());
        resposta.setS3Key(s3Key);
        return resposta;
    }

    public String urlPublica(String urlOuChave) {
        String chave = extrairChave(urlOuChave);
        if (chave == null || chave.isBlank()) {
            return urlOuChave;
        }
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucket).key(chave).build())
                .toExternalForm();
    }


    public String extrairChave(String urlOuChave) {
        if (urlOuChave == null || urlOuChave.isBlank()) {
            return null;
        }
        if (!urlOuChave.startsWith("http")) {
            return urlOuChave.trim();
        }
        try {
            String caminho = URI.create(urlOuChave).getPath();
            return (caminho != null && caminho.length() > 1) ? caminho.substring(1) : null;
        } catch (RuntimeException e) {
            return null;
        }
    }

    private void validarArquivo(MultipartFile arquivo, Long idEmpresa, Long idUsuarioEmpresa) {
        if (idEmpresa == null || idUsuarioEmpresa == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Empresa e usuário são obrigatórios.");
        }
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Arquivo de imagem é obrigatório.");
        }

        String tipoMime = arquivo.getContentType();
        if (tipoMime == null || !tipoMime.toLowerCase().startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE, "O arquivo enviado não é uma imagem.");
        }
        if (arquivo.getSize() > maxImageSizeBytes) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "Imagem excede o tamanho máximo permitido (" + maxImageSizeBytes + " bytes).");
        }
    }

    private String montarChave(Long idEmpresa, String nomeOriginal) {
        String extensao = "";
        int ponto = nomeOriginal.lastIndexOf('.');
        if (ponto >= 0) {
            extensao = nomeOriginal.substring(ponto).toLowerCase();
        }
        return "chat/" + idEmpresa + "/" + UUID.randomUUID() + extensao;
    }

    private String sanitizarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "imagem";
        }
        String limpo = nome.replaceAll("[\\\\/\\r\\n]", "_").trim();
        return limpo.isBlank() ? "imagem" : limpo;
    }
}

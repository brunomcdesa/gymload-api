package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.exception.IntegracaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BackBlazeService {

    private static final int QTD_MAX_DIAS_IMAGEM = 7;
    @Value("${api.aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public void uploadFile(String fileName, MultipartFile file) {
        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build(),
                RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException exception) {
            throw new IntegracaoException(exception, BackBlazeService.class.getName(),
                "Erro ao salvar arquivo no BackBlaze.");
        }
    }

    public String generatePresignedUrl(String fileName) {
        try {
            var getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

            return s3Presigner.presignGetObject(GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofDays(QTD_MAX_DIAS_IMAGEM))
                    .getObjectRequest(getObjectRequest)
                    .build())
                .url()
                .toString();
        } catch (Exception exception) {
            throw new IntegracaoException(exception, BackBlazeService.class.getName(),
                "Erro ao gerar URL assinada para o arquivo.");
        }
    }
}

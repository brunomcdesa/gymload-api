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

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class BackBlazeService {

    @Value("${api.aws.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

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

    public InputStream downloadFile(String fileName) {
        try {
            return s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build()
            );
        } catch (Exception exception) {
            throw new IntegracaoException(exception, BackBlazeService.class.getName(),
                "Erro ao buscar arquivo no BackBlaze.");
        }
    }
}

package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.exception.IntegracaoException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;

import static br.com.gymloadapi.helper.TestsHelper.umMockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BackBlazeServiceTest {

    @InjectMocks
    private BackBlazeService service;
    @Mock
    private S3Client s3Client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "bucketName", "bucket-test");
    }

    @Test
    @SneakyThrows
    void uploadFile_deveFazerUpload_quandoNaoOcorrerErro() {
        assertDoesNotThrow(() -> service.uploadFile("file.jpeg", umMockMultipartFile()));

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @SneakyThrows
    void uploadFile_deveLancarException_quandoOcorrerErroParaSalvarArquivo() {
        var file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("Erro ao ler arquivo"));

        var exception = assertThrows(
            IntegracaoException.class,
            () -> service.uploadFile("file", file)
        );
        assertEquals("Erro ao salvar arquivo no BackBlaze.", exception.getMessage());

        verifyNoInteractions(s3Client);
    }

    @Test
    @SneakyThrows
    void downloadFile_deveRetorarResourceDoArquivo_quandoNaoOcorrerErroDuranteFluxo() {
        var mockResponseStream = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponseStream);

        assertNotNull(service.downloadFile("file.jpeg"));

        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    void downloadFile_QuandoOcorreExcecao_DeveLancarIntegracaoException() {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(S3Exception.class);

        var exception = assertThrowsExactly(
            IntegracaoException.class,
            () -> service.downloadFile("file.jpeg")
        );
        assertEquals("Erro ao buscar arquivo no BackBlaze.", exception.getMessage());

        verify(s3Client).getObject(any(GetObjectRequest.class));
    }
}

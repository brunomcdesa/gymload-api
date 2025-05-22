package br.com.gymloadapi.modulos.comum.service;

import br.com.gymloadapi.modulos.comum.exception.IntegracaoException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;

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
    @Mock
    private S3Presigner s3Presigner;

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
    void generatePresignedUrl_deveRetorarUrlDeImagem_quandoNaoOcorrerErroDuranteFluxo() {
        var presignedGetObjectRequest = mock(PresignedGetObjectRequest.class);
        var mockedUrl = mock(URL.class);

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGetObjectRequest);
        when(presignedGetObjectRequest.url()).thenReturn(mockedUrl);
        when(mockedUrl.toString()).thenReturn("http://teste/file.jpeg");

        assertNotNull(service.generatePresignedUrl("file.jpeg"));

        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @SneakyThrows
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void generatePresignedUrl_deveRetorarUrlDeImagem_quandoImagemUsuarioForInvalido(String imagemUsuario) {
        var presignedGetObjectRequest = mock(PresignedGetObjectRequest.class);
        var mockedUrl = mock(URL.class);

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedGetObjectRequest);
        when(presignedGetObjectRequest.url()).thenReturn(mockedUrl);
        when(mockedUrl.toString()).thenReturn("http://teste/file.jpeg");

        assertNotNull(service.generatePresignedUrl(imagemUsuario));

        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    void generatePresignedUrl_deveLancarIntegracaoException_quandoOcorrerErroDuranteFluxo() {
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenThrow(S3Exception.class);

        var exception = assertThrowsExactly(
            IntegracaoException.class,
            () -> service.generatePresignedUrl("file.jpeg")
        );
        assertEquals("Erro ao gerar URL assinada para o arquivo.", exception.getMessage());

        verify(s3Presigner).presignGetObject(any(GetObjectPresignRequest.class));
    }
}

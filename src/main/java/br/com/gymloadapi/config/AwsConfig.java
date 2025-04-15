package br.com.gymloadapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${api.aws.accessKeyId}")
    private String accessKey;
    @Value("${api.aws.secretKey}")
    private String secretKey;
    @Value("${api.aws.endpoint}")
    private String endpoint;
    @Value("${api.aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .serviceConfiguration(S3Configuration.builder()
                .checksumValidationEnabled(false)
                .build()
            ).build();
    }
}

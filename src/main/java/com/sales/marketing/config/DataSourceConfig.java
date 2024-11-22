package com.sales.marketing.config;

import java.util.Base64;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${aws.s3.accessKey}")
    private String accessKeyAWS;

    @Value("${aws.s3.secretKey}")
    private String secretKeyAWS;

    @Value("${aws.secretsmanager.name}")
    private String secretNameAWS;

    @Value("${aws.region}")
    private String regionNameAWS;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int minIdle;

    @Value("${spring.datasource.hikari.idle-timeout}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean
    public DataSource getDataSource() throws JsonMappingException, JsonProcessingException {
        String secretName = secretNameAWS;
        String region = regionNameAWS;
        String accessKey = accessKeyAWS;
        String secretKey = secretKeyAWS;

        AWSSecretsManager client;
        if (accessKeyAWS.isEmpty() && secretKeyAWS.isEmpty()) {
            client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
        } else {
            client = AWSSecretsManagerClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(accessKey, secretKey)))
                    .withRegion(region).build();
        }

        String secret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            LOG.info("Secret fetched");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> awsProperties = mapper.readValue(secret, Map.class);
            LOG.info("Connected to DB URL: " + awsProperties.get("aws.spring.datasource.url"));

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(awsProperties.get("aws.spring.datasource.url"));
            hikariConfig.setUsername(awsProperties.get("aws.spring.datasource.username"));
            hikariConfig.setPassword(awsProperties.get("aws.spring.datasource.password"));
            hikariConfig.setMaximumPoolSize(maxPoolSize);
            hikariConfig.setMinimumIdle(minIdle);
            hikariConfig.setIdleTimeout(idleTimeout);
            hikariConfig.setMaxLifetime(maxLifetime);
            hikariConfig.setConnectionTimeout(connectionTimeout);

            return new HikariDataSource(hikariConfig);
        } else {
            String decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
            LOG.info("Decoded Binary Secret successfully: " + decodedBinarySecret);
            throw new IllegalStateException("Secret value is in binary format, which is not supported for now.");
        }
    }
}

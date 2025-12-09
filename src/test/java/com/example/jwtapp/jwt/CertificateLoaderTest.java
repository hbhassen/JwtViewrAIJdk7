package com.example.jwtapp.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.jwtapp.config.JwtProperties;
import com.example.jwtapp.testutil.TestKeyProvider;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

class CertificateLoaderTest {

    @Test
    void loadCertificate_shouldReturnPublicKey() {
        JwtProperties properties = new JwtProperties();
        properties.setCertificatePath("file:" + TestKeyProvider.certificatePath());
        CertificateLoader loader = new CertificateLoader(new DefaultResourceLoader(), properties);

        var certificate = loader.loadCertificate();

        assertThat(certificate).isNotNull();
        assertThat(certificate.getPublicKey()).isNotNull();
    }
}

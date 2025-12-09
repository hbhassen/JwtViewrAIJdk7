package com.example.jwtapp.jwt;

import com.example.jwtapp.config.JwtProperties;
import com.example.jwtapp.exception.CertificateLoadingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class CertificateLoader {

    private static final Logger log = LoggerFactory.getLogger(CertificateLoader.class);

    private final ResourceLoader resourceLoader;
    private final JwtProperties properties;

    private volatile X509Certificate cachedCertificate;

    public CertificateLoader(ResourceLoader resourceLoader, JwtProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    public X509Certificate loadCertificate() {
        X509Certificate local = cachedCertificate;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (cachedCertificate == null) {
                cachedCertificate = doLoadCertificate();
            }
            return cachedCertificate;
        }
    }

    public PublicKey loadPublicKey() {
        return loadCertificate().getPublicKey();
    }

    private X509Certificate doLoadCertificate() {
        var path = properties.getCertificatePath();
        Resource resource = resourceLoader.getResource(path);
        if (!resource.exists()) {
            throw new CertificateLoadingException("Certificat introuvable : " + path);
        }
        try (InputStream inputStream = resource.getInputStream()) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(inputStream);
        } catch (IOException | CertificateException e) {
            log.error("Echec de chargement du certificat {}", path, e);
            throw new CertificateLoadingException("Impossible de charger le certificat : " + path, e);
        }
    }
}

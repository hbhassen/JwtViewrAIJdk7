package com.example.jwtapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import org.springframework.core.io.ClassPathResource;

public final class CertificateUtils {

    private CertificateUtils() {
    }

    public static X509Certificate loadCertificate(String classpathLocation) throws CertificateException, IOException {
        ClassPathResource resource = new ClassPathResource(classpathLocation);
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certificateFactory.generateCertificate(inputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
        }
    }

    public static PublicKey extractPublicKey(X509Certificate certificate) {
        if (certificate == null) {
            return null;
        }
        return certificate.getPublicKey();
    }
}

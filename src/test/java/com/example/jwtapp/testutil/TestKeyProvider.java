package com.example.jwtapp.testutil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

public final class TestKeyProvider {

    private static final KeyPair KEY_PAIR;
    private static final Path CERT_FILE;

    static {
        Security.addProvider(new BouncyCastleProvider());
        try {
            KEY_PAIR = Keys.keyPairFor(SignatureAlgorithm.RS256);
            CERT_FILE = createCertificateFile(KEY_PAIR);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to initialize test keys", e);
        }
    }

    private TestKeyProvider() {
    }

    public static String certificatePath() {
        return CERT_FILE.toAbsolutePath().toString();
    }

    public static String createValidToken() {
        return buildToken(KEY_PAIR.getPrivate(), Instant.now().plusSeconds(3600));
    }

    public static String createExpiredToken() {
        return buildToken(KEY_PAIR.getPrivate(), Instant.now().minusSeconds(3600));
    }

    public static String createInvalidSignatureToken() {
        KeyPair otherPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        return buildToken(otherPair.getPrivate(), Instant.now().plusSeconds(3600));
    }

    private static String buildToken(PrivateKey key, Instant expiry) {
        return Jwts.builder()
                .subject("user123")
                .issuer("issuer-demo")
                .claim("role", "tester")
                .expiration(Date.from(expiry))
                .signWith(key, SignatureAlgorithm.RS256)
                .compact();
    }

    private static Path createCertificateFile(KeyPair keyPair) throws Exception {
        X509Certificate certificate = createSelfSignedCertificate(keyPair);
        Path certDir = Path.of("target", "test-classes", "certs");
        Files.createDirectories(certDir);
        Path certFile = certDir.resolve("generated-test.crt");
        writePem(certificate, certFile);
        return certFile;
    }

    private static X509Certificate createSelfSignedCertificate(KeyPair keyPair) throws Exception {
        Instant now = Instant.now();
        X500Name dnName = new X500Name("CN=Test");
        BigInteger serial = BigInteger.valueOf(now.toEpochMilli());
        Date notBefore = Date.from(now.minusSeconds(60));
        Date notAfter = Date.from(now.plusSeconds(86400));

        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                dnName,
                serial,
                notBefore,
                notAfter,
                dnName,
                keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA")
                .build(keyPair.getPrivate());
        X509CertificateHolder holder = builder.build(signer);
        return new JcaX509CertificateConverter()
                .setProvider(BouncyCastleProvider.PROVIDER_NAME)
                .getCertificate(holder);
    }

    private static void writePem(X509Certificate certificate, Path certFile) throws Exception {
        PemObject pemObject = new PemObject("CERTIFICATE", certificate.getEncoded());
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(certFile), StandardCharsets.UTF_8);
             PemWriter pemWriter = new PemWriter(writer)) {
            pemWriter.writeObject(pemObject);
        } catch (Exception e) {
            throw new IOException("Unable to write certificate PEM", e);
        }
    }
}

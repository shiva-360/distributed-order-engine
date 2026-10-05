package com.shivansh.auth_service.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Getter
public class KeyProvider {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String keyId;

    public KeyProvider(
            @Value("${app.jwt.private-key-path}") String privatePath,
            @Value("${app.jwt.public-key-path}") String publicPath,
            @Value("${app.jwt.key-id}") String keyId) throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(readPem(privatePath)));
        this.publicKey = kf.generatePublic(new X509EncodedKeySpec(readPem(publicPath)));
        this.keyId = keyId;
    }

    private static byte[] readPem(String path) throws IOException {
        String pem = Files.readString(Path.of(path))
                .replaceAll("-----[A-Z ]+-----", "")
                .replaceAll("\\s", "");
        return Base64.getDecoder().decode(pem);
    }
}

package org.openmrs.module.pihmalawi;

import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class PublicKeyProvider {

    private PublicKey publicKey;

    public void init() throws Exception {
        //Load public key
        String publicKeyContent = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA53cAYbvpz2cL7jF68Qh6\n" +
                "sW1jGgr8bDyYROmhCdVStjU2pK//SRRb577bTMv8p1ovsuHulVkvnl0PnKnrvTjN\n" +
                "/Uv4D5SS2sJeuEL7shkXbrZEx8bsvRu6HZOqAL1scRF+Z4qxdnJByBIy2kc9ZKjs\n" +
                "bdHi2P6MEWigAJJfHn6gbtIKNoDy84Bn1KZ9gZp69R3bohkiBBUJAG3gn2o8wVvz\n" +
                "0JLUPHo/70oWQez5dQYKjckoI3ynzkgxtHqn/Nz2arqkYlDMUz0Eip7s6iL92Ynv\n" +
                "2kNtGCAW5VVy6zeJ1rvagdr0Uv4G+UXavwj6uhDmbxhgvPXQK9yY+JtkvOolGUpB\n" +
                "gwIDAQAB\n" +
                "-----END PUBLIC KEY-----";
        // Remove header, footer, and newlines from the key content
        publicKeyContent = publicKeyContent.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(keySpec);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}

package org.openmrs.module.pihmalawi;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Properties;

@Component
public class PublicKeyProvider {

    private PublicKey publicKey = null;

    public void init() throws Exception {
        Properties properties = Context.getRuntimeProperties();
        String pkFile = properties.getProperty(PihMalawiConstants.REST_DIGITAL_SIGNATURE_PUBLIC_KEY);
        if (StringUtils.isNotBlank(pkFile)) {
            //Load public key
            File file = new File(pkFile);
            FileInputStream fis = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(fis);
            byte[] keyBytes = new byte[(int) file.length()];
            dis.readFully(keyBytes);
            dis.close();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
    }

    public boolean verifySignature(String data, String signature) throws Exception {
        if ( this.publicKey == null ) {
            init();
        }
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(this.publicKey);
        sig.update(data.getBytes(StandardCharsets.UTF_8));
        return sig.verify(Base64.getDecoder().decode(signature.getBytes(StandardCharsets.UTF_8)));
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}

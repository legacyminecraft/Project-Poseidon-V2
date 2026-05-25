package com.legacyminecraft.poseidon.network.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public record ProxyHelloPacket(byte[] details, byte[] signature) {

    public static final String MAC_ALGORITHM = "HmacSHA256";

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(ProxyConnectionDetails.class, new ProxyConnectionDetails.Serializer())
            .create();

    public ProxyHelloPacket(DataInput input) throws IOException {
        byte[] details = new byte[input.readUnsignedShort()];
        input.readFully(details);
        byte[] signature = new byte[input.readUnsignedShort()];
        input.readFully(signature);
        this(details, signature);
    }

    public boolean isSignatureValid(byte[] secret) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(MAC_ALGORITHM);
        mac.init(new SecretKeySpec(secret, MAC_ALGORITHM));
        byte[] actualSignature = mac.doFinal(details());
        return MessageDigest.isEqual(signature(), actualSignature);
    }

    public ProxyConnectionDetails deserializeDetails() {
        return GSON.fromJson(new String(details(), StandardCharsets.UTF_8), ProxyConnectionDetails.class);
    }
}

package com.legacyminecraft.poseidon.network.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.common.net.InetAddresses;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.UUID;

public final class PlayerDataForwarding {

    public static final String CHANNEL = "proxy:forward_player_data";
    public static final String MAC_ALGORITHM = "HmacSHA256";

    private PlayerDataForwarding() {
    }

    public static byte[] verifySignature(byte[] payload, byte[] secret) throws InvalidSignatureException {
        ByteArrayInputStream buf = new ByteArrayInputStream(payload);

        byte[] signature = new byte[32];
        buf.read(signature, 0, signature.length);
        byte[] forwardedData = buf.readAllBytes();

        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, MAC_ALGORITHM));
            byte[] actualSignature = mac.doFinal(forwardedData);
            if (!MessageDigest.isEqual(signature, actualSignature)) {
                throw new InvalidSignatureException();
            }
            return forwardedData;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static ForwardedPlayerData readForwardedData(byte[] forwardedData) {
        ByteArrayDataInput input = ByteStreams.newDataInput(forwardedData);
        InetAddress address = InetAddresses.forString(input.readUTF());
        UUID id = new UUID(input.readLong(), input.readLong());
        String name = input.readUTF();
        boolean online = input.readBoolean();
        return new ForwardedPlayerData(address, new MinecraftProfile(id, name, online));
    }
}

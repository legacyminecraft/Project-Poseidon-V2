package com.legacyminecraft.poseidon.network.proxy;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.common.net.InetAddresses;
import com.legacyminecraft.poseidon.profile.MinecraftProfile;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerDataForwardingTest {

    @ParameterizedTest
    @MethodSource("arguments")
    void canVerifySignatureAndDeserialize(
            String secret,
            InetAddress address,
            MinecraftProfile profile
    ) throws Exception {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] payload = createForwardingData(secretBytes, address, profile);
        byte[] forwardedData = PlayerDataForwarding.verifySignature(payload, secretBytes);
        ForwardedPlayerData playerData = PlayerDataForwarding.readForwardedData(forwardedData);
        assertThat(playerData.address()).isEqualTo(address);
        assertThat(playerData.profile()).isEqualTo(profile);
    }

    private byte[] createForwardingData(byte[] secret, InetAddress address, MinecraftProfile profile) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(address.getHostAddress());
        output.writeLong(profile.id().getMostSignificantBits());
        output.writeLong(profile.id().getLeastSignificantBits());
        output.writeUTF(profile.name());
        output.writeBoolean(profile.online());
        byte[] forwardedData = output.toByteArray();

        try {
            Mac mac = Mac.getInstance(PlayerDataForwarding.MAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, PlayerDataForwarding.MAC_ALGORITHM));
            byte[] signature = mac.doFinal(forwardedData);
            ByteArrayOutputStream buf = new ByteArrayOutputStream(signature.length + forwardedData.length);
            buf.writeBytes(signature);
            buf.writeBytes(forwardedData);
            return buf.toByteArray();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("DfMZrNpXnSzRBnJWfwfJ", InetAddresses.forString("192.168.0.1"), new MinecraftProfile(UUID.fromString("142d3862-e360-419b-9ab3-421fed84f565"), "zavdav", true)),
                Arguments.of("6bFznsKTrqYdSvVSfDGk", InetAddresses.forString("10.0.0.1"), new MinecraftProfile(UUID.fromString("2cfc6452-a6b4-4c49-982e-492eaa3a14ec"), "JohnyMuffin", true)),
                Arguments.of("U4LKwH9dkvg8Y2ckufC2", InetAddresses.forString("172.16.0.1"), new MinecraftProfile(UUID.fromString("5d89d7c0-f301-302f-bb85-e7c5d8575d66"), ".offline", false))
        );
    }
}
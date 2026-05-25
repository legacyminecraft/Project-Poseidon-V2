package com.legacyminecraft.poseidon.network.proxy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProxyHelloPacketTest {

    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .create();

    @ParameterizedTest
    @MethodSource("arguments")
    void canVerifySignatureAndDeserialize(
            String sourceHost,
            int sourcePort,
            String secret
    ) throws Exception {
        JsonObject object = new JsonObject();
        object.addProperty("sourceHost", sourceHost);
        object.addProperty("sourcePort", sourcePort);
        byte[] detailsBytes = GSON.toJson(object).getBytes(StandardCharsets.UTF_8);

        Mac mac = Mac.getInstance(ProxyHelloPacket.MAC_ALGORITHM);
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        mac.init(new SecretKeySpec(secretBytes, ProxyHelloPacket.MAC_ALGORITHM));
        byte[] signature = mac.doFinal(detailsBytes);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(buffer);
        output.writeShort(detailsBytes.length);
        output.write(detailsBytes);
        output.writeShort(signature.length);
        output.write(signature);

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(buffer.toByteArray()));
        ProxyHelloPacket hello = new ProxyHelloPacket(input);
        assertThat(hello.isSignatureValid(secretBytes)).isTrue();

        ProxyConnectionDetails details = hello.deserializeDetails();
        assertThat(details.sourceHost()).isEqualTo(sourceHost);
        assertThat(details.sourcePort()).isEqualTo(sourcePort);
    }

    static Stream<Arguments> arguments() {
        return Stream.of(
                Arguments.of("192.168.0.1", 47468, "DfMZrNpXnSzRBnJWfwfJ"),
                Arguments.of("10.0.0.1", 24930, "6bFznsKTrqYdSvVSfDGk"),
                Arguments.of("172.16.0.1", 59207, "U4LKwH9dkvg8Y2ckufC2")
        );
    }
}
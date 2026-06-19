package com.legacyminecraft.poseidon.network.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ProxyHelloMessageTest {

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

        Mac mac = Mac.getInstance(ProxyHelloMessage.MAC_ALGORITHM);
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        mac.init(new SecretKeySpec(secretBytes, ProxyHelloMessage.MAC_ALGORITHM));
        byte[] signature = mac.doFinal(detailsBytes);

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeShort(detailsBytes.length);
        output.write(detailsBytes);
        output.writeShort(signature.length);
        output.write(signature);

        ByteArrayDataInput input = ByteStreams.newDataInput(output.toByteArray());
        ProxyHelloMessage helloMessage = new ProxyHelloMessage(input);
        assertThat(helloMessage.isSignatureValid(secretBytes)).isTrue();

        ProxyConnectionDetails details = helloMessage.deserializeDetails();
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
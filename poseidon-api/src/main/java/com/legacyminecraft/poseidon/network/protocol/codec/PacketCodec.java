package com.legacyminecraft.poseidon.network.protocol.codec;

import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Represents a function which encodes and decodes a duplex packet.
 *
 * @param <P> the type of packet to encode/decode
 */
public interface PacketCodec<P extends DuplexPacket> extends PacketEncoder<P>, PacketDecoder<P> {

    /**
     * Creates a new codec from an encoder and a decoder.
     *
     * @param encoder the encoder
     * @param decoder the decoder
     * @return the created codec
     * @param <P> the type of packet to encode/decode
     */
    static <P extends DuplexPacket> PacketCodec<P> of(PacketEncoder<P> encoder, PacketDecoder<P> decoder) {
        return new PacketCodec<>() {
            @Override
            public void encode(P packet, DataOutput output) throws IOException {
                encoder.encode(packet, output);
            }

            @Override
            public P decode(DataInput input) throws IOException {
                return decoder.decode(input);
            }
        };
    }
}

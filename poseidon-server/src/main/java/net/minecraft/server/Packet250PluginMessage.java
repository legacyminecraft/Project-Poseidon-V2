package net.minecraft.server;

import com.legacyminecraft.poseidon.messaging.Messenger;
import com.legacyminecraft.poseidon.network.protocol.DuplexPacket;
import com.legacyminecraft.poseidon.network.protocol.ProtocolUtil;
import com.legacyminecraft.poseidon.network.protocol.codec.PacketCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet250PluginMessage extends Packet implements DuplexPacket {

    public static final PacketCodec<Packet250PluginMessage> CODEC = PacketCodec.of(
            Packet250PluginMessage::a, Packet250PluginMessage::new
    );

    public String channel;
    public byte[] message;

    public Packet250PluginMessage(String channel, byte[] message) {
        this.channel = channel;
        this.message = message;
    }

    public Packet250PluginMessage(DataInput input) throws IOException {
        a(input);
    }

    public void a(DataInput input) throws IOException {
        this.channel = ProtocolUtil.readString(input, Messenger.MAX_CHANNEL_LENGTH);
        this.message = readMessage(input);
    }

    public void a(DataOutput output) throws IOException {
        ProtocolUtil.writeString(this.channel, output);
        writeMessage(this.message, output);
    }

    public void a(NetHandler nethandler) {
        nethandler.handlePluginMessage(this);
    }

    public int a() {
        return 2 + this.channel.length() * 2 + 2 + this.message.length;
    }

    private static byte[] readMessage(DataInput input) throws IOException {
        short length = input.readShort();
        byte[] message = new byte[length];
        input.readFully(message);
        return message;
    }

    private static void writeMessage(byte[] message, DataOutput output) throws IOException {
        output.writeShort(message.length);
        output.write(message);
    }
}

package com.legacyminecraft.poseidon.network.connection;

import com.legacyminecraft.poseidon.network.protocol.OutboundPacket;
import net.minecraft.server.NetHandler;

import java.net.InetSocketAddress;

public interface INetworkManager {

    void a(NetHandler netHandler);

    void queue(OutboundPacket packet);

    void a();

    void a(String s, Object... aobject);

    void b();

    InetSocketAddress getSocketAddress();

    void d();

    int e();
}

package com.legacyminecraft.poseidon.network.connection;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.legacyminecraft.poseidon.Poseidon;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConnectionThrottle {

    private final LoadingCache<InetAddress, AtomicInteger> cache;
    private final int threshold;

    public ConnectionThrottle() {
        this.cache = CacheBuilder.newBuilder()
                .ticker(Ticker.systemTicker())
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .expireAfterWrite(Poseidon.getConfig().network.connectionThrottling.interval.getNanos(), TimeUnit.NANOSECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public AtomicInteger load(InetAddress key) {
                        return new AtomicInteger(0);
                    }
                });
        this.threshold = Poseidon.getConfig().network.connectionThrottling.threshold;
    }

    public boolean throttle(InetAddress address) {
        if (Poseidon.getConfig().network.connectionThrottling.enabled
                && !address.isLoopbackAddress()
                && !Poseidon.getConfig().network.connectionThrottling.excludedAddresses.contains(address)) {
            int count = this.cache.getUnchecked(address).incrementAndGet();
            return count > this.threshold;
        }
        return false;
    }
}

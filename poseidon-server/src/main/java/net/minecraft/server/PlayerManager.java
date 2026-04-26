package net.minecraft.server;

import com.legacyminecraft.poseidon.util.ChunkPos;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jspecify.annotations.Nullable;

public class PlayerManager {

    public ObjectOpenHashSet<EntityPlayer> managedPlayers = new ObjectOpenHashSet<>(); // Poseidon - ArrayList -> ObjectOpenHashSet
    private Long2ObjectOpenHashMap<PlayerInstance> b = new Long2ObjectOpenHashMap<>(); // Poseidon - PlayerList -> Long2ObjectOpenHashMap
    private ObjectOpenHashSet<PlayerInstance> c = new ObjectOpenHashSet<>(); // Poseidon - ArrayList -> ObjectOpenHashSet
    private MinecraftServer server;
    private int e;
    private int f;
    private final int[][] g = new int[][] { { 1, 0}, { 0, 1}, { -1, 0}, { 0, -1}};

    public PlayerManager(MinecraftServer minecraftserver, int i, int j) {
        if (j > 15) {
            throw new IllegalArgumentException("Too big view radius!");
        } else if (j < 3) {
            throw new IllegalArgumentException("Too small view radius!");
        } else {
            this.f = j;
            this.server = minecraftserver;
            this.e = i;
        }
    }

    public WorldServer a() {
        return this.server.getWorldServer(this.e);
    }

    public void flush() {
        this.c.forEach(playerinstance -> playerinstance.a()); // Poseidon - forEach

        this.c.clear();
    }

    private @Nullable PlayerInstance a(int i, int j, boolean flag) {
        // Poseidon start
        long k = ChunkPos.of(i, j);
        PlayerInstance playerinstance = this.b.get(k);
        // Poseidon end

        if (playerinstance == null && flag) {
            playerinstance = new PlayerInstance(this, i, j);
            this.b.put(k, playerinstance); // Poseidon
        }

        return playerinstance;
    }

    public void flagDirty(int i, int j, int k) {
        int l = i >> 4;
        int i1 = k >> 4;
        PlayerInstance playerinstance = this.a(l, i1, false);

        if (playerinstance != null) {
            playerinstance.a(i & 15, j, k & 15);
        }
    }

    public void addPlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;

        entityplayer.d = entityplayer.locX;
        entityplayer.e = entityplayer.locZ;
        int k = 0;
        int l = this.f;
        int i1 = 0;
        int j1 = 0;

        this.a(i, j, true).a(entityplayer);

        int k1;

        for (k1 = 1; k1 <= l * 2; ++k1) {
            for (int l1 = 0; l1 < 2; ++l1) {
                int[] aint = this.g[k++ % 4];

                for (int i2 = 0; i2 < k1; ++i2) {
                    i1 += aint[0];
                    j1 += aint[1];
                    this.a(i + i1, j + j1, true).a(entityplayer);
                }
            }
        }

        k %= 4;

        for (k1 = 0; k1 < l * 2; ++k1) {
            i1 += this.g[k][0];
            j1 += this.g[k][1];
            this.a(i + i1, j + j1, true).a(entityplayer);
        }

        this.managedPlayers.add(entityplayer);
    }

    public void removePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.d >> 4;
        int j = (int) entityplayer.e >> 4;

        for (int k = i - this.f; k <= i + this.f; ++k) {
            for (int l = j - this.f; l <= j + this.f; ++l) {
                PlayerInstance playerinstance = this.a(k, l, false);

                if (playerinstance != null) {
                    playerinstance.b(entityplayer);
                }
            }
        }

        this.managedPlayers.remove(entityplayer);
    }

    private boolean a(int i, int j, int k, int l) {
        int i1 = i - k;
        int j1 = j - l;

        return i1 >= -this.f && i1 <= this.f && j1 >= -this.f && j1 <= this.f;
    }

    public void movePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;
        double d0 = entityplayer.d - entityplayer.locX;
        double d1 = entityplayer.e - entityplayer.locZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            int k = (int) entityplayer.d >> 4;
            int l = (int) entityplayer.e >> 4;
            int i1 = i - k;
            int j1 = j - l;

            if (i1 != 0 || j1 != 0) {
                for (int k1 = i - this.f; k1 <= i + this.f; ++k1) {
                    for (int l1 = j - this.f; l1 <= j + this.f; ++l1) {
                        if (!this.a(k1, l1, k, l)) {
                            this.a(k1, l1, true).a(entityplayer);
                        }

                        if (!this.a(k1 - i1, l1 - j1, i, j)) {
                            PlayerInstance playerinstance = this.a(k1 - i1, l1 - j1, false);

                            if (playerinstance != null) {
                                playerinstance.b(entityplayer);
                            }
                        }
                    }
                }

                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;

                // Poseidon start - send nearest chunks first
                if (i1 > 1 || i1 < -1 || j1 > 1 || j1 < -1) {
                    final double x = entityplayer.locX;
                    final double z = entityplayer.locZ;

                    entityplayer.chunkCoordIntPairQueue.sort((a, b) -> {
                        double ax = (ChunkPos.x(a) << 4) + 8;
                        double az = (ChunkPos.z(a) << 4) + 8;
                        double bx = (ChunkPos.x(b) << 4) + 8;
                        double bz = (ChunkPos.z(b) << 4) + 8;

                        double da = Math.pow(ax - x, 2) + Math.pow(az - z, 2);
                        double db = Math.pow(bx - x, 2) + Math.pow(bz - z, 2);
                        return Double.compare(da, db);
                    });
                }
                // Poseidon end
            }
        }
    }

    public int getFurthestViewableBlock() {
        return this.f * 16 - 16;
    }

    // Poseidon start
    public boolean a(EntityPlayer entityplayer, int i, int j) {
        PlayerInstance playerchunk = this.a(i, j, false);
        return playerchunk != null
                && PlayerInstance.b(playerchunk).contains(entityplayer)
                && !entityplayer.chunkCoordIntPairQueue.contains(PlayerInstance.a(playerchunk));
    }
    // Poseidon end

    static Long2ObjectOpenHashMap<PlayerInstance> a(PlayerManager playermanager) { // Poseidon - PlayerList -> Long2ObjectOpenHashMap
        return playermanager.b;
    }

    static ObjectOpenHashSet<PlayerInstance> b(PlayerManager playermanager) { // Poseidon - List -> ObjectOpenHashSet
        return playermanager.c;
    }
}

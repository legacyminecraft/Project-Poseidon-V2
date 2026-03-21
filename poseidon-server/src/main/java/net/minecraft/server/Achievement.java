package net.minecraft.server;

import org.jspecify.annotations.Nullable;

public class Achievement extends Statistic {

    public final int a;
    public final int b;
    public final @Nullable Achievement c;
    private final String l;
    public final ItemStack d;
    private boolean m;

    public Achievement(int i, String s, int j, int k, Item item, @Nullable Achievement achievement) {
        this(i, s, j, k, new ItemStack(item), achievement);
    }

    public Achievement(int i, String s, int j, int k, Block block, Achievement achievement) {
        this(i, s, j, k, new ItemStack(block), achievement);
    }

    public Achievement(int i, String s, int j, int k, ItemStack itemstack, @Nullable Achievement achievement) {
        super(5242880 + i, StatisticCollector.a("achievement." + s));
        this.d = itemstack;
        this.l = StatisticCollector.a("achievement." + s + ".desc");
        this.a = j;
        this.b = k;
        if (j < AchievementList.a) {
        	AchievementList.a = j;
        }

        if (k < AchievementList.b) {
        	AchievementList.b = k;
        }

        if (j > AchievementList.c) {
            AchievementList.c = j;
        }

        if (k > AchievementList.d) {
            AchievementList.d = k;
        }

        this.c = achievement;
    }

    public Achievement a() {
        this.g = true;
        return this;
    }

    public Achievement b() {
        this.m = true;
        return this;
    }

    public Achievement c() {
        super.d();
        AchievementList.e.add(this);
        return this;
    }
}

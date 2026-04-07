package com.legacyminecraft.poseidon.config.type;

import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class Duration {

    public static final Serializer SERIALIZER = new Serializer();

    private static final long NANOS_PER_MILLI = 1_000_000;
    private static final long NANOS_PER_SECOND = NANOS_PER_MILLI * 1000;
    private static final long NANOS_PER_MINUTE = NANOS_PER_SECOND * 60;
    private static final long NANOS_PER_HOUR = NANOS_PER_MINUTE * 60;
    private static final long NANOS_PER_DAY = NANOS_PER_HOUR * 24;
    private static final Pattern SPACE = Pattern.compile(" ");
    private static final Pattern NOT_NUMERIC = Pattern.compile("\\D");

    private final long nanos;
    private final String value;

    private Duration(String value) {
        this.nanos = parseDuration(value);
        this.value = value;
    }

    public static Duration of(String duration) {
        return new Duration(duration);
    }

    private static long parseDuration(String duration) {
        String finalDuration = SPACE.matcher(duration).replaceAll("");
        int i = finalDuration.length();
        while (i-- != 0 && NOT_NUMERIC.matcher(String.valueOf(finalDuration.charAt(i))).matches());

        String number = finalDuration.substring(0, i + 1);
        String unit = finalDuration.substring(i + 1);

        long amount;
        try {
            amount = Long.parseUnsignedLong(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration amount '" + number + "': must be a positive integer");
        }

        return switch (unit) {
            case "d" -> amount * NANOS_PER_DAY;
            case "h" -> amount * NANOS_PER_HOUR;
            case "m" -> amount * NANOS_PER_MINUTE;
            case "s" -> amount * NANOS_PER_SECOND;
            case "ms" -> amount * NANOS_PER_MILLI;
            case "ns" -> amount;
            default -> throw new IllegalArgumentException("Invalid time unit '" + unit + "': must be 'd', 'h', 'm', 's', 'ms' or 'ns'");
        };
    }

    public long getNanos() {
        return this.nanos;
    }

    public long getMillis() {
        return this.nanos / NANOS_PER_MILLI;
    }

    public long getSeconds() {
        return this.nanos / NANOS_PER_SECOND;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Duration duration)) return false;
        return this.nanos == duration.nanos && Objects.equals(this.value, duration.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.nanos, this.value);
    }

    @Override
    public String toString() {
        return "Duration{" +
                "nanos=" + this.nanos +
                ", value='" + this.value + '\'' +
                '}';
    }

    public static final class Serializer extends ScalarSerializer<Duration> {
        private Serializer() {
            super(Duration.class);
        }

        @Override
        public Duration deserialize(Type type, Object obj) throws SerializationException {
            try {
                return Duration.of(String.valueOf(obj));
            } catch (IllegalArgumentException e) {
                throw new SerializationException(e);
            }
        }

        @Override
        protected Object serialize(Duration duration, Predicate<Class<?>> typeSupported) {
            return duration.getValue();
        }
    }
}

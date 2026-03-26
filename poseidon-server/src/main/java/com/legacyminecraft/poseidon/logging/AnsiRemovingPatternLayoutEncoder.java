package com.legacyminecraft.poseidon.logging;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

public final class AnsiRemovingPatternLayoutEncoder extends PatternLayoutEncoder {

    private static final Pattern ANSI_PATTERN = Pattern.compile("\\e\\[[\\d;]*[^\\d;]");

    @Override
    public byte[] encode(ILoggingEvent event) {
        Charset charset = getCharset();
        byte[] bytes = super.encode(event);
        String record = charset != null ? new String(bytes, charset) : new String(bytes);

        String ansiless = ANSI_PATTERN.matcher(record).replaceAll("");
        return charset != null ? ansiless.getBytes(charset) : ansiless.getBytes();
    }
}

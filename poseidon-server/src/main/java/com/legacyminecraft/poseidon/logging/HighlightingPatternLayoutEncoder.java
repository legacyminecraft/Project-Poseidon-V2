package com.legacyminecraft.poseidon.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import net.kyori.ansi.ANSIComponentRenderer;
import net.kyori.ansi.StyleOps;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;

public final class HighlightingPatternLayoutEncoder extends PatternLayoutEncoder {

    @Override
    public byte[] encode(ILoggingEvent event) {
        Charset charset = getCharset();
        byte[] bytes = super.encode(event);
        String record = charset != null ? new String(bytes, charset) : new String(bytes);

        ANSIComponentRenderer.ToString<Level> renderer = ANSIComponentRenderer.toString(HighlightStyle.instance);
        Level level = event.getLevel();
        renderer.pushStyle(level);
        renderer.text(record);
        renderer.popStyle(level);
        renderer.complete();

        String highlighted = renderer.asString();
        return charset != null ? highlighted.getBytes(charset) : highlighted.getBytes();
    }

    private static final class HighlightStyle implements StyleOps<Level> {
        private static final HighlightStyle instance = new HighlightStyle();

        @Override
        public int color(Level level) {
            if (level.isGreaterOrEqual(Level.ERROR)) {
                return 0xFF0000;
            } else if (level.isGreaterOrEqual(Level.WARN)) {
                return 0xFFFF00;
            } else {
                return 0xFFFFFF;
            }
        }

        @Override
        public State bold(Level level) {
            if (level.isGreaterOrEqual(Level.WARN)) {
                return State.TRUE;
            } else {
                return State.UNSET;
            }
        }

        @Override
        public State italics(Level level) {
            return State.UNSET;
        }

        @Override
        public State underlined(Level level) {
            return State.UNSET;
        }

        @Override
        public State strikethrough(Level level) {
            return State.UNSET;
        }

        @Override
        public State obfuscated(Level level) {
            return State.UNSET;
        }

        @Override
        public @Nullable String font(Level level) {
            return null;
        }
    }
}

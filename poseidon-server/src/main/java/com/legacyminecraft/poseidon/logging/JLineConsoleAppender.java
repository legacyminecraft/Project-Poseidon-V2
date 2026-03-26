package com.legacyminecraft.poseidon.logging;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.status.ErrorStatus;
import org.jline.reader.LineReader;
import org.jspecify.annotations.Nullable;

import java.nio.charset.StandardCharsets;

public final class JLineConsoleAppender<E> extends UnsynchronizedAppenderBase<E> {

    private @Nullable Encoder<E> encoder;
    private @Nullable LineReader lineReader;

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public void setLineReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    @Override
    public void start() {
        int errors = 0;
        if (this.encoder == null) {
            addStatus(new ErrorStatus("No encoder set for the appender named \"" + this.name + "\".", this));
            errors++;
        }

        if (this.lineReader == null) {
            addStatus(new ErrorStatus("No line reader set for the appender named \"" + this.name + "\".", this));
            errors++;
        }

        if (errors == 0) {
            super.start();
        }
    }

    @Override
    protected void append(E eventObject) {
        byte[] bytes = this.encoder.encode(eventObject);
        this.lineReader.printAbove(new String(bytes, StandardCharsets.UTF_8));
    }
}

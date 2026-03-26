package com.legacyminecraft.poseidon.logging;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import com.legacyminecraft.poseidon.Poseidon;
import net.minecraft.server.MinecraftServer;

import java.nio.charset.StandardCharsets;

public final class ServerLoggingConfigurator extends ContextAwareBase implements Configurator {

    @Override
    public ExecutionStatus configure(LoggerContext context) {
        HighlightingPatternLayoutEncoder consoleEncoder = new HighlightingPatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setCharset(StandardCharsets.UTF_8);
        consoleEncoder.setPattern(Poseidon.config().logging.consolePattern);
        consoleEncoder.start();

        JLineConsoleAppender<ILoggingEvent> consoleAppender = new JLineConsoleAppender<>();
        consoleAppender.setName("console-appender");
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.setLineReader(MinecraftServer.reader);
        consoleAppender.start();

        AnsiRemovingPatternLayoutEncoder fileEncoder = new AnsiRemovingPatternLayoutEncoder();
        fileEncoder.setContext(context);
        fileEncoder.setCharset(StandardCharsets.UTF_8);
        fileEncoder.setPattern(Poseidon.config().logging.filePattern);
        fileEncoder.start();

        FileAppender<ILoggingEvent> fileAppender;
        if (Poseidon.config().logging.rollingLogFile.enabled) {
            TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
            rollingPolicy.setContext(context);
            rollingPolicy.setFileNamePattern(Poseidon.config().logging.rollingLogFile.fileNamePattern);

            RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
            rollingFileAppender.setName("rolling-file-appender");
            rollingFileAppender.setFile(Poseidon.config().logging.rollingLogFile.latestFile);
            rollingFileAppender.setRollingPolicy(rollingPolicy);

            rollingPolicy.setParent(rollingFileAppender);
            rollingPolicy.start();
            fileAppender = rollingFileAppender;
        } else {
            fileAppender = new FileAppender<>();
            fileAppender.setName("file-appender");
            fileAppender.setFile(Poseidon.config().logging.file);
        }

        fileAppender.setContext(context);
        fileAppender.setEncoder(fileEncoder);
        fileAppender.setAppend(true);
        fileAppender.start();

        AsyncAppender asyncConsoleAppender = new AsyncAppender();
        asyncConsoleAppender.setName("async-console-appender");
        asyncConsoleAppender.setContext(context);
        asyncConsoleAppender.setDiscardingThreshold(0);
        asyncConsoleAppender.addAppender(consoleAppender);
        asyncConsoleAppender.start();

        AsyncAppender asyncFileAppender = new AsyncAppender();
        asyncFileAppender.setName("async-file-appender");
        asyncFileAppender.setContext(context);
        asyncFileAppender.setDiscardingThreshold(0);
        asyncFileAppender.addAppender(fileAppender);
        asyncFileAppender.start();

        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.convertAnSLF4JLevel(Poseidon.config().logging.level));
        rootLogger.addAppender(asyncConsoleAppender);
        rootLogger.addAppender(asyncFileAppender);

        return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
    }
}

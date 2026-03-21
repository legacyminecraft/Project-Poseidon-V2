package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ChunkFilenameFilter implements FilenameFilter {

    public static final Pattern a = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");

    private ChunkFilenameFilter() {}

    public boolean accept(File file1, String s) {
        Matcher matcher = a.matcher(s);

        return matcher.matches();
    }

    ChunkFilenameFilter(@Nullable EmptyClass2 emptyclass2) {
        this();
    }
}

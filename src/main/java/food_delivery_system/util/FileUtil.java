package food_delivery_system.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileUtil — abstraction over file I/O. All repositories use it to read/write
 * pipe-delimited records from .txt files. Hides IO details from services.
 */
@Component
public class FileUtil {

    @Value("${foodiego.data.dir:data}")
    private String dataDir;

    public static final String DELIM = "\\|";
    public static final String DELIM_CHAR = "|";

    public Path filePath(String fileName) {
        Path dir = Paths.get(dataDir);
        try {
            if (!Files.exists(dir)) Files.createDirectories(dir);
        } catch (IOException ignored) {}
        Path p = dir.resolve(fileName);
        if (!Files.exists(p)) {
            try { Files.createFile(p); } catch (IOException ignored) {}
        }
        return p;
    }

    /** Read all non-empty lines from a file. */
    public synchronized List<String> readAllLines(String fileName) {
        try {
            return Files.readAllLines(filePath(fileName));
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /** Append a single record line. */
    public synchronized void appendLine(String fileName, String line) {
        try (BufferedWriter w = Files.newBufferedWriter(filePath(fileName),
                StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            w.write(line);
            w.newLine();
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    /** Overwrite the file with the given lines. */
    public synchronized void writeAllLines(String fileName, List<String> lines) {
        try {
            Files.write(filePath(fileName), lines,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    /** Escape a value for pipe-delimited storage. */
    public static String esc(String v) {
        if (v == null) return "";
        return v.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }

    public static String join(Object... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(DELIM_CHAR);
            sb.append(esc(parts[i] == null ? "" : parts[i].toString()));
        }
        return sb.toString();
    }

    public static String[] split(String line) {
        return line.split(DELIM, -1);
    }

    /** Generate a numeric ID based on current time. */
    public static String nextId() {
        return Long.toString(System.currentTimeMillis()) + (int)(Math.random() * 1000);
    }
}

package net.shangkostudio.synccraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class VerifyFileHandler {
    public static void ensureDirectoryExists(Path path) throws IOException {
        Files.createDirectories(path);
    }
}
package web;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serves static files from a directory on the filesystem.
 *
 * Maps request paths to files under the base directory.
 * Resolves "/" to "index.html".
 *
 * Example usage:
 * <pre>
 * Assets assets = new FileAssets(Path.of("frontend/src"));
 * InputStream html = assets.content("/");
 * </pre>
 */
public final class FileAssets implements Assets {

    private final Path base;

    /**
     * Primary constructor.
     *
     * @param base root directory for static files
     */
    public FileAssets(final Path base) {
        this.base = base;
    }

    @Override
    public InputStream content(final String path) throws IOException {
        final String resolved;
        if ("/".equals(path) || path.isEmpty()) {
            resolved = "index.html";
        } else if (path.startsWith("/")) {
            resolved = path.substring(1);
        } else {
            resolved = path;
        }
        return Files.newInputStream(
            this.base.resolve(resolved).normalize()
        );
    }

    @Override
    public String mime(final String path) {
        if (path.endsWith(".html") || "/".equals(path) || path.isEmpty()) {
            return "text/html";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".json")) {
            return "application/json";
        } else {
            return "application/octet-stream";
        }
    }
}

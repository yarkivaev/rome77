package web;

import java.io.IOException;
import java.io.InputStream;

/**
 * Serves static file assets by path.
 *
 * Resolves a request path to file content and MIME type.
 *
 * Example usage:
 * <pre>
 * Assets assets = new FileAssets(basePath);
 * InputStream content = assets.content("/index.html");
 * </pre>
 */
public interface Assets {

    /**
     * Returns the content of the file at the given path.
     *
     * @param path request path relative to the base directory
     * @return file content as InputStream
     * @throws IOException if the file cannot be read
     */
    InputStream content(String path) throws IOException;

    /**
     * Returns the MIME type for the given path.
     *
     * @param path request path
     * @return MIME type string
     */
    String mime(String path);
}

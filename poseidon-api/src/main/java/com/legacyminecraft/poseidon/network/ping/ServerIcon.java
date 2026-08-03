package com.legacyminecraft.poseidon.network.ping;

import com.google.common.base.Preconditions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Objects;

/**
 * Represents a server icon sent as part of the server list ping response.
 * <p>
 * A server icon is always a 64x64 image, represented as a Base64-encoded
 * string prefixed by {@code data:image/png;base64,}.
 */
public final class ServerIcon {

    public static final String PREFIX = "data:image/png;base64,";

    private final String base64String;

    private ServerIcon(String base64String) {
        Preconditions.checkArgument(base64String != null, "base64String cannot be null");
        Preconditions.checkArgument(base64String.startsWith(PREFIX),
                "base64String must start with \"%s\" (found %s)", PREFIX, base64String);

        this.base64String = base64String;
    }

    /**
     * Creates a server icon from a BufferedImage.
     *
     * @param image the image
     * @throws IllegalArgumentException if the image is not 64x64
     * @return the created server icon
     */
    public static ServerIcon create(BufferedImage image) {
        Preconditions.checkArgument(image != null, "image cannot be null");
        Preconditions.checkArgument(image.getWidth() == 64 && image.getHeight() == 64,
                "image must be 64x64 (found %sx%s)", image.getWidth(), image.getHeight());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", bytes);
        } catch (IOException e) {
            throw new RuntimeException("failed to create server icon", e);
        }

        return new ServerIcon(PREFIX + Base64.getEncoder().encodeToString(bytes.toByteArray()));
    }

    /**
     * Loads a server icon from a path.
     *
     * @param path the image path
     * @return the loaded server icon
     * @throws IllegalArgumentException if the image at the path is not 64x64
     * @throws IOException if the file at the specified path is not an image,
     *         or an I/O error occurs
     */
    public static ServerIcon load(Path path) throws IOException {
        try (InputStream input = Files.newInputStream(path)) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IOException("failed to read image");
            }
            return create(image);
        }
    }

    /**
     * Returns the Base64 representation of the server icon.
     *
     * @return the server icon as a Base64 string
     */
    public String asBase64String() {
        return this.base64String;
    }

    @Override
    public String toString() {
        return asBase64String();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerIcon that)) {
            return false;
        }
        return Objects.equals(this.base64String, that.base64String);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.base64String);
    }
}

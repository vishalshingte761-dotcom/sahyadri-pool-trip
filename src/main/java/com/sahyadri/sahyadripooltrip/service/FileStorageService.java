package com.sahyadri.sahyadripooltrip.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String dir) {
        root = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create upload directory", e);
        }
    }

    public String store(MultipartFile file, String folder, long maxBytes, boolean videoAllowed) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("Required document/file is missing");
        if (file.getSize() > maxBytes)
            throw new IllegalArgumentException(
                    "File is too large. Maximum allowed size is " + (maxBytes / (1024 * 1024)) + " MB");
        String type = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename().toLowerCase();
        if ("application/octet-stream".equals(type)) {
            if (originalName.endsWith(".jpg") || originalName.endsWith(".jpeg")) {
                type = "image/jpeg";
            } else if (originalName.endsWith(".png")) {
                type = "image/png";
            } else if (originalName.endsWith(".gif")) {
                type = "image/gif";
            } else if (originalName.endsWith(".webp")) {
                type = "image/webp";
            } else if (originalName.endsWith(".mp4")) {
                type = "video/mp4";
            } else if (originalName.endsWith(".webm")) {
                type = "video/webm";
            } else if (originalName.endsWith(".mov")) {
                type = "video/quicktime";
            }
        }
        Set<String> allowedImages = Set.of(
                "image/jpeg", "image/png", "image/gif", "image/webp");
        Map<String, String> safeExtensions = Map.ofEntries(
                Map.entry("image/jpeg", ".jpg"),
                Map.entry("image/png", ".png"),
                Map.entry("image/gif", ".gif"),
                Map.entry("image/webp", ".webp"),
                Map.entry("application/pdf", ".pdf"),
                Map.entry("video/mp4", ".mp4"),
                Map.entry("video/webm", ".webm"),
                Map.entry("video/quicktime", ".mov"));

        boolean image = allowedImages.contains(type);
        boolean pdf = "application/pdf".equals(type) || (originalName.endsWith(".pdf") && looksLikePdf(file));
        boolean video = Set.of("video/mp4", "video/webm", "video/quicktime").contains(type);

        if (!(image || pdf || (videoAllowed && video)))
            throw new IllegalArgumentException(
                    "Unsupported file type. Upload PDF/image" + (videoAllowed ? "/video" : "") + " only.");

        String ext = safeExtensions.get(type);
        if (ext == null && pdf) {
            ext = ".pdf";
        }
        if (ext == null) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        String stored = UUID.randomUUID() + ext;
        Path dir = root.resolve(folder).normalize();
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(stored).normalize();
            if (!target.startsWith(root))
                throw new IllegalArgumentException("Invalid file path");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return folder + "/" + stored;
        } catch (IOException e) {
            throw new IllegalStateException("File upload failed", e);
        }
    }

    private boolean looksLikePdf(MultipartFile file) {
        try {
            byte[] header = file.getInputStream().readNBytes(5);
            return header.length == 5 && header[0] == '%' && header[1] == 'P' && header[2] == 'D' && header[3] == 'F'
                    && header[4] == '-';
        } catch (IOException e) {
            return false;
        }
    }

    public void delete(String relative) {
        if (relative == null || relative.isBlank()) {
            return;
        }

        Path path = root.resolve(relative).normalize();

        if (!path.startsWith(root)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new IllegalStateException("File cleanup failed", e);
        }
    }

    public byte[] read(String relative) throws IOException {

        if (relative == null || relative.isBlank()) {
            throw new IllegalArgumentException("Document path is required");
        }

        Path p = root.resolve(relative).normalize();

        if (!p.startsWith(root)) {
            throw new NoSuchFileException(relative);
        }

        if (!Files.exists(p) || !Files.isRegularFile(p)) {
            throw new NoSuchFileException(relative);
        }

        return Files.readAllBytes(p);
    }
}

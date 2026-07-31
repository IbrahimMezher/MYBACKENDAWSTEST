package com.flutterbackend.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    public String storeImage(MultipartFile file, String subdir) {
        return store(file, subdir, IMAGE_EXTENSIONS, "Only JPG, PNG, or WEBP images are accepted.", true);
    }

    public String storePdf(MultipartFile file, String subdir) {
        return store(file, subdir, List.of("pdf"), "Only PDF files are accepted.", false);
    }

    private String store(MultipartFile file,
                         String subdir,
                         List<String> allowedExtensions,
                         String invalidMessage,
                         boolean image) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload");
        String ext;
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            ext = original.substring(dot + 1).replaceAll("[^A-Za-z0-9]", "")
                    .toLowerCase(Locale.ROOT);
        } else {
            throw new RuntimeException(invalidMessage);
        }

        if (!allowedExtensions.contains(ext)) {
            throw new RuntimeException(invalidMessage);
        }

        String contentType = file.getContentType() != null
                ? file.getContentType().toLowerCase(Locale.ROOT)
                : "";
        if (image) {
            if (!contentType.startsWith("image/") || !hasImageSignature(file, ext)) {
                throw new RuntimeException(invalidMessage);
            }
        } else if (!"application/pdf".equals(contentType) || !hasPdfSignature(file)) {
            throw new RuntimeException(invalidMessage);
        }

        String safeSubdir = subdir.replace("\\", "/").replaceAll("[^A-Za-z0-9/_-]", "");
        Path dir = root.resolve(safeSubdir).normalize();
        if (!dir.startsWith(root)) {
            throw new RuntimeException("Invalid upload path");
        }

        try {
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + "." + ext;
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + safeSubdir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not save uploaded file");
        }
    }

    private boolean hasPdfSignature(MultipartFile file) {
        byte[] header = readHeader(file, 4);
        return header.length >= 4
                && header[0] == 0x25
                && header[1] == 0x50
                && header[2] == 0x44
                && header[3] == 0x46;
    }

    private boolean hasImageSignature(MultipartFile file, String ext) {
        byte[] header = readHeader(file, 12);
        if (("jpg".equals(ext) || "jpeg".equals(ext)) && header.length >= 3) {
            return (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
        }
        if ("png".equals(ext) && header.length >= 8) {
            return (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47
                    && header[4] == 0x0D
                    && header[5] == 0x0A
                    && header[6] == 0x1A
                    && header[7] == 0x0A;
        }
        if ("webp".equals(ext) && header.length >= 12) {
            return header[0] == 0x52
                    && header[1] == 0x49
                    && header[2] == 0x46
                    && header[3] == 0x46
                    && header[8] == 0x57
                    && header[9] == 0x45
                    && header[10] == 0x42
                    && header[11] == 0x50;
        }
        return false;
    }

    private byte[] readHeader(MultipartFile file, int size) {
        try (InputStream input = file.getInputStream()) {
            return input.readNBytes(size);
        } catch (IOException e) {
            throw new RuntimeException("Could not read uploaded file");
        }
    }
}

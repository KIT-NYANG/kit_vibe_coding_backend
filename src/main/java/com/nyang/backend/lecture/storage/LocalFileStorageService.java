package com.nyang.backend.lecture.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String saveVideo(MultipartFile file) {
        return save(file, "videos");
    }

    @Override
    public String saveThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return save(file, "thumbnails");
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            String relativePath = filePath.replace("/uploads/", "");
            Path targetPath = Paths.get(uploadDir).resolve(relativePath).normalize();
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + filePath);
        }
    }

    private String save(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try {
            Path dirPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(dirPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;

            Path targetPath = dirPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + subDir + "/" + savedFilename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패");
        }
    }
}
package com.nyang.backend.lecture.storage;

import com.nyang.backend.lecture.service.VideoMetadataService;
import com.nyang.backend.lecture.dto.StoredVideoInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final VideoMetadataService videoMetadataService;

    @Override
    public StoredVideoInfo saveVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        try {
            Path dirPath = Paths.get(uploadDir, "videos");
            Files.createDirectories(dirPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;

            Path targetPath = dirPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            int durationSeconds = videoMetadataService.extractDurationSeconds(
                    targetPath.toAbsolutePath().toString()
            );

            String videoPath = "/uploads/videos/" + savedFilename;

            return new StoredVideoInfo(videoPath, durationSeconds);

        } catch (IOException e) {
            throw new RuntimeException("영상 파일 저장 실패", e);
        }
    }

    @Override
    public String saveThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path dirPath = Paths.get(uploadDir, "thumbnails");
            Files.createDirectories(dirPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;

            Path targetPath = dirPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/thumbnails/" + savedFilename;
        } catch (IOException e) {
            throw new RuntimeException("썸네일 파일 저장 실패", e);
        }
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
            throw new RuntimeException("파일 삭제 실패: " + filePath, e);
        }
    }
}
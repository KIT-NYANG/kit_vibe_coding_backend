package com.nyang.backend.lecture.storage;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;
import com.nyang.backend.lecture.dto.StoredVideoInfo;
import com.nyang.backend.lecture.service.VideoMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class GcsFileStorageService implements FileStorageService {

    private final Storage storage;
    private final VideoMetadataService videoMetadataService;

    @Value("${gcp.storage.bucket}")
    private String bucketName;

    @Override
    public StoredVideoInfo saveVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.VIDEO_FILE_REQUIRED);
        }

        File tempFile = null;

        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;
            String objectName = "videos/" + savedFilename;

            tempFile = File.createTempFile("video-", "-" + originalFilename);
            file.transferTo(tempFile);

            int durationSeconds = videoMetadataService.extractDurationSeconds(
                    tempFile.getAbsolutePath()
            );

            BlobInfo blobInfo = BlobInfo.newBuilder(
                            BlobId.of(bucketName, objectName))
                    .setContentType(file.getContentType())
                    .build();

            // 기존 방식보다 이쪽이 더 나음
            storage.createFrom(blobInfo, tempFile.toPath());

            String videoUrl = buildPublicUrl(bucketName, objectName);

            return new StoredVideoInfo(videoUrl, durationSeconds, tempFile);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.VIDEO_SAVE_FAILED);
        }
    }

    @Override
    public String saveThumbnail(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String savedFilename = UUID.randomUUID() + "_" + originalFilename;
            String objectName = "thumbnails/" + savedFilename;

            BlobInfo blobInfo = BlobInfo.newBuilder(
                            BlobId.of(bucketName, objectName))
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            return buildPublicUrl(bucketName, objectName);

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.THUMBNAIL_SAVE_FAILED);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {
            String prefix = "https://storage.googleapis.com/" + bucketName + "/";
            if (!filePath.startsWith(prefix)) {
                return;
            }

            String objectName = filePath.substring(prefix.length());
            storage.delete(bucketName, objectName);

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private String buildPublicUrl(String bucketName, String objectName) {
        return "https://storage.googleapis.com/" +
                bucketName +
                "/" +
                URLEncoder.encode(objectName, StandardCharsets.UTF_8)
                        .replace("+", "%20")
                        .replace("%2F", "/");
    }
}
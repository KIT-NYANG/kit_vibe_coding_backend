package com.nyang.backend.lecture.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveVideo(MultipartFile file);
    String saveThumbnail(MultipartFile file);
    void deleteFile(String filePath);
}
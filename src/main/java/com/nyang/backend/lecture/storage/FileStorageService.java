package com.nyang.backend.lecture.storage;

import com.nyang.backend.lecture.dto.StoredVideoInfo;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredVideoInfo saveVideo(MultipartFile file);
    String saveThumbnail(MultipartFile file);
    void deleteFile(String filePath);
}
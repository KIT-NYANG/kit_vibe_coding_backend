package com.nyang.backend.lecture.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.nyang.backend.global.exception.BusinessException;
import com.nyang.backend.global.exception.ErrorCode;

@Service
public class VideoMetadataService {

    public int extractDurationSeconds(String filePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffprobe",
                    "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    filePath
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line = reader.readLine();
            int exitCode = process.waitFor();

            if (exitCode != 0 || line == null || line.isBlank()) {
                throw new BusinessException(ErrorCode.VIDEO_METADATA_EXTRACT_FAILED);
            }

            double duration = Double.parseDouble(line.trim());
            return (int) Math.ceil(duration);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.VIDEO_METADATA_EXTRACT_FAILED);
        }
    }
}
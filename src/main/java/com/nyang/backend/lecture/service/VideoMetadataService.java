package com.nyang.backend.lecture.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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
                throw new IllegalArgumentException("영상 길이를 추출할 수 없습니다.");
            }

            double duration = Double.parseDouble(line.trim());
            return (int) Math.ceil(duration);
        } catch (Exception e) {
            throw new RuntimeException("영상 메타데이터 추출 실패", e);
        }
    }
}
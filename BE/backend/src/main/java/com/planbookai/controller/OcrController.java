package com.planbookai.controller;

import com.planbookai.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ocr")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @PostMapping("/upload")
    public ResponseEntity<List<Map<String, Object>>> uploadBatch(@RequestParam("files") MultipartFile[] files) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                results.add(Map.of(
                    "file", originalName != null ? originalName : "unknown",
                    "error", "Chỉ hỗ trợ file ảnh"
                ));
                continue;
            }

            try {
                String text = ocrService.processImage(file);
                results.add(Map.of(
                    "file", originalName,
                    "text", text,
                    "status", "success"
                ));
            } catch (Exception e) {
                results.add(Map.of(
                    "file", originalName,
                    "error", "Không thể xử lý file: " + e.getMessage(),
                    "status", "error"
                ));
            }
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/languages")
    public ResponseEntity<List<String>> getSupportedLanguages() {
        return ResponseEntity.ok(List.of("vie", "eng"));
    }
}
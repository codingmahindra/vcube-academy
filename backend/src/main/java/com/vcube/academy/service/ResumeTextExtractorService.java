package com.vcube.academy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ResumeTextExtractorService {

    private static final Pattern CLEAN_PATTERN = Pattern.compile("[\\r\\n\\t]+");

    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();

        try {
            // Check if plain text or markdown
            if (originalFilename != null && (originalFilename.endsWith(".txt") || originalFilename.endsWith(".md"))) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
                return sb.toString().trim();
            }

            // For binary streams (PDF, DOCX, etc.), extract printable UTF-8 / ASCII strings
            byte[] bytes = file.getBytes();
            StringBuilder textBuilder = new StringBuilder();
            StringBuilder currentWord = new StringBuilder();

            for (byte b : bytes) {
                char c = (char) (b & 0xFF);
                if ((c >= 32 && c <= 126) || c == '\n' || c == '\r' || c == '\t') {
                    currentWord.append(c);
                } else {
                    if (currentWord.length() >= 3) {
                        textBuilder.append(currentWord).append(" ");
                    }
                    currentWord.setLength(0);
                }
            }
            if (currentWord.length() >= 3) {
                textBuilder.append(currentWord).append(" ");
            }

            String extracted = textBuilder.toString().replaceAll("(?m)^\\s+$", "").trim();
            return extracted.length() > 50 ? extracted : "Resume document uploaded: " + originalFilename;
        } catch (Exception e) {
            log.error("Failed to extract text from resume file: {}", originalFilename, e);
            return "Resume document uploaded: " + (originalFilename != null ? originalFilename : "resume");
        }
    }
}

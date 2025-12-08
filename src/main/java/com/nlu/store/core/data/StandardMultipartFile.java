package com.nlu.store.core.data;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Standard implementation of {@link MultipartFile} wrapping a Servlet {@link Part}.
 */
public class StandardMultipartFile implements MultipartFile {

    private final Part part;
    private final String filename;

    public StandardMultipartFile(Part part) {
        this.part = part;
        this.filename = part.getSubmittedFileName();
    }

    @Override
    public String getName() {
        return part.getName();
    }

    @Override
    public String getOriginalFilename() {
        return this.filename;
    }

    @Override
    public String getContentType() {
        return part.getContentType();
    }

    @Override
    public long getSize() {
        return part.getSize();
    }

    @Override
    public boolean isEmpty() {
        return this.getSize() == 0 || this.filename == null || this.filename.trim().isEmpty();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return part.getInputStream();
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (InputStream inputStream = part.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        // Sử dụng Files.copy an toàn hơn part.write() vì part.write phụ thuộc vào cấu hình location của Tomcat/Jetty
        try (InputStream inputStream = part.getInputStream()) {
            Files.copy(inputStream, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

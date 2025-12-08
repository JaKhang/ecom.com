package com.nlu.store.core.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A representation of an uploaded file received in a multipart request.
 * <p>
 * This interface decouples the application code from the underlying Servlet API (Part),
 * making it easier to test and manage file uploads.
 */
public interface MultipartFile {

    /**
     * Return the name of the parameter in the multipart form.
     *
     * @return the name of the parameter (never {@code null} or empty).
     */
    String getName();

    /**
     * Return the original filename in the client's filesystem.
     * <p>
     * This may contain path information depending on the browser used,
     * but it typically contains only the file name.
     *
     * @return the original filename, or the empty String if no file was chosen.
     */
    String getOriginalFilename();

    /**
     * Return the content type of the file.
     *
     * @return the content type, or {@code null} if not defined.
     */
    String getContentType();

    /**
     * Return the size of the file in bytes.
     *
     * @return the size of the file, or 0 if empty.
     */
    long getSize();

    /**
     * Return whether the uploaded file is empty.
     *
     * @return true if the file is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Return an InputStream to read the contents of the file.
     * <p>
     * The user is responsible for closing the returned stream.
     *
     * @return the contents of the file as stream.
     * @throws IOException in case of access errors.
     */
    InputStream getInputStream() throws IOException;

    /**
     * Return the contents of the file as an array of bytes.
     *
     * @return the contents of the file as bytes.
     * @throws IOException in case of access errors.
     */
    byte[] getBytes() throws IOException;

    /**
     * Transfer the received file to the given destination file.
     * <p>
     * This may either move the file in the filesystem, copy the file in the
     * filesystem, or save memory-held contents to the destination file.
     *
     * @param dest the destination file (typically absolute path).
     * @throws IOException           in case of reading or writing errors.
     * @throws IllegalStateException if the file has already been moved.
     */
    void transferTo(File dest) throws IOException, IllegalStateException;
}

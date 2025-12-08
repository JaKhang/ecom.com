package com.nlu.store.core.web.bind;

import com.nlu.store.core.data.MultipartFile;

public// DTO cho Multipart Data
class UploadForm {
    private String description;
    private MultipartFile avatar;

    public String getDescription() { return description; }
    public MultipartFile getAvatar() { return avatar; }
}

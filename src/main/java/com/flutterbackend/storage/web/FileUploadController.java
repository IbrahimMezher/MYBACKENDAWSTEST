package com.flutterbackend.storage.web;

import com.flutterbackend.storage.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/uploads")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/public/broker-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadBrokerLogo(@RequestParam("file") MultipartFile file) {
        return Map.of("url", fileStorageService.storeImage(file, "brokers/logos"));
    }

    @PostMapping(value = "/public/broker-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadBrokerDocument(@RequestParam("file") MultipartFile file) {
        return Map.of("url", fileStorageService.storeImage(file, "brokers/documents"));
    }

    @PostMapping(value = "/category-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('customer','broker','admin','superadmin')")
    public Map<String, String> uploadCategoryImage(@RequestParam("file") MultipartFile file) {
        return Map.of("url", fileStorageService.storeImage(file, "category-fields/images"));
    }

    @PostMapping(value = "/category-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('customer','broker','admin','superadmin')")
    public Map<String, String> uploadCategoryDocument(@RequestParam("file") MultipartFile file) {
        return Map.of("url", fileStorageService.storePdf(file, "category-fields/documents"));
    }

    @PostMapping(value = "/policy-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('broker','admin','superadmin')")
    public Map<String, String> uploadPolicyDocument(@RequestParam("file") MultipartFile file) {
        return Map.of("url", fileStorageService.storePdf(file, "policies/documents"));
    }
}

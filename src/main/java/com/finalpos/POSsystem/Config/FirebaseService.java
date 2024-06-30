package com.finalpos.POSsystem.Config;
import org.apache.commons.io.FilenameUtils;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit ;
import com.google.cloud.storage.BlobInfo;

@Service
public class FirebaseService {
    private static final String SERVICE_ACCOUNT = "./src/main/resources/serviceAccountKey.json";

    @Value("${BUCKET_NAME}")
    private String BUCKET_NAME;
    private static Storage storage;

    @PostConstruct
    private void initialize() throws IOException {
        // Reads the configurations from JSON file, then initializes the connection for the specified database
        FileInputStream serviceAccount =
                new FileInputStream(SERVICE_ACCOUNT);

        Credentials credentials = GoogleCredentials.fromStream(serviceAccount);

        storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // Generate a unique name for the image
        String imageName = generateUniqueName(file.getOriginalFilename());

        // Build the BlobId
        BlobId blobId = BlobId.of(BUCKET_NAME, "profile/" + imageName);

        // Upload the image to Firebase Storage
        String extension = FilenameUtils.getExtension(imageName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/" + extension).build();

        storage.create(blobInfo, file.getBytes());

        // Return the URL of the uploaded image
        return getImageUrl("profile/" + imageName);
    }

    private String getImageUrl(String objectPath)  {
        // Get the BlobId for the image
        BlobId blobId = BlobId.of(BUCKET_NAME, objectPath);

        // Get the download URL for the image (100 years)
        return storage.get(blobId).signUrl(36500, TimeUnit.DAYS).toString();
    }

    private String generateUniqueName(String originalFilename) {
        return "image_" + System.currentTimeMillis() + "_" + originalFilename;
    }
}
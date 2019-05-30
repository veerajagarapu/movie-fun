package org.superbiz.moviefun.blobstore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

public class FileStore implements BlobStore {
    @Autowired
    private AmazonS3Client amazonS3client;
    private String photoStorageBucket;
    private final Tika tika = new Tika();

    public FileStore(AmazonS3Client amazonS3client, String photoStorageBucket) {
        this.amazonS3client = amazonS3client;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();
        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            IOUtils.copy(blob.inputStream, outputStream);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File file = new File(name);
        if (!file.exists()) {
            return Optional.empty();
        }
        return Optional.of(new Blob(
                name,
                new FileInputStream(file),
                tika.detect(file)
        ));
    }

    @Override
    public void deleteAll() {
        // ...
    }


}

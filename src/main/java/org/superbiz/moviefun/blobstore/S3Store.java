package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class S3Store implements BlobStore {
    private AmazonS3 amazonS3;
    private String photoStorageBucket;
    private final Tika tika = new Tika();

    public S3Store(AmazonS3 amazonS3, String photoStorageBucket) {
        this.amazonS3 = amazonS3;
        this.photoStorageBucket = photoStorageBucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        amazonS3.putObject(photoStorageBucket, blob.name, blob.inputStream, new ObjectMetadata());
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (!amazonS3.doesObjectExist(photoStorageBucket, name)) {
            return Optional.empty();
        }
        try (S3Object s3Object = amazonS3.getObject(photoStorageBucket, name)) {
            S3ObjectInputStream content = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(content);
            return Optional.of(new Blob(
                    name,
                    new ByteArrayInputStream(bytes),
                    tika.detect(bytes)
            ));
        }
    }

    @Override
    public void deleteAll() {
        List<S3ObjectSummary> summaries = amazonS3
                .listObjects(photoStorageBucket)
                .getObjectSummaries();
        for (S3ObjectSummary summary : summaries) {
            amazonS3.deleteObject(photoStorageBucket, summary.getKey());
        }
    }
}

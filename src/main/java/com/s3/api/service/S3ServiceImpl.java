package com.s3.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

@Service
public class S3ServiceImpl implements IS3Service {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Override
    public String createBucket(String bucketName) {
        CreateBucketResponse response = this.s3Client.createBucket(bucketBuilder -> bucketBuilder.bucket(bucketName));
        return "Bucket creado en la ubicación: " + response.location();
    }

    @Override
    public String checkIfBucketExist(String bucketName) {
        try {
            this.s3Client.headBucket( headBucket -> headBucket.bucket(bucketName) );
            return "El bucket " + bucketName + " si existe.";
        } catch(S3Exception exception){
            return "El bucket " + bucketName + " no existe.";
        }
    }

    @Override
    public List<Bucket> getAllBuckets() {
        ListBucketsResponse bucketsResponse = this.s3Client.listBuckets();

        if(bucketsResponse.hasBuckets()){
            return bucketsResponse.buckets();
        } else {
            return List.of();
        }
    }

    @Override
    public Boolean uploadFile(String bucketName, String key, Path fileLocation) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectResponse putObjectResponse = this.s3Client.putObject(putObjectRequest, fileLocation);

        return putObjectResponse.sdkHttpResponse().isSuccessful();
    }

    @Override
    public void downloadFile(String bucket, String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = this.s3Client.getObjectAsBytes(getObjectRequest);

        File destinationFile = new File("src/main/resources/static");

        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
            fos.write(objectBytes.asByteArray());
            fos.flush();
        } catch (IOException e) {
            throw new IOException("Error al descargar el archivo: " + e.getCause());
        }
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(duration)
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        URL presignedUrl = presignedRequest.url();

        return presignedUrl.toString();
    }

    @Override
    public String generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        URL presignedUrl = presignedRequest.url();

        return presignedUrl.toString();
    }
}

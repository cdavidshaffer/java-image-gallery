package edu.au.cpsc.tools;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

public class S3 {

    public static final Region DEFAULT_REGION = Region.US_EAST_2;

    public List<String> listObjects(String bucketName, Region region) {
        if (region == null)
            region = DEFAULT_REGION;

        // try with resources
        try (S3Client s3 = S3Client.builder()
                .region(region)
                .build()) {

            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Iterable listRes = s3.listObjectsV2Paginator(listReq);
            List<String> result = new ArrayList<>();
            for (S3Object o : listRes.contents()) {
                result.add(o.key());
            }
            return result;
        }
    }

    public byte[] getObject(String bucketName, String key, Region region) {
        if (region == null)
            region = DEFAULT_REGION;

        try (S3Client s3 = S3Client.builder()
                .region(region)
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            ResponseBytes<GetObjectResponse> result = s3.getObject(getObjectRequest, ResponseTransformer.toBytes());
            return result.asByteArray();
        }
    }

    public void putObject(String bucketName, String key, byte[] bytes, Region region) {
        if (region == null)
            region = DEFAULT_REGION;

        try (S3Client s3 = S3Client.builder()
                .region(region)
                .build()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        }
    }

}

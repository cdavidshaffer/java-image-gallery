package edu.au.cpsc;

import edu.au.cpsc.tools.S3;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        String bucketName = "cds-images-private";
        S3 s3 = new S3();
        System.out.println(s3.listObjects(bucketName, null));
        // s3.putObject(bucketName, "monkey", "banana".getBytes(), null);
        byte[] bytes = s3.getObject(bucketName, "monkey", null);
        String value = new String(bytes);
        System.out.println(value);
    }
}

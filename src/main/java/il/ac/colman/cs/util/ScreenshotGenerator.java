package il.ac.colman.cs.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.net.URL;

public class ScreenshotGenerator {

    public static String takeScreenshot(String url) {
        String screenshotFilePath = null;

        AmazonS3 client = AmazonS3ClientBuilder.defaultClient();
        client.putObject("dana-and-ofirs-bucket", "key", new File(url));
        URL objectUrl = client.getUrl("bucket", "key");


        return screenshotFilePath;
    }
}

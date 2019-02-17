package il.ac.colman.cs.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ScreenshotGenerator {

    public static String takeScreenshot(String url) {
        String screenshotUrl = "";
        String screenshotPath = System.getProperty("config.screenshot.path");

        String takeScreenshotCommand = String.format("xvfb-run --server-args=\"-screen 0 1024x768x24\" wkhtmltoimage --format png --crop-w 1024 --crop-h 768 --quiet --quality 60 %s %s", url, screenshotPath);

        try {
            String s;
            System.out.println("Executing command: " + takeScreenshotCommand);
            Process p = Runtime.getRuntime().exec(takeScreenshotCommand);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getErrorStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("Command Executed: " + p.exitValue());

            String bucketName = System.getProperty("config.s3.bucket.name");
            AmazonS3 client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
            System.out.println("Trying to put in bucket...");
            client.putObject(bucketName, url, new File(screenshotPath));
            screenshotUrl = client.getUrl(bucketName, url).toExternalForm();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return screenshotUrl;
    }
}

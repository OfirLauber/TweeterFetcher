package il.ac.colman.cs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.gson.Gson;
import il.ac.colman.cs.util.DataStorage;
import il.ac.colman.cs.util.LinkExtractor;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LinkListener {

    public static void main(String[] args) throws SQLException, InterruptedException, IOException {
        DataStorage dataStorage = new DataStorage();
        LinkExtractor linkExtractor = new LinkExtractor();
        final AmazonSQS client = getAmazonSQSClient();
        final Gson gson = new Gson();
        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        Dimension dimension = new Dimension().withName("SCAN").withValue("milliseconds");

        while (true) {

            // time before scanning
            long startTime = System.nanoTime();

            ReceiveMessageResult result = client.receiveMessage(System.getProperty("config.sqs.url"));

            // time after scanning
            Long endTime = (System.nanoTime() - startTime) / 1000000;

            // send the metric to the cloudWatch
            MetricDatum datum = new MetricDatum()
                    .withMetricName("SITE_SCANNING")
                    .withUnit(StandardUnit.None)
                    .withValue(Double.parseDouble(endTime.toString()))
                    .withDimensions(dimension);

            PutMetricDataRequest request = new PutMetricDataRequest()
                    .withNamespace("DanaAndOfir")
                    .withMetricData(datum);

            PutMetricDataResult response = cw.putMetricData(request);

            List<Message> messages = result.getMessages();
            if (messages.size() == 0) {
                Thread.sleep(5000);
            } else {
                for (Message message : messages) {
                    try {
                        FetchedTweet fetchedTweet = gson.fromJson(message.getBody(), FetchedTweet.class);

                        // time before extracting
                        startTime = System.nanoTime();

                        ExtractedLink extractedLink = linkExtractor.extractContent(fetchedTweet.getUrl());

                        // time after extracting
                        endTime = (System.nanoTime() - startTime) / 1000000;

                        dataStorage.addLink(extractedLink, fetchedTweet.getTrack());

                        dimension = new Dimension().withName("EXTRACT").withValue("millisecondsS");

                        MetricDatum extractingDatum = new MetricDatum()
                                .withMetricName("SCREENSHOTS")
                                .withUnit(StandardUnit.None)
                                .withValue(Double.parseDouble(endTime.toString()))
                                .withDimensions(dimension);

                        PutMetricDataRequest extractingRequest = new PutMetricDataRequest()
                                .withNamespace("DanaAndOfir")
                                .withMetricData(extractingDatum);

                        PutMetricDataResult extractingResponse = cw.putMetricData(extractingRequest);
                    } catch (Exception ex) {
                        // Do nothing
                    }
                }
            }
        }
    }

    private static AmazonSQS getAmazonSQSClient() {
        return AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }
}

package il.ac.colman.cs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.gson.Gson;
import il.ac.colman.cs.util.DataStorage;
import il.ac.colman.cs.util.LinkExtractor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class LinkListener {

    public static void main(String[] args) throws SQLException, InterruptedException, IOException {
        DataStorage dataStorage = new DataStorage();
        LinkExtractor linkExtractor = new LinkExtractor();
        final AmazonSQS client = getAmazonSQSClient();
        final Gson gson = new Gson();

        while (true) {
            ReceiveMessageResult result = client.receiveMessage(System.getProperty("config.sqs.url"));
            List<Message> messages = result.getMessages();
            if (messages.size() == 0) {
                Thread.sleep(5000);
            } else {
                for (Message message : messages) {
                    try {
                        FetchedTweet fetchedTweet = gson.fromJson(message.getBody(), FetchedTweet.class);
                        ExtractedLink extractedLink = linkExtractor.extractContent(fetchedTweet.getUrl());
                        dataStorage.addLink(extractedLink, fetchedTweet.getTrack());
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

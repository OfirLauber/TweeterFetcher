package il.ac.colman.cs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.google.gson.Gson;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterListener {

    public static void main(String[] args) {
        Configuration twitterConfiguration = getTwitterConfiguration();
        TwitterStream twitterStream = getTwitterStream(twitterConfiguration);
        StatusListener listener = getStatusListener();
        twitterStream.addListener(listener);
        twitterStream.filter(System.getProperty("config.twitter.track"));
    }

    private static Configuration getTwitterConfiguration() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(System.getProperty("config.twitter.consumer.key"))
                .setOAuthConsumerSecret(System.getProperty("config.twitter.consumer.secret"))
                .setOAuthAccessToken(System.getProperty("config.twitter.access.token"))
                .setOAuthAccessTokenSecret(System.getProperty("config.twitter.access.secret"));
        return cb.build();
    }

    private static TwitterStream getTwitterStream(Configuration configuration) {
        TwitterStreamFactory tf = new TwitterStreamFactory(configuration);
        return tf.getInstance();
    }

    private static StatusListener getStatusListener() {
        final AmazonSQS client = getAmazonSQSClient();
        final Gson gson = new Gson();

        return new StatusListener() {
            public void onStatus(Status status) {
                URLEntity[] urlEntities = status.getURLEntities();
                if (urlEntities.length != 0) {
                    for (URLEntity urlEntity : urlEntities) {
                        FetchedTweet fetchedTweet = new FetchedTweet(urlEntity.getText(), System.getProperty("config.twitter.track"));
                        client.sendMessage(System.getProperty("config.sqs.url"), gson.toJson(fetchedTweet));
                    }
                }
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            public void onScrubGeo(long l, long l1) {
            }

            public void onStallWarning(StallWarning stallWarning) {
            }
        };
    }

    private static AmazonSQS getAmazonSQSClient() {
        return AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    }
}

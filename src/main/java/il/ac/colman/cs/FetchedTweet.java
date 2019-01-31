package il.ac.colman.cs;

public class FetchedTweet {

    private String url;
    private String track;

    public FetchedTweet(String url, String track) {
        this.url = url;
        this.track = track;
    }

    public String getUrl() {
        return url;
    }

    public String getTrack() {
        return track;
    }
}

package il.ac.colman.cs;

import java.util.Date;

public class ExtractedLink {

    private final String link;
    private final Date date;
    private final String content;
    private final String title;
    private final String description;
    private final String screenshotURL;

    public ExtractedLink(String link, Date date, String content, String title, String description, String screenshotURL) {
        this.link = link;
        this.date = date;
        this.content = content;
        this.title = title;
        this.description = description;
        this.screenshotURL = screenshotURL;
    }

    public String getLink() {
        return link;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getScreenshotURL() {
        return screenshotURL;
    }
}

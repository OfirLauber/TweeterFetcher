package il.ac.colman.cs.util;

import il.ac.colman.cs.ExtractedLink;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;

/**
 * Extract content from links
 */
public class LinkExtractor {

    public ExtractedLink extractContent(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        String content = document.body().text();
        String title;
        try {
            title = document.select("meta[name=title]").get(0).attr("content");
        } catch (Exception ex) {
            title = document.title();
        }
        String description;
        try {
            description = document.select("meta[name=description]").get(0).attr("content");
        } catch (Exception ex) {
            description = "";
        }

        return new ExtractedLink(url, new Date(), content, title, description, ScreenshotGenerator.takeScreenshot(url));
    }
}

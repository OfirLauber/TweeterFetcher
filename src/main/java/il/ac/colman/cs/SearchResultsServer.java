package il.ac.colman.cs;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.colman.cs.util.DataStorage;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SearchResultsServer extends AbstractHandler {

    public static void main(String[] args) throws Exception {
        // Connect to the database
        DataStorage dataStorage = new DataStorage();

        // Start the http server on port 8080
        Server server = new Server(8080);

        server.setHandler(new SearchResultsServer());

        server.start();
        server.join();
    }

    private DataStorage storage;

    SearchResultsServer() throws SQLException {
        storage = new DataStorage();
    }

    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
        Dimension dimension = new Dimension().withName("SEARCH").withValue("milliseconds");

        // Set the content type to JSON
        httpServletResponse.setContentType("application/json;charset=UTF-8");

        // Set the status to 200 OK
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        long startTime = System.nanoTime();

        // Build data from request
        List<ExtractedLink> results = storage.search(httpServletRequest.getParameter("query"));

        long endTime = (System.nanoTime() - startTime) / 1000000;
        Long timeDifference = (endTime - startTime);

        MetricDatum datum = new MetricDatum()
                .withMetricName("SearchResultsServer")
                .withUnit(StandardUnit.None)
                .withValue(Double.parseDouble(timeDifference.toString()))
                .withDimensions(dimension);

        PutMetricDataRequest metRequest = new PutMetricDataRequest()
                .withNamespace("DanaAndOfir")
                .withMetricData(datum);

        PutMetricDataResult response = cw.putMetricData(metRequest);


        // Notify that this request was handled
        request.setHandled(true);

        // Convert data to JSON string and write to output
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(httpServletResponse.getWriter(), results);
    }
}

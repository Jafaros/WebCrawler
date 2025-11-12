package utb.fai;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerTask implements Runnable {
    URIinfo uriInfo;
    private final ConcurrentLinkedDeque<URIinfo> foundURIs;
    private final Set<URI> visitedURIs;
    
    int debugLevel = 0;
    int maxDepth = 5;

    public CrawlerTask(URIinfo uriInfo, ConcurrentLinkedDeque<URIinfo> foundURIs, Set<URI> visitedURIs, int maxDepth, int debugLevel) {
        this.uriInfo = uriInfo;
        this.foundURIs = foundURIs;
        this.visitedURIs = visitedURIs;
        this.maxDepth = maxDepth;
        this.debugLevel = debugLevel;
    }

    @Override
    public void run() {
        // Print pracujícího vlákna pro debug a kontrolu paralelismu
        if (debugLevel > 2) {
            String threadName = Thread.currentThread().getName();
            System.err.println("[" + threadName + "] Processing " + uriInfo.uri);
        }

        try {
            System.err.println("Analyzing " + uriInfo.uri);

            Document doc = Jsoup.connect(uriInfo.uri.toString()).get();

            String text = doc.body().text();
            String[] words = text.split("\\W+");

            WordCounter counter = WordCounter.getInstance();

            for (String word : words) {
                if (word.isEmpty()) continue;

                counter.addWord(word.toLowerCase());
            }

            if (uriInfo.depth < maxDepth) {
                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    String href = link.absUrl("href");

                    if (href.isEmpty()) continue;

                    if (visitedURIs.add(new URI(href))) {
                        foundURIs.add(new URIinfo(href, uriInfo.depth + 1));

                        if (debugLevel > 1) System.err.println("Found link: " + href);
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            if (debugLevel > 0) System.err.println("Error processing " + uriInfo.uri + ": " + e.getMessage());
        }
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.servicecrawler.crawler4j.crawler_logic;

import cloudservices.brokerage.crawler.servicecrawler.crawler4j.configuration.Crawler4jConfig;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class CrawlerController {

    private Crawler4jConfig config;

    public CrawlerController(Crawler4jConfig config) {
        this.config = config;
    }

    public void start() throws Exception {
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(config.getCrawlIntermediateStorage());
        crawlConfig.setPolitenessDelay(config.getPolitenessDelay());
        crawlConfig.setMaxDepthOfCrawling(config.getMaxDepth());
        crawlConfig.setMaxPagesToFetch(config.getMaxPages());
        crawlConfig.setResumableCrawling(config.isResumable());
        crawlConfig.setIncludeBinaryContentInCrawling(config.isBinaryContentCrawling());
        crawlConfig.setIncludeHttpsPages(config.isHttpsCrawling());

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        robotstxtConfig.setEnabled(false); //disable robots being blocked
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */

        for (String domain : config.getCrawlDomains()) {
            controller.addSeed(domain);
        }

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(CustomCrawler.class, config.getNumberOfCrawlers());

    }

    public Crawler4jConfig getConfig() {
        return config;
    }

    public void setConfig(Crawler4jConfig config) {
        this.config = config;
    }
}

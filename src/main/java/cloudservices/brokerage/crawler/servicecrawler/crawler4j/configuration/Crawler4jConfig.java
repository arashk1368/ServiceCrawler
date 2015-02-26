/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.servicecrawler.crawler4j.configuration;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class Crawler4jConfig {

    private final static String DEFAULT_FILTERS = ".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$";
    private final static String DEFAULT_ACCEPTED_OUTDOMAIN_PATTERN = "\\?[wW][sSAa][dD][lL]";
    private final static String DEFAULT_CRAWL_STORAGE = "./data"; //for storing temp data
    private final static int DEFAULT_NUM_CRAWLERS = 10; //number of threads
    private final static int DEFAULT_POLITENESS_DELAY = 500; //ms
    private final static int DEFAULT_MAX_DEPTH = -1; //unlimited
    private final static int DEFAULT_MAX_PAGES = -1; //unlimited
    private final static boolean DEFAULT_RESUMABLE = false;
    private final static boolean DEFAULT_BINARY_CONTENT_CRAWLING = false;
    private final static boolean DEFAULT_HTTPS_CRAWLING = true;
    private Pattern filters;
    private boolean inDomainOnly;
    private String crawlIntermediateStorage;
    private int numberOfCrawlers;
    private int politenessDelay;
    private int maxDepth;
    private int maxPages;
    private boolean resumable;
    private String[] crawlSeeds;
    private boolean binaryContentCrawling;
    private Pattern acceptedOutdomainPattern;
    private boolean httpsCrawling;

    public Crawler4jConfig() {
        this.filters = Pattern.compile(DEFAULT_FILTERS);
        this.inDomainOnly = true;
        this.crawlIntermediateStorage = DEFAULT_CRAWL_STORAGE;
        this.numberOfCrawlers = DEFAULT_NUM_CRAWLERS;
        this.politenessDelay = DEFAULT_POLITENESS_DELAY;
        this.maxDepth = DEFAULT_MAX_DEPTH;
        this.maxPages = DEFAULT_MAX_PAGES;
        this.resumable = DEFAULT_RESUMABLE;
        this.binaryContentCrawling = DEFAULT_BINARY_CONTENT_CRAWLING;
        this.acceptedOutdomainPattern = Pattern.compile(DEFAULT_ACCEPTED_OUTDOMAIN_PATTERN);
        this.httpsCrawling = DEFAULT_HTTPS_CRAWLING;
    }

    public Crawler4jConfig(String[] crawlSeeds) {
        this();
        this.crawlSeeds = crawlSeeds;
    }

    public Crawler4jConfig(Properties prop) throws NumberFormatException{
        this.filters = Pattern.compile(prop.getProperty("filters", DEFAULT_FILTERS));
        this.inDomainOnly = Boolean.parseBoolean(prop.getProperty("inDomainOnly", "true"));
        this.crawlIntermediateStorage = prop.getProperty("crawlIntermediateStorage", DEFAULT_CRAWL_STORAGE);
        this.numberOfCrawlers = Integer.parseInt(prop.getProperty("numberOfCrawlers",
                String.valueOf(DEFAULT_NUM_CRAWLERS)));
        this.politenessDelay = Integer.parseInt(prop.getProperty("politenessDelay",
                String.valueOf(DEFAULT_POLITENESS_DELAY)));
        this.maxDepth = Integer.parseInt(prop.getProperty("maxDepth", String.valueOf(DEFAULT_MAX_DEPTH)));
        this.maxPages = Integer.parseInt(prop.getProperty("maxPages", String.valueOf(DEFAULT_MAX_PAGES)));
        this.resumable = Boolean.parseBoolean(prop.getProperty("resumable", String.valueOf(DEFAULT_RESUMABLE)));
        this.binaryContentCrawling = Boolean.parseBoolean(prop.getProperty("binaryContentCrawling",
                String.valueOf(DEFAULT_BINARY_CONTENT_CRAWLING)));
        this.acceptedOutdomainPattern = Pattern.compile(prop.getProperty("acceptedOutdomainPattern",
                DEFAULT_ACCEPTED_OUTDOMAIN_PATTERN));
        this.httpsCrawling = Boolean.parseBoolean(prop.getProperty("httpsCrawling",
                String.valueOf(DEFAULT_HTTPS_CRAWLING)));
        String[] domains = prop.getProperty("crawlDomains", "").split(",");
        this.crawlSeeds = domains;
    }

    public Pattern getFilters() {
        return filters;
    }

    public void setFilters(Pattern filters) {
        this.filters = filters;
    }

    public boolean isInDomainOnly() {
        return inDomainOnly;
    }

    public void setInDomainOnly(boolean inDomainOnly) {
        this.inDomainOnly = inDomainOnly;
    }

    public String[] getCrawlDomains() {
        return crawlSeeds;
    }

    public void setCrawlDomains(String[] crawlDomains) {
        this.crawlSeeds = crawlDomains;
    }

    public String getCrawlIntermediateStorage() {
        return crawlIntermediateStorage;
    }

    public void setCrawlIntermediateStorage(String crawlIntermediateStorage) {
        this.crawlIntermediateStorage = crawlIntermediateStorage;
    }

    public int getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public void setNumberOfCrawlers(int numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }

    public int getPolitenessDelay() {
        return politenessDelay;
    }

    public void setPolitenessDelay(int politenessDelay) {
        this.politenessDelay = politenessDelay;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    public boolean isResumable() {
        return resumable;
    }

    public void setResumable(boolean resumable) {
        this.resumable = resumable;
    }

    public boolean isBinaryContentCrawling() {
        return binaryContentCrawling;
    }

    public void setBinaryContentCrawling(boolean binaryContentCrawling) {
        this.binaryContentCrawling = binaryContentCrawling;
    }

    public Pattern getAcceptedOutdomainPattern() {
        return acceptedOutdomainPattern;
    }

    public void setAcceptedOutdomainPattern(Pattern acceptedOutdomainPattern) {
        this.acceptedOutdomainPattern = acceptedOutdomainPattern;
    }

    public boolean isHttpsCrawling() {
        return httpsCrawling;
    }

    public void setHttpsCrawling(boolean httpsCrawling) {
        this.httpsCrawling = httpsCrawling;
    }
}

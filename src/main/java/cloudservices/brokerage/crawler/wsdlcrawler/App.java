package cloudservices.brokerage.crawler.wsdlcrawler;

import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.BaseDAO;
import cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.configuration.Crawler4jConfig;
import cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.crawler_logic.CrawlerController;
import cloudservices.brokerage.crawler.wsdlcrawler.utils.properties_utils.PropertiesReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.cfg.Configuration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {

    private final static Logger LOGGER = Logger.getLogger(App.class
            .getName());

    public void crawl() throws Exception {
        Crawler4jConfig config = PropertiesReader.loadCrawler4jConfig(ResourceFileUtil.getResourcePath("crawler4jconfig.properties"));
        CrawlerController controller = new CrawlerController(config);
        long startTime = System.currentTimeMillis();
        LOGGER.log(Level.INFO, "Crawling Start");
        controller.start();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        String msg = "Crawling End in " + totalTime + "ms";
        LOGGER.log(Level.INFO, msg);
    }

    public void crawl2() throws IOException {
        String google = "http://www.google.com/search?q=";
        String search = "service";
        String charset = "UTF-8";
        String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!

        Elements links = Jsoup.connect(google + URLEncoder.encode(search, charset)).userAgent(userAgent).get().select("li.g>h3>a");

        for (Element link : links) {
            String title = link.text();
            String val = link.val();
            String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

            if (!url.startsWith("http")) {
                continue; // Ads/news/etc.
            }

            System.out.println("Title: " + title);
            System.out.println("URL: " + url);
            System.out.println("VALUE: " + val);
        }
    }

    public static void main(String[] args) {
        try {
            LoggerSetup.setup("log.txt", "log.html");
            Logger logger = Logger.getLogger("");
            logger.setLevel(Level.FINER);
        } catch (IOException e) {
            throw new RuntimeException("Problems with creating the log files");
        }
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            BaseDAO.openSession(configuration);
//            App app = new App();
//            app.crawl();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            BaseDAO.closeSession();
        }

    }
}

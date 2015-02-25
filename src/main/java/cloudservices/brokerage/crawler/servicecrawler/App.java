package cloudservices.brokerage.crawler.servicecrawler;

import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.BaseDAO;
import cloudservices.brokerage.crawler.servicecrawler.crawler4j.configuration.Crawler4jConfig;
import cloudservices.brokerage.crawler.servicecrawler.crawler4j.crawler_logic.CrawlerController;
import cloudservices.brokerage.crawler.servicecrawler.utils.properties_utils.PropertiesReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.cfg.Configuration;

public class App {

    private final static Logger LOGGER = Logger.getLogger(App.class.getName());

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

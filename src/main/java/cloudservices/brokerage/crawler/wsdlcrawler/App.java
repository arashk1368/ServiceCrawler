package cloudservices.brokerage.crawler.wsdlcrawler;

import cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.configuration.Crawler4jConfig;
import cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.crawler_logic.CrawlerController;
import cloudservices.brokerage.crawler.wsdlcrawler.utils.logging.LoggerSetup;
import cloudservices.brokerage.crawler.wsdlcrawler.model.DAO.BaseDAO;
import cloudservices.brokerage.crawler.wsdlcrawler.utils.properties_utils.PropertiesReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private final static Logger LOGGER = Logger.getLogger(App.class
            .getName());

    public static void main(String[] args) {
        try {
            LoggerSetup.setup("log.txt", "log.html");
            BaseDAO.openSession();
            
            Crawler4jConfig config = PropertiesReader.loadCrawler4jConfig("crawler4jconfig.properties");
            CrawlerController controller = new CrawlerController(config);
            long startTime = System.currentTimeMillis();
            LOGGER.log(Level.INFO, "Crawling Start");
            controller.start();
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            String msg = "Crawling End in " + totalTime + "ms";
            LOGGER.log(Level.INFO, msg);
        } catch (IOException e) {
            throw new RuntimeException("Problems with creating the log files");
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            BaseDAO.closeSession();
        }

    }
    
}

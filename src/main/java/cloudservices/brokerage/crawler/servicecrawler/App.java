package cloudservices.brokerage.crawler.servicecrawler;

import cloudservices.brokerage.commons.utils.file_utils.DirectoryUtil;
import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.logging.LoggerSetup;
import cloudservices.brokerage.commons.utils.properties_utils.PropertiesWriter;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.BaseDAO;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.v3.ServiceProviderDAO;
import cloudservices.brokerage.crawler.crawlingcommons.model.entities.v3.ServiceProvider;
import cloudservices.brokerage.crawler.servicecrawler.crawler4j.configuration.Crawler4jConfig;
import cloudservices.brokerage.crawler.servicecrawler.crawler4j.crawler_logic.CrawlerController;
import cloudservices.brokerage.crawler.servicecrawler.utils.properties_utils.PropertiesReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.cfg.Configuration;

public class App {

    private final static Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        createLogFile();

        long startTime = System.currentTimeMillis();
        LOGGER.log(Level.SEVERE, "Crawling Start");

        Configuration configuration = new Configuration();
        configuration.configure("v3hibernate.cfg.xml");
        BaseDAO.openSession(configuration);

        try {
            fillSeedDomains(100);
            crawl();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            BaseDAO.closeSession();
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            LOGGER.log(Level.SEVERE, "Crawling End in {0}ms", totalTime);
        }
    }

    public static void crawl() throws Exception {
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

    private static boolean createLogFile() {
        try {
            StringBuilder sb = new StringBuilder();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
            Calendar cal = Calendar.getInstance();
            sb.append(dateFormat.format(cal.getTime()));
            String filename = sb.toString();
            DirectoryUtil.createDir("logs");
            LoggerSetup.setup("logs/" + filename + ".txt", "logs/" + filename + ".html", Level.INFO);
            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    private static void fillSeedDomains(int rank) throws Exception {
        ServiceProviderDAO providerDAO = new ServiceProviderDAO();
        List<ServiceProvider> providers = providerDAO.findTop(rank);
        LOGGER.log(Level.INFO, "{0} seeds from providers", rank);
        String domains = "";
        for (ServiceProvider provider : providers) {
            String url = "http://" + provider.getName();
            LOGGER.log(Level.FINE, "Seed url : {0} found from provider with ID: {1}", new Object[]{url, provider.getId()});
            domains += url + ",";
        }
        if (domains.length() > 0) {
            domains = domains.substring(0, domains.length() - 1);
            String address = ResourceFileUtil.getResourcePath("crawler4jconfig.properties");
            PropertiesWriter.write(address, "crawlDomains", domains);
            LOGGER.log(Level.INFO, "Crawling Seeds: {0}", domains);
        } else {
            throw new Exception("There are no seeds!");
        }
    }
}

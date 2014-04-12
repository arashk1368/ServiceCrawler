/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.crawler_logic;

import cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.configuration.Crawler4jConfig;
import cloudservices.brokerage.crawler.wsdlcrawler.model.DAO.DAOException;
import cloudservices.brokerage.crawler.wsdlcrawler.model.DAO.WSDLDAO;
import cloudservices.brokerage.crawler.wsdlcrawler.model.entities.WSDL;
import cloudservices.brokerage.crawler.wsdlcrawler.utils.properties_utils.PropertiesReader;
import cloudservices.brokerage.crawler.wsdlcrawler.utils.validators.WSDLValidator;
import cloudservices.brokerage.crawler.wsdlcrawler.utils.validators.XMLValidator;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class CustomCrawler extends WebCrawler {

    private final static Logger LOGGER = Logger.getLogger(CustomCrawler.class
            .getName());
    private final Crawler4jConfig config;

    public CustomCrawler() throws IOException, NumberFormatException {
        super();
        this.config = PropertiesReader.loadCrawler4jConfig("crawler4jconfig.properties");
    }

    @Override
    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        String msg = "Try to Visit: " + url;
        LOGGER.log(Level.FINER, msg);
        if (config.isInDomainOnly()) {
            boolean inDomain = false;
            for (String domain : config.getCrawlDomains()) {
                inDomain = inDomain || href.startsWith(domain);
            }
            inDomain = inDomain || config.getAcceptedOutdomainPattern().matcher(href).matches();
            return !config.getFilters().matcher(href).matches() && inDomain;
        } else {
            return !config.getFilters().matcher(href).matches();
        }
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String msg = "Visiting: " + url;
        LOGGER.log(Level.FINE, msg);
        if (XMLValidator.isXML(page.getContentType())) {
            if (WSDLValidator.validateWSDL(page.getContentData())) {
                WSDL wsdl = new WSDL(url);
                WSDLDAO dao = new WSDLDAO();
                try {
                    dao.addWSDL(wsdl);
                    msg = "WSDL SAVED: " + url;
                    LOGGER.log(Level.INFO, msg);
                } catch (DAOException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.servicecrawler.crawler4j.crawler_logic;

import cloudservices.brokerage.commons.utils.file_utils.DirectoryUtil;
import cloudservices.brokerage.commons.utils.file_utils.FileWriter;
import cloudservices.brokerage.commons.utils.file_utils.ResourceFileUtil;
import cloudservices.brokerage.commons.utils.url_utils.URLExtracter;
import cloudservices.brokerage.commons.utils.validators.WADLValidator;
import cloudservices.brokerage.commons.utils.validators.WSDLValidator;
import cloudservices.brokerage.commons.utils.validators.XMLValidator;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.DAOException;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.v3.ServiceDescriptionDAO;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.v3.ServiceDescriptionSnapshotDAO;
import cloudservices.brokerage.crawler.crawlingcommons.model.DAO.v3.ServiceProviderDAO;
import cloudservices.brokerage.crawler.crawlingcommons.model.entities.v3.ServiceDescription;
import cloudservices.brokerage.crawler.crawlingcommons.model.entities.v3.ServiceDescriptionSnapshot;
import cloudservices.brokerage.crawler.crawlingcommons.model.entities.v3.ServiceProvider;
import cloudservices.brokerage.crawler.crawlingcommons.model.enums.v3.ServiceDescriptionType;
import cloudservices.brokerage.crawler.servicecrawler.crawler4j.configuration.Crawler4jConfig;
import cloudservices.brokerage.crawler.servicecrawler.utils.properties_utils.PropertiesReader;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class CustomCrawler extends WebCrawler {

    private final static Logger LOGGER = Logger.getLogger(CustomCrawler.class.getName());
    private final Crawler4jConfig config;
    private final ServiceDescriptionDAO serviceDescDAO;
    private final ServiceProviderDAO providerDAO;
    private final ServiceDescriptionSnapshotDAO snapshotDAO;
    private boolean isNewSD;
    private final static String TOKEN = ";;;";
    private final String withCtxReposAddress = "SnapshotRepository/WithContext/WSDLS/";
    private final String withoutCtxReposAddress = "SnapshotRepository/WithoutContext/WSDLS/";
    private final File wadlSchema;

    public CustomCrawler() throws IOException, NumberFormatException {
        super();
        this.config = PropertiesReader.loadCrawler4jConfig(ResourceFileUtil.getResourcePath("crawler4jconfig.properties"));
        this.serviceDescDAO = new ServiceDescriptionDAO();
        this.providerDAO = new ServiceProviderDAO();
        this.snapshotDAO = new ServiceDescriptionSnapshotDAO();
        this.wadlSchema = new File("wadl.xsd");
    }

    @Override
    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        LOGGER.log(Level.FINE, "Try to Visit: {0}", url);

        if (config.isInDomainOnly()) {
            boolean inDomain = false;
            for (String domain : config.getCrawlDomains()) {
                inDomain = inDomain || href.contains(domain);
            }
            inDomain = inDomain || config.getAcceptedOutdomainPattern().matcher(href).matches();
            LOGGER.log(Level.FINER, "In Domain: {0}", inDomain);
            return !config.getFilters().matcher(href).matches() && inDomain;
        } else {
            return !config.getFilters().matcher(href).matches();
        }
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     *
     * @param page
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        LOGGER.log(Level.FINE, "Visiting: {0}", url);
        isNewSD = false;

        try {
            if (XMLValidator.isXML(page.getContentType())) {
                if (WSDLValidator.validateWSDL(page.getContentData())) {
                    ServiceDescription sd = addOrUpdateService(url, ServiceDescriptionType.WSDL);
                    if (isNewSD) {
                        saveSnapshot(page.getContentData(), sd);
                    }
                } else if (WADLValidator.validateXMLSchema(new ByteArrayInputStream(page.getContentData()), wadlSchema)) {
                    ServiceDescription sd = addOrUpdateService(url, ServiceDescriptionType.WADL);
                    if (isNewSD) {
                        saveSnapshot(page.getContentData(), sd);
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private ServiceDescription addOrUpdateService(String url, ServiceDescriptionType type) throws DAOException {
        ServiceDescription inDB = serviceDescDAO.findByUrl(url);

        if (inDB == null) {
            LOGGER.log(Level.FINER, "There is no service in DB with url : {0}", url);
            ServiceDescription sd = new ServiceDescription();
            sd.setUrl(url);
            sd.setSource("Crawling");
            sd.setUpdated(true);
            sd.setType(type);
            Long id = (Long) serviceDescDAO.save(sd);
            LOGGER.log(Level.FINE, "New service description with ID : {0} saved", id);
            sd.setId(id);
            isNewSD = true;
            return sd;
        } else {
            LOGGER.log(Level.FINE, "Found the same url with ID = {0} in DB, Trying to update", inDB.getId());

            if (!inDB.getSource().contains("Crawling")) {
                String newSource = inDB.getSource().concat(TOKEN).concat("Crawling");
                inDB.setSource(newSource);
                inDB.setUpdated(true);
                LOGGER.log(Level.FINER, "Source updated to {0}", newSource);
            }

            if (inDB.isUpdated()) {
                serviceDescDAO.saveOrUpdate(inDB);
            }

            isNewSD = false;
            return inDB;
        }
    }

    private ServiceProvider createOrUpdateProvider(ServiceDescription sd) throws URISyntaxException, DAOException {
        String name = URLExtracter.getDomainName(sd.getUrl());
        LOGGER.log(Level.FINE, "URL : {0} Name : {1}", new Object[]{sd.getUrl(), name});

        ServiceProvider inDB = providerDAO.findByName(name);
        if (inDB == null) {
            LOGGER.log(Level.FINE, "There is no service provider in DB with Name = {0}, Saving a new one", name);
            ServiceProvider provider = new ServiceProvider();
            provider.setNumberOfServices(1);
            provider.setName(name);
            Long id = (Long) providerDAO.save(provider);
            LOGGER.log(Level.FINE, "Provider with ID : {0} saved", id);
            provider.setId(id);
            return provider;
        } else {
            LOGGER.log(Level.FINE, "Found the same provider name with ID = {0} in DB, Updating", inDB.getId());
            if (isNewSD) {
                inDB.setNumberOfServices(inDB.getNumberOfServices() + 1);
                providerDAO.saveOrUpdate(inDB);
            }
            return inDB;
        }
    }

    private void saveSnapshot(byte[] content, ServiceDescription sd) throws URISyntaxException, DAOException, IOException {
        Date now = new Date();
        ServiceProvider provider = createOrUpdateProvider(sd);
        sd.setServiceProvider(provider);
        sd.setAvailable(true);
        sd.setLastAvailableTime(now);
        serviceDescDAO.saveOrUpdate(sd);

        ServiceDescriptionSnapshot snapshot = new ServiceDescriptionSnapshot();
        snapshot.setAccessedTime(now);
        snapshot.setIsProcessed(false);
        snapshot.setServiceDescription(sd);
        snapshot.setType(sd.getType());
        Long snapshotId = (Long) snapshotDAO.save(snapshot);
        LOGGER.log(Level.INFO, "New snapshot with ID : {0} saved", snapshotId);
        snapshot.setId(snapshotId);

        String providerName = provider.getName();
        LOGGER.log(Level.FINE, "Provider name is used : {0}", providerName);
        String snapDirAddress = getSnapDirAddress(providerName);
        LOGGER.log(Level.FINE, "Snap Directory address : {0}", snapDirAddress);
        String ctxDir = withCtxReposAddress.concat(snapDirAddress);
        String plainDir = withoutCtxReposAddress.concat(snapDirAddress);
        DirectoryUtil.createDirs(ctxDir);
        DirectoryUtil.createDirs(plainDir);
        LOGGER.log(Level.FINER, "Directories created in {0} and in {1}", new Object[]{ctxDir, plainDir});

        String fileName = getSnapFileName(sd.getType(), snapshotId);
        LOGGER.log(Level.FINE, "File name : {0}", fileName);

        String ctxFileAddress = ctxDir.concat(fileName);
        String plainFileAddress = plainDir.concat(fileName);

        String ctx = getContext(sd, providerName);
        LOGGER.log(Level.FINER, "Contex : {0}", ctx);

        InputStream temp = new ByteArrayInputStream(content);

        FileWriter.writeInputStream(temp, new File(ctxFileAddress), ctx);
        LOGGER.log(Level.INFO, "With context created successfully in {0}", ctxFileAddress);

        InputStream temp2 = new ByteArrayInputStream(content);

        FileWriter.writeInputStream(temp2, new File(plainFileAddress));
        LOGGER.log(Level.INFO, "Without context created successfully in {0}", plainFileAddress);

        snapshot.setFileAddress(snapDirAddress.concat(fileName));
        snapshotDAO.saveOrUpdate(snapshot);
    }

    private String getSnapDirAddress(String providerName) {
        StringBuilder sb = new StringBuilder();
        sb.append("Data");
        sb.append("/");
        sb.append(providerName);
        sb.append("/");
        return sb.toString();
    }

    private String getSnapFileName(ServiceDescriptionType type, Long snapshotId) {
        StringBuilder sb = new StringBuilder();
        sb.append(snapshotId);
        sb.append(".");
        switch (type) {
            case REST:
                sb.append("html");
                break;
            case WADL:
                sb.append("wadl");
                break;
            case WSDL:
                sb.append("wsdl");
                break;
        }
        return sb.toString();
    }

    private String getContext(ServiceDescription description, String providerCtx) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!--");
        sb.append("\n");
        sb.append(description.getTitle());
        sb.append("\n");
        sb.append(description.getDescription());
        sb.append("\n");
        sb.append(description.getTags());
        sb.append("\n");
        sb.append(providerCtx);
        sb.append("\n");
        sb.append("-->");
        sb.append("\n");
        return sb.toString();
    }
}

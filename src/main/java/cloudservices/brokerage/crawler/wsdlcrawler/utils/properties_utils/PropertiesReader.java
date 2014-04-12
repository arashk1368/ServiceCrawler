/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.utils.properties_utils;

import cloudservices.brokerage.crawler.wsdlcrawler.App;
import cloudservices.brokerage.crawler.wsdlcrawler.crawler4j.configuration.Crawler4jConfig;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Arash Khodadadi http://www.arashkhodadadi.com/
 */
public class PropertiesReader {

    public static Crawler4jConfig loadCrawler4jConfig(String fileName) throws IOException,NumberFormatException {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName);
            if (input == null) {
                throw new FileNotFoundException("Unable to find " + fileName + " in resources");
            }
            //load a properties file from class path, inside static method
            prop.load(input);
            Crawler4jConfig config = new Crawler4jConfig(prop);
            return config;
        } catch (IOException ex) {
            throw ex;
        } catch (NumberFormatException ex) {
            throw ex;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }
}

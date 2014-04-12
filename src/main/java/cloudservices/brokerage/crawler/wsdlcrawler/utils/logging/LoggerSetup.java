/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.utils.logging;

import cloudservices.brokerage.crawler.wsdlcrawler.utils.formatters.HTMLFormatter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class LoggerSetup {

    private static FileHandler fileTxt;
    private static SimpleFormatter formatterTxt;
    private static FileHandler fileHTML;
    private static Formatter formatterHTML;

    public static void setup(String txtOutputName,String htmlOutputName) throws IOException {

        // Get the global logger to configure it
        Logger logger = Logger.getLogger("");

        logger.setLevel(Level.ALL);
        fileTxt = new FileHandler(txtOutputName);
        fileHTML = new FileHandler(htmlOutputName);

        // create txt Formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        // create HTML Formatter
        formatterHTML = new HTMLFormatter();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
    }
}

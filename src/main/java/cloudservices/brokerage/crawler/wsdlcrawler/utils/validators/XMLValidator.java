/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.utils.validators;

import java.util.regex.Pattern;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class XMLValidator {
    
    public static boolean isXML(String contentType){
        Pattern pttrn = Pattern.compile("^\\S+xml.+$");
        return pttrn.matcher(contentType).matches();
    }
}

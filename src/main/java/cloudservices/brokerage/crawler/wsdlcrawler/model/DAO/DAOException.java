/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.model.DAO;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class DAOException extends Exception {

    public DAOException(String message) {
        super(message);
    }
    
    public DAOException(String message, Throwable cause){
        super(message,cause);
    }
    
}

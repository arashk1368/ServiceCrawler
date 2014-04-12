/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.model.DAO;

import cloudservices.brokerage.crawler.wsdlcrawler.model.entities.WSDL;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class WSDLDAO extends BaseDAO{
    
    public void addWSDL(WSDL wsdl) throws DAOException{
        super.save(wsdl);
    }
}

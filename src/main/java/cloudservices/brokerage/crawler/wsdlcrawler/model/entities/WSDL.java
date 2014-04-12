/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.model.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/*
 * @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */

@Entity
public class WSDL implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String url;

    public WSDL() {
    }

    public WSDL(String url) {
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    
}

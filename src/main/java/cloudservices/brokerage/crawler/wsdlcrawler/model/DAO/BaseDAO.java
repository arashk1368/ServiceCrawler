/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.model.DAO;

import cloudservices.brokerage.crawler.wsdlcrawler.utils.db_utils.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public abstract class BaseDAO {

    private static Session session;

    public BaseDAO() {
    }

    public static void openSession() {
        session = HibernateUtil.getSessionFactory().openSession();
    }

    public static void closeSession() {
        session.close();
        HibernateUtil.getSessionFactory().close();
    }

    protected void startTransaction() throws DAOException {
        if (!getSession().getTransaction().isActive()) {
            getSession().beginTransaction();
        }
    }

    protected void commitTransaction() throws DAOException {
        if (getSession().getTransaction().isActive()) {
            getSession().getTransaction().commit();
        }
    }

    protected Session getSession() throws DAOException {
        if (HibernateUtil.getSessionFactory().isClosed()) {
            throw new DAOException("Session factory already closed");
        } else if (session == null) {
            throw new DAOException("Session is not opened yet");
        } else if (!session.isOpen()) {
            throw new DAOException("Session already closed");
        }
        return session;
    }

    protected List getAll(String className) throws DAOException {
        startTransaction();
        Query query = getSession().createQuery("from " + className);
        List temp = query.list();
        commitTransaction();
        return temp;
    }

    protected Object getById(Long id, Class entity) throws DAOException {
        startTransaction();
        Object temp = getSession().get(entity, id);
        commitTransaction();
        return temp;
    }

    protected Serializable save(Object entity) throws DAOException {
        startTransaction();
        Serializable temp = getSession().save(entity);
        commitTransaction();
        return temp;
    }

    protected void saveOrUpdate(Object entity) throws DAOException {
        startTransaction();
        getSession().saveOrUpdate(entity);
        commitTransaction();
    }
}

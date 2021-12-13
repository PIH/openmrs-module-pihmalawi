package org.openmrs.module.pihmalawi.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.pihmalawi.db.ExtendedPatientDAO;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/*
* This class will handle the actual database calls for Yendanafe patients through Hibernate ORM
* */
@Component
public class HibernateExtendedPatientPatientDAO implements ExtendedPatientDAO {
    /**
     * Hibernate session factory
     */
    private SessionFactory sessionFactory;

    /**
     * Set session factory
     *
     * @param sessionFactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public PatientIdentifier getPatientIdentifierByIdentifier(String identifier) {
        return (PatientIdentifier) sessionFactory.getCurrentSession().createQuery(
                "from PatientIdentifier p where p.identifier = :identifier").setString("identifier", identifier).uniqueResult();
    }

    @Override
    public List<Patient> getPatientsByChangedDate(Date dateChanged) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(Patient.class);
        if ( dateChanged != null ) {
            crit.add(
                    Restrictions.or(
                            Restrictions.and(
                                    Restrictions.isNull("dateChanged"),
                                    Restrictions.ge("dateCreated", dateChanged)
                            ),
                            Restrictions.ge("dateChanged", dateChanged))
                    );

        }
        crit.addOrder(Order.asc("dateChanged"));
        return crit.list();
    }

}

package org.openmrs.module.pihmalawi.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.module.pihmalawi.db.ExtendedPatientDAO;
import org.springframework.stereotype.Component;

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
    public PatientIdentifier getPatientIdentifierByYendaNafeUuid(String uuid) {
        return (PatientIdentifier) sessionFactory.getCurrentSession().createQuery(
                "from PatientIdentifier p where p.identifier = :uuid").setString("uuid", uuid).uniqueResult();
    }
}

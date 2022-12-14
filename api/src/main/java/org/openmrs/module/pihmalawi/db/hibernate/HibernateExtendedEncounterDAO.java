package org.openmrs.module.pihmalawi.db.hibernate;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.module.pihmalawi.db.ExtendedEncounterDAO;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class HibernateExtendedEncounterDAO implements ExtendedEncounterDAO {

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
    public List<Object[]> getDuplicateEncounters(Date startDate, Date endDate, EncounterType encounterType) {

        String sql = "select distinct a.patient_id as 'PID',\n" +
                "                a.encounter_id as 'EncounterId',\n" +
                "                a.encounter_type as 'EncounterTypeId',\n" +
                "                t.name as 'EncounterType',\n" +
                "                a.encounter_datetime as 'EncounterDateTime',\n" +
                "                a.date_created as 'DateCreated',\n" +
                "                CONCAT_WS(' ', n.given_name, n.family_name ) as 'User',\n" +
                "                loc.name as 'Location',\n" +
                "                a.voided as 'Voided'\n" +
                "from encounter a\n" +
                "  inner join users u on a.creator = u.user_id\n" +
                "  inner join person_name n on u.person_id = n.person_id\n" +
                "  inner join encounter_type t on a.encounter_type = t.encounter_type_id\n" +
                "  inner join location loc on a.location_id = loc.location_id\n" +
                "  inner join encounter b on a.patient_id=b.patient_id and a.encounter_type=b.encounter_type and a.encounter_datetime = b.encounter_datetime\n" +
                "where a.encounter_id <> b.encounter_id and a.voided=0 and b.voided = 0\n" +
                "      and a.encounter_datetime > :startDate\n" +
                "      and a.encounter_datetime < :endDate\n" +
                "order by a.patient_id desc, a.encounter_datetime desc;";


        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        //prevent hibernate from flushing before fetching the list
        query.setFlushMode(FlushMode.MANUAL);

        return query.list();
    }

    @Override
    public List<Encounter> getDeletedEncountersByVoidReason(Date startDate, Date endDate, String voidReason) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
        crit.add(Restrictions.eq("voided", true));
        if (startDate != null) {
            crit.add(Restrictions.ge("encounterDatetime", startDate));
        }
        if (endDate != null) {
            crit.add(Restrictions.le("encounterDatetime", endDate));
        }
        if (StringUtils.isNotBlank(voidReason)) {
            crit.add(Restrictions.eq("voidReason", voidReason));
        }
        crit.addOrder(Order.desc("encounterDatetime"));

        return crit.list();
    }


}

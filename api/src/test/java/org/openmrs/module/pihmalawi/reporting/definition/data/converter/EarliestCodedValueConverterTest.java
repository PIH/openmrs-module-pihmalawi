package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.pihmalawi.common.CodedValueAndDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EarliestCodedValueConverterTest {


    @Test
    public void shouldReduceToEarliestDate() {

        Calendar c = Calendar.getInstance();
        c.set(2012, 1, 1);
        Date earlierDate = c.getTime();
        c.set(2013, 1, 1);
        Date anotherEarlierDate = c.getTime();

        Concept concept1 = new Concept(1);
        Concept concept2 = new Concept(2);
        List<CodedValueAndDate> list = new ArrayList<CodedValueAndDate>();

        Obs codedObs1 = new Obs();
        codedObs1.setValueCoded(concept1);

        Obs codedObs2 = new Obs();
        codedObs2.setValueCoded(concept2);

        Obs todayDateObs = new Obs();
        todayDateObs.setValueDatetime(new Date());

        Obs earlierDateObs = new Obs();
        earlierDateObs.setValueDatetime(earlierDate);

        Obs anotherEarlierDateObs = new Obs();
        anotherEarlierDateObs.setValueDatetime(anotherEarlierDate);


        list.add(new CodedValueAndDate(codedObs1, todayDateObs));
        list.add(new CodedValueAndDate(codedObs1, earlierDateObs));
        list.add(new CodedValueAndDate(codedObs2, anotherEarlierDateObs));
        list.add(new CodedValueAndDate(codedObs2, todayDateObs));

        List<CodedValueAndDate> convertedList = (List<CodedValueAndDate>) new EarliestCodedValueConverter().convert(list);
        Assert.assertEquals(2, convertedList.size());
        Assert.assertEquals(concept1, convertedList.get(0).getValue());
        Assert.assertEquals(earlierDate, convertedList.get(0).getDate());
        Assert.assertEquals(concept2, convertedList.get(1).getValue());
        Assert.assertEquals(anotherEarlierDate, convertedList.get(1).getDate());

    }
}

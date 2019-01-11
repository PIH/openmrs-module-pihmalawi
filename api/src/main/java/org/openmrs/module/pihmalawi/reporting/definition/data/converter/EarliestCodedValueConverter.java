package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.openmrs.Concept;
import org.openmrs.module.pihmalawi.common.CodedValueAndDate;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EarliestCodedValueConverter implements DataConverter {

    // given a Collection<CodedValueAndDate>, removes duplicate coded values, keeping the one with the earliest data

    public EarliestCodedValueConverter(){}

    @Override
    public Object convert(Object o) {

        List<CodedValueAndDate> list = (List<CodedValueAndDate>) o;

        // sort by date ascending
        Collections.sort(list, new Comparator<CodedValueAndDate>() {
            @Override
            public int compare(CodedValueAndDate codedValueAndDate1, CodedValueAndDate codedValueAndDate2) {
                return codedValueAndDate1.getDate().compareTo(codedValueAndDate2.getDate());
            }
        });

        // remove duplicates
        Iterator<CodedValueAndDate> i = list.iterator();
        Set<Concept> codedValueSet = new HashSet<Concept>();

        while(i.hasNext()) {
            CodedValueAndDate elem = i.next();
            if (codedValueSet.contains(elem.getValue())) {
                i.remove();
            }
            else {
                codedValueSet.add(elem.getValue());
            }
        }

        return list;
    }

    @Override
    public Class<?> getInputDataType() {
        return Collection.class;
    }

    @Override
    public Class<?> getDataType() {
        return Collection.class;
    }
}

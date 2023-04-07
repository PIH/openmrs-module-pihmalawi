package org.openmrs.module.pihmalawi.validator;

import org.openmrs.module.reporting.common.DateUtil;

public class DateValidator {
    public static Boolean validateDateIsValidFormat(String date){
        try{
            DateUtil.parseYmd(date);
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }
}

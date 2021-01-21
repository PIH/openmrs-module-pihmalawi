package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.module.pihmalawi.reporting.reports.IC3DataEntry;
import org.openmrs.module.reporting.ReportingConstants;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *  End point will handle getting of IC3 Data Entry Report
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + IC3DataEntryRestController.PIHMALAWI + IC3DataEntryRestController.DATA_ENTRY)
public class IC3DataEntryRestController {
    public static final String PIHMALAWI = "/pihmalawi";
    public static final String DATA_ENTRY = "/data-entry";

    @Autowired
    IC3DataEntry ic3DataEntry;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object GetIC3DataEntryReport(
            @RequestParam String date, @RequestParam Integer location)  {

       try
        {
            if(!validateDateIsValidFormat(date))
            {
                SimpleObject message = new SimpleObject();
                message.put("error","given date "+date+ "is not valid");
                return new ResponseEntity<SimpleObject>(message, HttpStatus.BAD_REQUEST);

            }
            ReportManagerUtil.setupReport(ic3DataEntry);
            ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
            ReportUtil.updateGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "en");
            ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(ic3DataEntry.getUuid());
            EvaluationContext context = new EvaluationContext();

            context.addParameterValue("endDate", DateUtil.parseYmd(date));
            context.addParameterValue("location", location);

            ReportData data = reportDefinitionService.evaluate(rd, context);
            List<SimpleObject> traceReportData = new ArrayList<SimpleObject>();
            for (String dsName : data.getDataSets().keySet()) {
                traceReportData.addAll(getTraceReportData(data.getDataSets().get(dsName)));
            }
            return new ResponseEntity<List<SimpleObject>>(traceReportData, HttpStatus.OK);
        }
        catch (Exception ex)
        {
           return new ResponseEntity<String>(ex.getMessage()+ Arrays.toString(ex.getStackTrace()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public Boolean validateDateIsValidFormat(String date)
    {
        try{
            DateUtil.parseYmd(date);
            return true;
        }
        catch(Exception ex)
        {
            return false;
        }
    }
    public List<SimpleObject> getTraceReportData(DataSet d) {
        Iterator iterator = d.iterator();
        List<SimpleObject> dataList = new ArrayList<SimpleObject>();
            while (iterator.hasNext())
            {
                DataSetRow r = (DataSetRow)iterator.next();
                SimpleObject facilityDetails= new SimpleObject();
                facilityDetails.add("identifiers",r.getColumnValue("identifiers"));
                facilityDetails.add("givenName",r.getColumnValue("given_name"));
                facilityDetails.add("familyName",r.getColumnValue("family_name"));
                facilityDetails.add("cameToFacility",r.getColumnValue("came_to_facility"));
                facilityDetails.add("wasThisAppointmentDay",r.getColumnValue("was_this_appointment_day"));
                dataList.add(facilityDetails);
            }
        return dataList;
    }
}
package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.module.pihmalawi.reporting.reports.IC3TraceReport;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + IC3TraceReportRestController.PIHMALAWI + IC3TraceReportRestController.IC3TRACE)
public class IC3TraceReportRestController  {
    public static final String PIHMALAWI = "/pihmalawi";
    public static final String IC3TRACE = "/ic3trace";

    @Autowired
    IC3TraceReport ic3TraceReport;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object GetIC3TraceReport(
            @RequestParam String date)  {

       try
        {
            if(!validateDateIsValidFormat(date))
            {
                SimpleObject message = new SimpleObject();
                message.put("error","given date "+date+ "is not valid");
                return message;
            }
            ReportManagerUtil.setupReport(ic3TraceReport);
            ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
            ReportUtil.updateGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "en");
            ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(ic3TraceReport.getUuid());
            EvaluationContext context = new EvaluationContext();

            context.addParameterValue("endDate", DateUtil.parseYmd(date));

            ReportData data = reportDefinitionService.evaluate(rd, context);
            List<SimpleObject> traceReportData = new ArrayList<SimpleObject>();
            for (String dsName : data.getDataSets().keySet()) {
                System.out.println(dsName);
                System.out.println("---------------------------------");
                traceReportData.add(getTraceReportData(data.getDataSets().get(dsName),dsName));
            }
            return traceReportData;
        }
        catch (Exception ex)
        {
            return ex.getMessage()+ Arrays.toString(ex.getStackTrace());
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
    public SimpleObject getTraceReportData(DataSet d, String dataSetContext) {
        Iterator iterator = d.iterator();
        SimpleObject facility= new SimpleObject();
        List<SimpleObject> dataList = new ArrayList<SimpleObject>();
        StringBuilder stringBuilder = new StringBuilder();
            while (iterator.hasNext())
            {
                DataSetRow r = (DataSetRow)iterator.next();
                SimpleObject facilityDetails= new SimpleObject();
                if(!dataSetContext.equals("6 weeks"))
                {
                    facility.add("location",r.getColumnValue("parameter.location"));
                }
                facility.add("weeks",r.getColumnValue("parameter.minWeeks"));
                facilityDetails.add("end_date",r.getColumnValue("parameter.endDate"));
                facilityDetails.add("lab_weeks",r.getColumnValue("parameter.labWeeks"));
                facilityDetails.add("location",r.getColumnValue("parameter.location"));
                facilityDetails.add("min_weeks",r.getColumnValue("parameter.minWeeks"));
                facilityDetails.add("max_weeks",r.getColumnValue("parameter.maxWeeks"));
                facilityDetails.add("phase_1",r.getColumnValue("parameter.phase1"));
                facilityDetails.add("patient_id",r.getColumnValue("patient_id"));
                facilityDetails.add("village",r.getColumnValue("village"));
                facilityDetails.add("vhw",r.getColumnValue("vhw"));
                facilityDetails.add("first_name",r.getColumnValue("first_name"));
                facilityDetails.add("last_name",r.getColumnValue("last_name"));
                facilityDetails.add("eid_number",r.getColumnValue("eid_number"));
                facilityDetails.add("art_number",r.getColumnValue("art_number"));
                facilityDetails.add("ncd_number",r.getColumnValue("ncd_number"));
                facilityDetails.add("art_last_visit_date",r.getColumnValue("art_last_visit_date"));
                facilityDetails.add("art_last_appt_date",r.getColumnValue("art_last_appt_date"));
                facilityDetails.add("art_weeks_out_of_care",r.getColumnValue("art_weeks_out_of_care"));
                facilityDetails.add("eid_last_visit_date",r.getColumnValue("eid_last_visit_date"));
                facilityDetails.add("eid_last_appt_date",r.getColumnValue("eid_last_appt_date"));
                facilityDetails.add("eid_weeks_out_of_care",r.getColumnValue("eid_weeks_out_of_care"));
                facilityDetails.add("ncd_last_visit_date",r.getColumnValue("ncd_last_visit_date"));
                facilityDetails.add("ncd_last_appt_date",r.getColumnValue("ncd_last_appt_date"));
                facilityDetails.add("ncd_weeks_out_of_care",r.getColumnValue("ncd_weeks_out_of_care"));
                facilityDetails.add("diagnoses",r.getColumnValue("diagnoses"));
                facilityDetails.add("priority_criteria",r.getColumnValue("priority_criteria"));
                facilityDetails.add("trace_criteria",r.getColumnValue("trace_criteria"));

                dataList.add(facilityDetails);
                facility.add("trace_details",dataList);
            }
        return facility;
    }
}

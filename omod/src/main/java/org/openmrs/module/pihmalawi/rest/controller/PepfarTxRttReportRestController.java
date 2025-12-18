package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.module.pihmalawi.reporting.reports.PepfarTxRttReport;
import org.openmrs.module.pihmalawi.validator.DateValidator;
import org.openmrs.module.reporting.ReportingConstants;
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
 *  End point will handle getting of PEPFAR TX RTT Report
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + PepfarTxRttReportRestController.PIHMALAWI + PepfarTxRttReportRestController.REPORT)
public class PepfarTxRttReportRestController {
    public static final String PIHMALAWI = "/pihmalawi";
    public static final String REPORT = "/report/pepfar-tx-rtt";

    @Autowired
    PepfarTxRttReport pepfarTxRttReport;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object GetReport(@RequestParam String startDate,
            @RequestParam String endDate, @RequestParam String location)  {

       try
        {
            if(!DateValidator.validateDateIsValidFormat(startDate))
            {
                SimpleObject message = new SimpleObject();
                message.put("error","given date "+endDate+ "is not valid");
                return new ResponseEntity<SimpleObject>(message, HttpStatus.BAD_REQUEST);

            }
            if(!DateValidator.validateDateIsValidFormat(endDate))
            {
                SimpleObject message = new SimpleObject();
                message.put("error","given date "+endDate+ "is not valid");
                return new ResponseEntity<SimpleObject>(message, HttpStatus.BAD_REQUEST);

            }
            ReportManagerUtil.setupReport(pepfarTxRttReport);
            ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
            ReportUtil.updateGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "en");
            ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(pepfarTxRttReport.getUuid());
            EvaluationContext context = new EvaluationContext();

            context.addParameterValue("startDate", startDate);
            context.addParameterValue("endDate", endDate);
            context.addParameterValue("location", location);

            ReportData data = reportDefinitionService.evaluate(rd, context);
            List<SimpleObject> reportData = new ArrayList<SimpleObject>();
            for (String dsName : data.getDataSets().keySet()) {
                reportData.addAll(getReportData(data.getDataSets().get(dsName)));
            }
            return new ResponseEntity<List<SimpleObject>>(reportData, HttpStatus.OK);
        }
        catch (Exception ex)
        {
           return new ResponseEntity<String>(ex.getMessage()+ Arrays.toString(ex.getStackTrace()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public List<SimpleObject> getReportData(DataSet d) {
        Iterator iterator = d.iterator();
        List<SimpleObject> dataList = new ArrayList<SimpleObject>();
            while (iterator.hasNext())
            {
                DataSetRow r = (DataSetRow)iterator.next();
                SimpleObject reportDetails= new SimpleObject();
                reportDetails.add("sortValue",r.getColumnValue("sort_value"));
                reportDetails.add("ageGroup",r.getColumnValue("age_group"));
                reportDetails.add("gender",r.getColumnValue("gender"));
                reportDetails.add("cd4LessThanTwoHundred",r.getColumnValue("cd4_less_than_two_hundred"));
                reportDetails.add("cd4EqualToOrGreaterThanTwoHundred",r.getColumnValue("cd4_equal_to_or_greater_than_two_hundred"));
                reportDetails.add("cd4UnknownOrNotDone",r.getColumnValue("cd4_unknown_or_not_done"));
                reportDetails.add("notEligibleForCd4",r.getColumnValue("not_eligible_for_cd4"));
                reportDetails.add("returnedLessThanThreeMonths",r.getColumnValue("returned_less_than_three_months"));
                reportDetails.add("returnedThreeToFiveMonths",r.getColumnValue("returned_three_to_five_months"));
                reportDetails.add("returnedSixMonthsPlus",r.getColumnValue("returned_six_months_plus"));
                dataList.add(reportDetails);
            }
        return dataList;
    }
}
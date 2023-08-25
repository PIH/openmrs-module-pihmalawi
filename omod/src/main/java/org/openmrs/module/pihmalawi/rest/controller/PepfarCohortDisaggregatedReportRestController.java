package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.module.pihmalawi.reporting.reports.MOHRegimenDispensationReport;
import org.openmrs.module.pihmalawi.reporting.reports.PepfarCohortDisaggregatedReport;
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
 *  End point will handle getting of PEPFAR Cohort Disaggregated Report
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + PepfarCohortDisaggregatedReportRestController.PIHMALAWI + PepfarCohortDisaggregatedReportRestController.REPORT)
public class PepfarCohortDisaggregatedReportRestController {
    public static final String PIHMALAWI = "/pihmalawi";
    public static final String REPORT = "/report/pepfar-cohort-disaggregated";

    @Autowired
    PepfarCohortDisaggregatedReport pepfarCohortDisaggregatedReport;

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
            ReportManagerUtil.setupReport(pepfarCohortDisaggregatedReport);
            ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
            ReportUtil.updateGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "en");
            ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(pepfarCohortDisaggregatedReport.getUuid());
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
                reportDetails.add("txNew",r.getColumnValue("tx_new"));
                reportDetails.add("txCurr",r.getColumnValue("tx_curr"));
                reportDetails.add("txCurrIpt",r.getColumnValue("tx_curr_ipt"));
                reportDetails.add("txCurrScreenedTb",r.getColumnValue("tx_curr_screened_tb"));
                reportDetails.add("0A",r.getColumnValue("0A"));
                reportDetails.add("2A",r.getColumnValue("2A"));
                reportDetails.add("4A",r.getColumnValue("4A"));
                reportDetails.add("5A",r.getColumnValue("5A"));
                reportDetails.add("6A",r.getColumnValue("6A"));
                reportDetails.add("7A",r.getColumnValue("7A"));
                reportDetails.add("8A",r.getColumnValue("8A"));
                reportDetails.add("9A",r.getColumnValue("9A"));
                reportDetails.add("10A",r.getColumnValue("10A"));
                reportDetails.add("11A",r.getColumnValue("11A"));
                reportDetails.add("12A",r.getColumnValue("12A"));
                reportDetails.add("13A",r.getColumnValue("13A"));
                reportDetails.add("14A",r.getColumnValue("14A"));
                reportDetails.add("15A",r.getColumnValue("15A"));
                reportDetails.add("16A",r.getColumnValue("16A"));
                reportDetails.add("17A",r.getColumnValue("17A"));
                reportDetails.add("0P",r.getColumnValue("0P"));
                reportDetails.add("2P",r.getColumnValue("2P"));
                reportDetails.add("4P",r.getColumnValue("4P"));
                reportDetails.add("4PP",r.getColumnValue("4PP"));
                reportDetails.add("4PA",r.getColumnValue("4PA"));
                reportDetails.add("9P",r.getColumnValue("9P"));
                reportDetails.add("9PP",r.getColumnValue("9PP"));
                reportDetails.add("9PA",r.getColumnValue("9PA"));
                reportDetails.add("9PP",r.getColumnValue("9PP"));
                reportDetails.add("11P",r.getColumnValue("11P"));
                reportDetails.add("11PP",r.getColumnValue("11PP"));
                reportDetails.add("11PA",r.getColumnValue("11PA"));
                reportDetails.add("12PP",r.getColumnValue("12PP"));
                reportDetails.add("12PA",r.getColumnValue("12PA"));
                reportDetails.add("14PP",r.getColumnValue("14PP"));
                reportDetails.add("14PA",r.getColumnValue("14PA"));
                reportDetails.add("15PP",r.getColumnValue("15PP"));
                reportDetails.add("15PA",r.getColumnValue("15PA"));
                reportDetails.add("16P",r.getColumnValue("16P"));
                reportDetails.add("17P",r.getColumnValue("17P"));
                reportDetails.add("17PP",r.getColumnValue("17PP"));
                reportDetails.add("17PA",r.getColumnValue("17PA"));
                reportDetails.add("nonStandard",r.getColumnValue("non_standard"));
                dataList.add(reportDetails);
            }
        return dataList;
    }
}
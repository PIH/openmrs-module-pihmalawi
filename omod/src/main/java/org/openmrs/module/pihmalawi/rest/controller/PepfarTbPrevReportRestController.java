package org.openmrs.module.pihmalawi.rest.controller;

import org.openmrs.module.pihmalawi.reporting.reports.PepfarTbPrevReport;
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
 *  End point will handle getting of Pepfar TB Prev Report
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + PepfarTbPrevReportRestController.PIHMALAWI + PepfarTbPrevReportRestController.REPORT)
public class PepfarTbPrevReportRestController {
    public static final String PIHMALAWI = "/pihmalawi";
    public static final String REPORT = "/report/pepfar-tb-prev";

    @Autowired
    PepfarTbPrevReport pepfarTbPrevReport;

    @Autowired
    ReportDefinitionService reportDefinitionService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object GetReport(@RequestParam String endDate, @RequestParam String location)  {

       try
        {
            if(!DateValidator.validateDateIsValidFormat(endDate))
            {
                SimpleObject message = new SimpleObject();
                message.put("error","given date "+endDate+ "is not valid");
                return new ResponseEntity<SimpleObject>(message, HttpStatus.BAD_REQUEST);

            }
            ReportManagerUtil.setupReport(pepfarTbPrevReport);
            ReportUtil.updateGlobalProperty(ReportingConstants.GLOBAL_PROPERTY_DATA_EVALUATION_BATCH_SIZE, "-1");
            ReportUtil.updateGlobalProperty(ReportingConstants.DEFAULT_LOCALE_GP_NAME, "en");
            ReportDefinition rd = reportDefinitionService.getDefinitionByUuid(pepfarTbPrevReport.getUuid());
            EvaluationContext context = new EvaluationContext();

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
                reportDetails.add("newStartThreeHp",r.getColumnValue("new_start_three_hp"));
                reportDetails.add("newStartSixH",r.getColumnValue("new_start_six_h"));
                reportDetails.add("previousStartThreeHp",r.getColumnValue("previous_start_three_hp"));
                reportDetails.add("previousStartSixH",r.getColumnValue("previous_start_six_h"));
                reportDetails.add("completedNewStartThreeHp",r.getColumnValue("completed_new_start_three_hp"));
                reportDetails.add("completedNewStartSixH",r.getColumnValue("completed_new_start_six_h"));
                reportDetails.add("completedOldThreeHp",r.getColumnValue("completed_old_three_hp"));
                reportDetails.add("completedOldSixH",r.getColumnValue("completed_old_six_h"));
                dataList.add(reportDetails);
            }
        return dataList;
    }
}
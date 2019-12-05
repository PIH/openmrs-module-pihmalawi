<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("pihmalawi", "jquery.flot.min.js")
    ui.includeJavascript("pihmalawi", "jquery.flot.time.min.js")
%>

<script type="text/javascript">
    jq(document).ready( function() {
        jq("#print-form-link").click(function(event) {
            window.print();
        });

        var weightData = [];
        var minWeight = null;
        var maxWeight = null;
        <% weights.each { weight -> %>
            var wt = ${weight.valueNumeric};
            var wtDate = ${dateUtil.getStartOfDay(weight.obsDatetime).getTime()};
            weightData.push([wtDate, wt]);
            minWeight = (minWeight == null || minWeight > wt ? wt : minWeight);
            maxWeight = (maxWeight == null || maxWeight < wt ? wt : maxWeight);
        <% } %>

        jq.plot("#weight-graph", [ { data: weightData, color: "#333" } ], {
            xaxis: { mode: "time", minTickSize: [6, "month"] },
            yaxis: { min: minWeight*0.75, max: maxWeight*1.25 },
            series: {
                lines: { show: true },
                points: { show: true }
            },
            grid: { hoverable: true }
        });

        var seizureData = [];
        var maxNumSeizures = 10;
        var minSeizureTime = null;
        var seizureTimeDiff = 1000*60*60*24*180;

        <% seizures.each { seizure -> %>
            var num = ${seizure.valueNumeric};
            var seizureTime = ${seizure.obsDatetime.getTime()};
            seizureData.push([seizureTime, num]);
            minSeizureTime = (minSeizureTime == null || minSeizureTime > seizureTime ? seizureTime : minSeizureTime);
            maxNumSeizures = (maxNumSeizures == null || maxNumSeizures < num ? num : maxNumSeizures);
        <% } %>

        minSeizureTime=minSeizureTime-seizureTimeDiff;
        maxNumSeizures+=2;

        jq.plot("#seizure-graph", [ { data: seizureData, color: "#333" } ], {
            xaxis: { mode: "time", minTickSize: [6, "month"], min: minSeizureTime, max: (new Date().getTime()+seizureTimeDiff) },
            yaxis: { min: 0, max: maxNumSeizures },
            series: {
                bars: {
                    show: true,
                    fill: true,
                    lineWidth: 5
                },
            },
            grid: {hoverable: true, clickable: true}
        });
    } );
</script>

<style>
    body {
        width: 99%;
        max-width: none;
        font-size: 9pt;
    }
    table td {
        border: none;
        padding: 1px 3px 1px 3px;
    }
    table th {
        padding: 3px;
    }
    table {
        border-collapse: separate;
        width:100%;
        margin: 0 0;
    }
    td {
        vertical-align: top;
        background-color: white;
    }
    .question {
        text-align: right;
        white-space: nowrap;
    }
    .value {
        text-align: left;
        white-space: nowrap;
    }
    #form-actions {
        float: right;
    }
    .form-action-link {
        padding-left: 10px; padding-right:10px;
    }
    #weight-graph {
        width:500px; height:250px;
    }
    .yaxis-label {
        vertical-align: middle;
        -webkit-transform: rotate(270deg);
        -moz-transform: rotate(270deg);
        -ms-transform: rotate(270deg);
        -o-transform: rotate(270deg);
        transform: rotate(270deg);
    }
    .nowrap {
        white-space: nowrap;
    }
    .header-section {
        width:100%; padding:5px;
    }
    .section-divider-top {
        border-bottom: 2px solid black; margin-top:5px; margin-bottom: 5px;
    }
    .top-section-title {
        font-weight: bold;
        font-size: 1.2em;
        padding-left: 10px;
        padding-bottom: 5px;
    }
    .first-column {
        padding:0px 10px 0px 10px; white-space:nowrap; vertical-align:top;
    }
    .second-column {
        padding:0px 75px 0px 25px; white-space:nowrap; vertical-align:top;
    }
    .third-column {
        padding:0px 10px 0px 10px; white-space:nowrap; vertical-align:top; width:100%;
    }
    .detail-table td {
        padding: 0px 20px 0px 0px;
    }
    #identifier-section {
        padding-left:20px;
    }
    #weightsTable {
        white-space: nowrap;
        width: 300px;
    }
    #weightsTable thead {
        display: block
    }
    #weightsTable thead th {
        background: #f3f3f3;
        text-align: left
    }
    #weightsTable tbody {
        display: block;
        height: 200px;
        overflow: auto;
        width: 100%
    }
    #weightsTable tbody td {
        background: #FFF;
        border: 1px solid #f3f3f3;
    }
    #weightsTable thead th {
        width: 175px
    }
    #weightsTable thead th + th {
        width: 137px
    }
    #weightsTable tbody td {
        width: 175px
    }
    #weightsTable tbody td + td {
        width: 191px
    }
    #diagnosisTable {
        white-space: nowrap;
        min-width: 800px;
    }
    #diagnosisDetailTable {
        white-space: nowrap;
    }
    .alert {
        font-weight:bold;
        color:red;
    }
    .toast-container {
        display:none;
    }
    .diagnosisHeading {
        font-weight:bold;
        text-decoration: underline;
    }
    .diagnosisSection {
        border: 1px solid black;
        width: 300px;
    }
    .bpTable {
        width: 100%;
    }
    #seizure-graph {
        height:175px;
        width: 450px;
    }
</style>

<style type="text/css" media="print">
    @page {
        size: portrait;
        margin:.2in;
    }
    .hide-when-printing {
        display: none;
    }
    body {
        font-size: 1em;
    }
    .header-section {
        padding-top:20px; padding-bottom: 20px;
    }
    .section-divider-top {
        padding-top:5px;
        padding-bottom:5px;
    }
    .section-divider-bottom {
        padding-top:5px;
        padding-bottom:5px;
    }
</style>

<div id="printable-summary">

    <div id="form-actions" class="hide-when-printing">
        <a class="form-action-link" id="back-link" href="/${ ui.contextPath() }/patientDashboard.form?patientId=${ patient.id }">
            <i class="icon-chevron-left"></i>
            Return to Patient Dashboard
        </a>
        <a class="form-action-link" id="print-form-link">
            <i class="icon-print"></i>
            ${ ui.message("uicommons.print") }
        </a>
    </div>

    <div class="header-section">
        <span id="name-section" style="font-size:2em;">${ firstName } ${ lastName }</span>
        <span id="identifier-section">(${ccNumber ? ccNumber : arvNumber ? arvNumber : hccNumber ? hccNumber : "?"})
    </div>

    <table>
        <tr>
            <td class="first-column">
                <table>
                    <tr><td>Age</td><td><% if (ageYears > 0) { %>${ ageYears } years<% } else { %>${ ageMonths } months <% } %></td></tr>
                    <tr><td>Gender</td><td>${ gender == 'M' ? "Male" : gender == 'F' ? "Female" : ""}</td></tr>
                    <tr><td>Village</td><td>${village}</td></tr>
                </table>
            </td>
            <td class="second-column">
                <table>
                    <tr>
                        <td>Last height:</td>
                        <td>
                            <% if (height) { %>
                            ${ ui.format(height.valueNumeric) } cm on ${ ui.format(height.obsDatetime) }
                            <% } else { %>
                            <span class="alert">No Height Recorded</span>
                            <% } %>
                        </td>
                    </tr>
                    <tr>
                        <td>Last weight:</td>
                        <td>
                            <% if (weight) { %>
                            ${ weight.valueNumeric } kg on ${ui.format(weight.obsDatetime)}
                            <% } else { %>
                            <span class="alert">No Weight Recorded</span>
                            <% } %>
                        </td>
                    </tr>
                    <tr>
                        <td>Last BMI:</td>
                        <td>
                            <% if (bmi) { %>
                            ${ bmi } kg/m<sup>2</sup>
                            <% } else { %>
                            <span class="alert">N/A</span>
                            <% } %>
                        </td>
                    </tr>
                </table>
            </td>
            <td class="third-column">
                <span class="question">Community Health Worker:</span>
                <! Note: This appears to not work -->
                <span class="value">
                    <% if (chw) { %>
                    ${ chw }
                    <% } else { %>
                    N/A
                    <% } %>
                </span>
            </td>
        </tr>
    </table>

    <div class="section-divider-top"></div>

    <div class="top-section-title">Known Diagnoses</div>

    <table id="diagnosisTable" style="text-align: left;">
        <thead>
            <tr>
                <th></th>
                <th>Diagnosis Date</th>
                <th>First Visit Date</th>
                <th>Last Visit Date</th>
                <th>Next Appt Date</th>
                <th>Program Status</th>
            </tr>
        </thead>
        <tbody>
            <% diagnosisSections.each { diagnosisSection -> %>
                <% if (!diagnosisSection.getRows().isEmpty()) { %>
                    <% diagnosisSection.rows.each { row -> %>
                        <tr>
                            <td>${ui.format(row.diagnosis)}</td>
                            <td>${ui.format(row.diagnosisDate)}</td>
                            <td>${ui.format(diagnosisSection.earliestEncounterDate)}</td>
                            <td>${ui.format(diagnosisSection.latestEncounterDate)}</td>
                            <td>${ui.format(diagnosisSection.nextAppointmentDate)}</td>
                            <td>${ui.format(ccTxStatus)} since ${ui.format(ccTxStatusDate)} </td>
                        </tr>
                    <% } %>
                <% } %>
            <% } %>
            <% if (hivTxStatus) { %>
                <tr>
                    <td>HIV</td>
                    <td>${ui.format(hivEnrollmentDate)}</td>
                    <td>${ui.format(hivFirstVisitDate)}</td>
                    <td>${ui.format(hivLastVisitDate)}</td>
                    <td>${ui.format(artAppointmentStatus?.nextScheduledDate)}</td>
                    <td>${ ui.format(hivTxStatus) } since ${ui.format(hivTxStatusDate)}</td>
                </tr>
            <% } %>
        </tbody>
    </table>

    <div class="section-divider-top"></div>

    <div>
        <table id="diagnosisDetailTable">
        <tr>
            <td>
                <table>
                    <tr>
                        <% int htnDiaCols = 0;
                        if (htnSection != null) {
                            htnDiaCols++; %>
                            ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: htnSection])}
                        <% } %>
                        <% if (diabetesSection != null) {
                            htnDiaCols++; %>
                            ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: diabetesSection])}
                        <% } %>
                        <% if (epilepsySection != null) { %>
                            ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: epilepsySection])}
                        <% } %>
                        <% if (asthmaSection != null) { %>
                            ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: asthmaSection])}
                        <% } %>
                        <% if (mhSection != null) { %>
                            ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: mhSection])}
                        <% } %>
                    </tr>
                </table>
                <br/>
                <table style="width:100%;">
                    <tr>
                        <% if (htnDiaCols > 0) { %>
                        <td style="border:1px solid black; width:50%;" colspan="${htnDiaCols}">
                            <div class="top-section-title">Blood Pressure & Blood Glucose History</div>
                            <div style="height:200px; overflow:auto;">
                                <table class="bpTable">
                                    <thead>
                                        <tr>
                                            <th rowspan="2">Date</th>
                                            <th colspan="2">Blood Pressure</th>
                                            <th colspan="2">Blood Sugar (mg/dl)</th>
                                        </tr>
                                        <tr>
                                            <th>Systol</th>
                                            <th>Diastol</th>
                                            <th>FBS</th>
                                            <th>RBS</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% bpTable.each { row -> %>
                                        <tr>
                                            <td>${ui.format(row.key)}</td>
                                            <td>${ui.format(row.value['sbp'])}</td>
                                            <td>${ui.format(row.value['dbp'])}</td>
                                            <td>${ui.format(row.value['bst']) == 'Fasting' ? ui.format(row.value['bs']) : ""}</td>
                                            <td>${ui.format(row.value['bst']) != 'Fasting' ? ui.format(row.value['bs']) : ""}</td>
                                        </tr>
                                        <% } %>
                                        <% if (bpTable.isEmpty()) { %>
                                            <td colspan="5">None</td>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </td>
                        <% } %>
                        <% if (epilepsySection != null) { %>
                            <td style="border:1px solid black; width:50%" class="nowrap">
                                <div class="top-section-title">Seizure History</div>
                                <div id="seizure-graph"></div>
                            </td>
                        <% } %>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    </div>

    <div class="section-divider-top" style="border:none;">

    <div class="top-section-title">Weight Trend</div>

        <table>
            <tr>
                <td class="nowrap">
                    <table style="width: auto;">
                        <tr>
                            <td rowspan="3" class="yaxis-label">Weight(kg)</td>
                            <td style="text-align:center;">Weights over time</td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>
                                <div id="weight-graph"></div>
                            </td>
                            <td></td>
                        </tr>
                        <tr>
                            <td style="text-align:center;">Time</td>
                            <td></td>
                        </tr>
                    </table>
                </td>
                <td>
                    <table id="weightsTable" border="0" cellpadding="0" cellspacing="0" width="100%">
                        <thead>
                            <tr><th>Date</th><th>Weight (kg)</th></tr>
                        </thead>
                        <tbody>
                        <%
                            if (weights != null && weights.size() > 0) {
                            Collections.reverse(weights);  %>
                            <%  weights.each { weight -> %>
                                <tr>
                                    <td>${ ui.format(dateUtil.getStartOfDay(weight.obsDatetime)) }</td>
                                    <td>${ ui.format(weight.valueNumeric) }</td>
                                </tr>
                            <% } %>
                        <% } else { %>
                            <tr><td colspan="2">None</td></tr>
                        <% } %>
                        </tbody>
                    </table>
                </td>
                <td style="width:100%;"></td>
            </tr>
        </table>

    </div>
</div>

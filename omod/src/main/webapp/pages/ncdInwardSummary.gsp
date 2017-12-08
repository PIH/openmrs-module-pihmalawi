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
            weightData.push([${weight.obsDatetime.getTime()}, wt]);
            minWeight = (minWeight == null || minWeight > wt ? wt : minWeight);
            maxWeight = (maxWeight == null || maxWeight < wt ? wt : maxWeight);
        <% } %>

        jq.plot("#weight-graph", [ { data: weightData, color: "#333" } ], {
            xaxis: { mode: "time" },
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

        // 12 days
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
            xaxis: { mode: "time", min: minSeizureTime, max: (new Date().getTime()+seizureTimeDiff) },
            yaxis: { min: 0, max: maxNumSeizures, ticks: maxNumSeizures },
            series: {
                bars: {
                    show: true,
                    fill: true,
                    lineWidth: 10
                },
            },
            grid: { hoverable: true }
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
    }
    table {
        border-collapse: separate;
        width:unset;
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
        width:100%; padding:10px;
    }
    .section-divider-top {
        border-bottom: 2px solid black; padding-bottom:10px;
    }
    .section-divider-bottom {
        padding-top:10px;
    }
    .top-section-title {
        font-weight: bold;
        font-size: 1.2em;
        padding-bottom: 5px;
    }
    .section-title {
        font-weight: bold;
        font-size: 1.2em;
        padding:0px 10px 10px 10px;
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
        float: left;
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
        float: left;
        width: 800px;
    }
    #diagnosisDetailTable {
        white-space: nowrap;
        float: left;
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
        width: 20%;
    }
    .bpTable {
        width: 100%;
    }
    #seizure-graph {
        width:100%; height:175px;
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
        padding-bottom:20px;
    }
    .section-divider-bottom {
        padding-top:20px;
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
            <td colspan=3><div class="top-section-title">Demographics</div></td>
        </tr>
        <tr>
            <td class="first-column">
                <span class="question">Age:</span>
                <span class="value"><% if (ageYears > 0) { %>${ ageYears } years<% } else { %>${ ageMonths } months <% } %></span> </br>
                <span class="question">Gender:</span>
                <span class="value">${ gender == 'M' ? "Male" : gender == 'F' ? "Female" : ""}</span> </br>
                <span class="question">Village:</span>
                <span class="value">${village}</span> </br>
            </td>
            <td class="second-column">
                <span class="question">Last height:</span>
                <span class="value">
                    <% if (height) { %>
                    ${ ui.format(height.valueNumeric) } cm on ${ ui.format(height.obsDatetime) }
                    <% } else { %>
                    <span class="alert">No Height Recorded</span>
                    <% } %>
                </span></br>
                <span class="question">Last weight:</span>
                <span class="value">
                    <% if (weight) { %>
                    ${ weight.valueNumeric } kg on ${ui.format(weight.obsDatetime)}
                    <% } else { %>
                    <span class="alert">No Weight Recorded</span>
                    <% } %>
                </span> </br>
                <span class="question">Last BMI:</span>
                <span class="value">
                    <% if (bmi) { %>
                    ${ bmi } kg/m<sup>2</sup>
                    <% } else { %>
                    <span class="alert">N/A</span>
                    <% } %>
                </span>
            </td>
            <td class="third-column">
                <span class="question">Village Health Worker:</span>
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
        <tr><td colspan="3" class="section-divider-top"></td></tr>
        <tr>
            <td colspan="3"><div class="top-section-title">Known Diagnoses</div></td>
        <tr>
            <td colspan="3">
                <table id="diagnosisTable" border="0" cellpadding="0" cellspacing="0" width="100%">
                    <thead>
                        <tr>
                            <th></th>
                            <th>Diagnosis Date</th>
                            <th>First Visit Date</th>
                            <th>Last Visit Date</th>
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
                                <td>${ ui.format(hivTxStatus) } since ${ui.format(hivTxStatusDate)}</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
                <br/>
            </td>
        </tr>
    </table>

    <div class="section-divider-top"></div>

    <br/>

    <table id="diagnosisDetailTable">
        <tr>
            <td>
                <table>
                    <tr>
                        <% int htnDiaCols = 0;
                        if (htnSection != null) {
                            htnDiaCols++; %>
                            <td class="diagnosisSection">
                                ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: htnSection])}
                            </td>
                        <% } %>
                        <% if (diabetesSection != null) {
                            htnDiaCols++; %>
                            <td class="diagnosisSection">
                                ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: diabetesSection])}
                            </td>
                        <% } %>
                        <% if (epilepsySection != null) { %>
                            <td class="diagnosisSection">
                                ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: epilepsySection])}
                            </td>
                        <% } %>
                        <% if (asthmaSection != null) { %>
                            <td class="diagnosisSection">
                                ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: asthmaSection])}
                            </td>
                        <% } %>
                        <% if (mhSection != null) { %>
                            <td class="diagnosisSection">
                                ${ ui.includeFragment("pihmalawi", "ncdInwardSummary/diagnosisSection", [section: mhSection])}
                            </td>
                        <% } %>
                    </tr>
                </table>
                <br/>
                <table style="width:100%;">
                    <tr>
                        <% if (htnDiaCols > 0) { %>
                        <td style="border:1px solid black; width:50%;" colspan="${htnDiaCols}">
                            <div class="top-section-title">Blood Pressure & Blood Glucose History</div>
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
                            </table>
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

</table>

    <div>

        <table>
            <tr><td colspan="3" class="section-divider-top"></td></tr>
            <tr>
                <td colspan="3"><div class="top-section-title">Weight Trend</div></td>
            <tr>
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
                                    <td>${ ui.format(weight.obsDatetime) }</td>
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

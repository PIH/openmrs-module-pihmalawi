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
    #weightsTable {
        white-space: nowrap;
        float: left;
        width: 350px
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
        height: 262px;
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
    .alert {
        font-weight:bold;
        color:red;
    }
    .toast-container {
        display:none;
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
        <span id="identifier-section">(${arvNumber ? arvNumber : hccNumber ? hccNumber : ccNumber ? ccNumber : "?"})
    </div>

    <table>

        <tr>
            <td class="first-column">
                <div class="top-section-title">Demographics</div>
                <span class="question">Age:</span>
                <span class="value"><% if (ageYears > 0) { %>${ ageYears } years<% } else { %>${ ageMonths } months <% } %></span>
                <br/>
                <span class="question">Gender:</span>
                <span class="value">${ gender == 'M' ? "Male" : gender == 'F' ? "Female" : ""}</span>
            </td>
            <td class="second-column">
                <div class="top-section-title">Program Enrollments</div>
                <span class="question">HIV:</span>
                <span class="value">
                    <% if (hivTxStatus) { %>
                    ${ hivTxStatus }
                        <% if (hivTxStatusDate) { %>
                            on ${ ui.format(hivTxStatusDate) }
                        <% } %>
                        (${ arvNumber ? arvNumber : hccNumber })
                    <% } else { %>
                    Never Enrolled
                    <% } %>
                </span>
                <br/>
                <span class="question">NCD:</span>
                <span class="value">
                    <% if (ccTxStatus) { %>
                    ${ ccTxStatus } on ${ ui.format(ccTxStatusDate) } ( ${ ccNumber } )
                    <% } else { %>
                    Never Enrolled
                    <% } %>
                </span>
            </td>
            <td class="third-column">
                <div class="top-section-title">ARV Regimens:</div>
                <%  if (artRegimens != null && artRegimens.size() > 0) {
                    Collections.reverse(artRegimens); %>
                    <%  artRegimens.each { regimenObs -> %>
                        <div>${ ui.format(regimenObs.valueCoded) } on ${ ui.format(regimenObs.obsDatetime) }</div>
                    <% } %>
                <% } else { %>
                    <br/>
                    None
                <% } %>
            </td>
        </tr>

        <tr><td colspan="3" class="section-divider-top"></td></tr>
        <tr><td colspan="3" class="section-divider-bottom"></td></tr>
        <tr><td colspan="3" class="section-title">Past visits and appointments</td></tr>

        <tr id="encounters-section">
            <td class="first-column">Last 4 Encounters:</td>
            <td class="second-column">
                <table class="detail-table">
            <%
                    if (encounters != null) {
                    Collections.reverse(encounters);
                    def numToShow = encounters.size() > 4 ? 4 : encounters.size();
                    for (def i=0; i<numToShow; i++) {
                        def encounter = encounters.get(i);
                %>
                    <tr><td>${ ui.format(encounter.encounterDatetime) }</td><td style="width:100%;">${ ui.format(encounter.encounterType) }</td></tr>
            <% }
            } %>
                </table>
            </td>
            <td class="third-column"></td>
        </tr>

        <tr><td colspan="3"></td></tr>

        <tr id="appointments-section">
            <td class="first-column">Appointments:</td>
            <td class="second-column">
                <table class="detail-table">

                    <% for (def programName : appointmentStatuses.keySet()) {
                        def appStatus = appointmentStatuses[programName];
                        if (appStatus.currentlyEnrolled) {
                            def appDate = appStatus.nextScheduledDate;
                            def daysToApp = appStatus.daysToAppointment;
                            def appAlert = (daysToApp == null ? "No appointments scheduled" : (daysToApp < 0 ? "Overdue. Expected " + daysToApp*-1 + " days ago" : ""));
                    %>
                    <tr>
                        <td>${ programName }:</td>
                        <td>${ ui.format(appDate) }</td>
                        <td class="alert third-column">${ appAlert }</td>
                    </tr>
                    <% } %>
                    <% } %>
                </table>
            </td>
            <td class="third-column"></td>
        </tr>

        <tr><td colspan="3" class="section-divider-top"></td></tr>
        <tr><td colspan="3" class="section-divider-bottom"></td></tr>

        <% if (artStartDate != null) { %>
            <tr><td class="section-title" colspan="2">ART Status at Initiation</td></tr>
            <tr>
                <td class="first-column">WHO clinical conditions</td>
                <td class="second-column" colspan="2">${reasonConditions ? reasonConditions : '_____'}</td>
            </tr>
            <tr>
                <td class="first-column">Clinical stage</td>
                <td class="second-column" colspan="2">
                    ${reasonStage ? reasonStage : ''}
                    ${reasonPshd ? (reasonStage ? ' / PSHD' : 'PSHD') : ''}
                </td>
            </tr>
            <tr>
                <td class="first-column">TB Status at Initiation</td>
                <td class="second-column" colspan="2">${reasonTb ? reasonTb : '_____'}</td>
            </tr>
            <tr>
                <td class="first-column">CD4/TLC</td>
                <td class="second-column" colspan="2">
                    ${reasonCd4 ? reasonCd4 : '_____'} / ${reasonCd4Pct ? reasonCd4Pct : '_____'}%
                    &nbsp;&nbsp;
                    Date: ${reasonCd4Date ? ui.format(reasonCd4Date) : '_____'}
                </td>
            </tr>
            <tr>
                <td class="first-column">KS</td>
                <td class="second-column" colspan="2">${reasonKs ? reasonKs : '_____'}</td>
            </tr>
            <tr>
                <td class="first-column">Pregnant/Lactating</td>
                <td class="second-column" colspan="2">${reasonPregnantLactating ? reasonPregnantLactating : '_____'}</td>
            </tr>
            <tr><td colspan="3" class="section-divider-top"></td></tr>
            <tr><td colspan="3" class="section-divider-bottom"></td></tr>
        <% } %>

        <tr><td colspan="3" class="section-title">Most recent clinical data</td></tr>

        <tr>
            <td class="first-column">Last CD4:</td>
            <td class="second-column">
                <% if (cd4s && cd4s.size() > 0) { %>
                    ${ cd4s.get(cd4s.size()-1).valueNumeric } on ${ui.format(cd4s.get(cd4s.size()-1).obsDatetime)}
                <% } else { %>
                    <span class="alert">No CD4 Recorded</span>
                <% } %>
            </td>
            <td class="third-column"></td>
        </tr>

        <tr>
            <td class="first-column">Last Viral Load:</td>
            <td class="second-column">
                <% if (viralLoads && viralLoads.size() > 0) { %>
                ${ viralLoads.get(viralLoads.size()-1).valueNumeric } on ${ui.format(viralLoads.get(viralLoads.size()-1).obsDatetime)}
                <% } else { %>
                <span class="alert">No Viral Load Recorded</span>
                <% } %>
            </td>
            <td class="third-column"></td>
        </tr>

        <tr>
            <td class="first-column">Last TB Status:</td>
            <td class="second-column">
                <% if (tbStatus) { %>
                ${ ui.format(tbStatus.valueCoded) } on ${ ui.format(tbStatus.obsDatetime) }
                <% } else { %>
                <span class="alert">No TB Status Recorded</span>
                <% } %>
            </td>
            <td class="third-column"></td>
        </tr>

        <tr>
            <td class="first-column">Last Height:</td>
            <td class="second-column">
                <% if (height) { %>
                ${ ui.format(height.valueNumeric) } cm on ${ ui.format(height.obsDatetime) }
                <% } else { %>
                <span class="alert">No Height Recorded</span>
                <% } %>
            </td>
            <td class="third-column"></td>
        </tr>

        <tr>
            <td class="first-column">Last Weight:</td>
            <td class="second-column">
                <% if (lastWeight) { %>
                ${ lastWeight.valueNumeric } kg on ${ui.format(lastWeight.obsDatetime)}
                <% } else { %>
                <span class="alert">No Weight Recorded</span>
                <% } %>
            </td>
            <td class="third-column alert">
                <%
                    if (lastWeight && oneYearWeight && lastWeight.valueNumeric && oneYearWeight.valueNumeric) {
                        def weightChange = lastWeight.valueNumeric - oneYearWeight.valueNumeric;
                        def percentChange = weightChange * 100 / oneYearWeight.valueNumeric;
                        if (percentChange <= -10) { %>
                            Alert:  One year weight loss of ${ Math.round(Math.abs(percentChange)) }%
                <%      }
                    }
                %>
            </td>
        </tr>

        <tr>
            <td class="first-column">Last BMI:</td>
            <td class="second-column">
                <% if (bmi) { %>
                ${ bmi } kg/m<sup>2</sup>
                <% } else { %>
                <span class="alert">N/A</span>
                <% } %>
            </td>
            <td class="third-column alert">
                <% if (bmiValue && bmiValue < 19) { %>
                    Alert:  BMI &lt; 19
                <% } %>
            </td>
        </tr>

        <tr><td colspan="3" class="section-divider-top"></td></tr>
        <tr><td colspan="3" class="section-divider-bottom"></td></tr>

    </table>

    <div>

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

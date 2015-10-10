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

        jq.plot("#weight-graph", [ weightData ], {
            xaxis: { mode: "time" },
            yaxis: { min: minWeight*0.75, max: maxWeight*1.25 },
            series: {
                lines: { show: true },
                points: { show: true },
            },
            grid: { hoverable: true }
        });
    } );
</script>

<style>
    body {
        width: 99%;
        max-width: none;
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
    .section {
        border-bottom: 2px solid black; padding:10px;
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
</style>

<style type="text/css" media="print">
    @page {
        size: landscape;
        margin:.2in;
    }
    .hide-when-printing {
        display: none;
    }
    body {
        font-size: .6em;
    }
    #weight-graph {

    }
</style>

<div id="printable-summary">

    <div id="form-actions" class="hide-when-printing">
        <a class="form-action-link" id="print-form-link">
            <i class="icon-print"></i>
            ${ ui.message("uicommons.print") }
        </a>
    </div>

    <table class="section">
        <tr>
            <td>
                <table id="header-table">
                    <tr>
                        <td colspan="3" style="width:70%;">
                            <span id="name-section" style="font-size:2em;">${ firstName } ${ lastName }</span>
                            <span id="identifier-section">( ${arvNumber ? arvNumber : hccNumber ? hccNumber : ccNumber ? ccNumber : "?"} )
                        </td>
                        <td rowspan="2" style="width:20%; vertical-align: bottom;">
                            ARV Regimens:
                            <% if (artRegimens.size() > 0) { %>
                                <%  artRegimens.each { regimenObs -> %>
                                    <br/>
                                    ${ ui.format(regimenObs.valueCoded) } on ${ ui.format(regimenObs.obsDatetime) }
                                <% } %>
                            <% } else { %>
                                <br/>
                                None
                            <% } %>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <table>
                                <tr>
                                    <td class="question">Age:</td>
                                    <td class="value"><% if (ageYears > 0) { %>${ ageYears } years<% } else { %>${ ageMonths } months <% } %></td>
                                </tr>
                                <tr>
                                    <td class="question">Gender:</td>
                                    <td class="value">${ gender == 'M' ? "Male" : gender == 'F' ? "Female" : ""}</td>
                                </tr>
                            </table>
                        </td>
                        <td>
                            <table>
                                <tr>
                                    <td class="question">HIV Program Status:</td>
                                    <td class="value">
                                        <% if (hivTxStatus) { %>
                                        ${ hivTxStatus } on ${ ui.format(hivTxStatusDate) } ( ${ arvNumber ? arvNumber : hccNumber } )
                                        <% } else { %>
                                        Never Enrolled
                                        <% } %>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="question">NCD Program Status:</td>
                                    <td class="value">
                                        <% if (ccTxStatus) { %>
                                        ${ ccTxStatus } on ${ ui.format(ccTxStatusDate) } ( ${ ccNumber } )
                                        <% } else { %>
                                        Never Enrolled
                                        <% } %>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
    </table>

    <table class="section">

        <tr id="encounters-section">
            <td class="question">Last 4 Encounters:</td>
            <td class="value" style="width:100%;">
            <%
                Collections.reverse(encounters);
                def numToShow = encounters.size() > 4 ? 4 : encounters.size();
                for (def i=0; i<numToShow; i++) {
                    def encounter = encounters.get(i);
            %>
                ${ ui.format(encounter.encounterDatetime) } - ${ ui.format(encounter.encounterType) }<br/>
            <% } %>
            </td>
        </tr>
        <tr id="appointments-section">

            <td class="question">Appointments:</td>
            <td class="value" style="width:100%;">

            <% for (def programName : appointmentStatuses.keySet()) { %>
                <% if (appointmentStatuses[programName].currentlyEnrolled) { %>
                    ${ programName }:
                    <% if (appointmentStatuses[programName].daysToAppointment == null) { %>
                    <span class="alert">No appointments scheduled</span>
                    <% } else if (appointmentStatuses[programName].daysToAppointment >= 0) { %>
                    ${ ui.format(appointmentStatuses[programName].nextScheduledDate) } ( in ${ appointmentStatuses[programName].daysToAppointment } days )
                    <% } else { %>
                    <span class="alert">Overdue. Expected on ${ ui.format(appointmentStatuses[programName].nextScheduledDate) } ( in ${ appointmentStatuses[programName].daysToAppointment*-1 } days ago</span>
                    <% } %>
                    <br/>
                <% } %>
            <% } %>
            </td>
        </tr>
    </table>

    <table class="section">

        <tr>
            <td class="question">Last CD4:</td>
            <td class="value" style="width:100%;">
                <% if (cd4s && cd4s.size() > 0) { %>
                    ${ cd4s.get(cd4s.size()-1).valueNumeric } on ${ui.format(cd4s.get(cd4s.size()-1).obsDatetime)}
                <% } else { %>
                    <span class="alert">No CD4 Recorded</span>
                <% } %>
            </td>
        </tr>

        <tr>
            <td class="question">Last Viral Load:</td>
            <td class="value" style="width:100%;">
                <% if (viralLoads && viralLoads.size() > 0) { %>
                ${ viralLoads.get(viralLoads.size()-1).valueNumeric } on ${ui.format(viralLoads.get(viralLoads.size()-1).obsDatetime)}
                <% } else { %>
                <span class="alert">No Viral Load Recorded</span>
                <% } %>
            </td>
        </tr>

        <tr>
            <td class="question">TB Status:</td>
            <td class="value" style="width:100%;">
                <% if (tbStatus) { %>
                ${ ui.format(tbStatus.valueCoded) } on ${ ui.format(tbStatus.obsDatetime) }
                <% } else { %>
                <span class="alert">No TB Status Recorded</span>
                <% } %>
            </td>
        </tr>

        <tr>
            <td class="question">Last Height:</td>
            <td class="value" style="width:100%;">
                <% if (height) { %>
                ${ ui.format(height.valueNumeric) } cm on ${ ui.format(height.obsDatetime) }
                <% } else { %>
                <span class="alert">No Height Recorded</span>
                <% } %>
            </td>
        </tr>

        <tr>
            <td class="question">Last Weight:</td>
            <td class="value" style="width:100%;">
                <% if (weights && weights.size() > 0) { %>
                ${ weights.get(weights.size()-1).valueNumeric } kg on ${ui.format(weights.get(weights.size()-1).obsDatetime)}
                <% } else { %>
                <span class="alert">No Weight Recorded</span>
                <% } %>
            </td>
        </tr>

        <tr>
            <td class="question">Current BMI:</td>
            <td class="value" style="width:100%;">

            </td>
        </tr>

    </table>

    <div class="section">

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
                <td style="width:100%;">
                    This is where the table of weights will go...
                </td>
            </tr>
        </table>

    </div>

    <h2>TODO</h2>
    <div id="items-to-do">
        <ul class="list">
            <li>BMI</li>
            <li>Record weight change</li>
            <li>Record CD4 change</li>
            <li>
                Hightlight alerts prominently around things like:
                Weight change, BMI, CD4 Decrease / Threshold, Viral Load Increase / Threshold
            </li>
        </ul>

    </div>

</table>

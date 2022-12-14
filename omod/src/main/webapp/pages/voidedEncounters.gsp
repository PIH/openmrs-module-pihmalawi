<%
    ui.decorateWith("appui", "standardEmrPage")
    ui.includeJavascript("uicommons", "moment.min.js")

    def editDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")

    def searchFromDate = null;
    if (binding.hasVariable('fromDate')) {
        searchFromDate = fromDate;
    } else {
        searchFromDate = ((new Date()) - 180);
    }


%>
<script>

  jq(function () {
    jq(".tabs").tabs();
  });
</script>

<div class="container">
    <div class="panel panel-primary">
        <form id="search-form" method="post">
            <div class="panel-body">
                <div class="row">
                    <div class="col-md-4">
                        <label>From:</label>
                        ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                                id: "from-date",
                                formFieldName: "from-date-field",
                                defaultDate: searchFromDate,
                                endDate: new Date(),
                                label: "",
                                useTime: false
                        ])}
                    </div>
                    <div class="col-md-4">
                        <label>To:</label>
                        ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                                id: "to-date",
                                formFieldName: "to-date-field",
                                defaultDate: new Date(),
                                endDate: new Date(),
                                label: "",
                                useTime: false
                        ])}
                    </div>
                    <div class="col-md-2 align-self-end">
                        <button type="submit" id="searchVoidedEncounters" name="searchVoidedEncounters">Search</button>
                    </div>

                </div>
            </div>
        </form>
    </div>
</div>
<br/><br/>

<div class="tabs" xmlns="http://www.w3.org/1999/html">
    <div class="dashboard-container">

        <ul>
            <li>
                <a href="#voided">
                    Voided
                </a>
            </li>
        </ul>

        <div id="voided" class="row" style="display: inline-flex;width: -webkit-fill-available;border-top: 1px solid #dddddd;">
            <h1>
                ${ ui.message("pihmalawi.title") } Voided Encounters
            </h1>
            <table style="width:100%;">
                <tr>
                    <th style="text-align:left;">PatientId</th>
                    <th style="text-align:left;">EncId</th>
                    <th style="text-align:left;">EncType</th>
                    <th style="text-align:left;">EncDateTime</th>
                    <th style="text-align:left;">DateCreated</th>
                    <th style="text-align:left;">User</th>
                    <th style="text-align:left;">Location</th>
                    <th style="text-align:left;">Voided</th>
                    <th style="text-align:left;">VoidedBy</th>
                </tr>

                <% voidedEncounters.each { enc -> %>
                <tr>
                    <td><a href="${ '/' + ui.contextPath() + '/patientDashboard.form?patientId=' + enc.patient.patientId}">${ enc.patient.patientId }</a></td>
                    <td><a href="${ '/' + ui.contextPath() + '/admin/encounters/encounter.form?encounterId=' + enc.encounterId}">${ enc.encounterId }</a></td>
                    <td>${ enc.encounterType.name }</td>
                    <td>${ editDateFormat.format(enc.encounterDatetime) }</td>
                    <td>${ enc.dateCreated }</td>
                    <td>${ enc.creator.person.personName }</td>
                    <td>${ enc.location.name }</td>
                    <td>${ enc.voided }</td>
                    <td>${ enc.voidedBy }</td>
                </tr>
                <% } %>

            </table>
        </div>
    </div>
</div>


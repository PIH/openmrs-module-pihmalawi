<%
    def encounterType = config.encounterType
    def patientUuid = config.patientUuid

    def diagnosis = "656292d8-977f-11e1-8993-905e29aff6c1"
    def prematureBirth = "f541084c-84c7-48a6-b502-d9ddbb3bb3b9"
    def HIE = "76e0ba08-d931-4baf-9651-9946543cc623"
    def lowBirthWeight = "6575742a-977f-11e1-8993-905e29aff6c1"
    def developmentalDelay = "1be62437-3093-4530-b4ab-1cd4626b9704"
    def hydrocephalus = "26071668-6ad4-4d30-b661-a7a07cece1ac"
    def cleftLip = "c415db67-75e8-4077-a0f2-ba2864ae52b1"
    def cnsInfection = "657169d4-977f-11e1-8993-905e29aff6c1"
    def trisomy = "fc4bf95c-b445-44e3-959b-435145e79f01"
    def cleftpalate = "abe71d88-3f2c-4380-854b-c49b74946a01"
    def severeMalnutrition = "a94e5963-f6b1-4c91-b676-48dfb370a1f8"
    def otherNonCoded = "6562c316-977f-11e1-8993-905e29aff6c1"
    def epilepsy ="6546938a-977f-11e1-8993-905e29aff6c1"

%>

<script type="text/javascript">
  jq(function() {

    var contextPath = window.location.href.split('/')[3];
    var apiBaseUrl =  "/" + contextPath + "/ws/rest/v1";

    jq.getJSON(apiBaseUrl + "/encounter", {
      patient: "${ patientUuid }" ,
      encounterType: "${ encounterType }",
      v: "custom:(id,uuid,encounterDatetime,encounterType:(id,name,display),obs:(uuid,display,obsDatetime,comment,valueCoded:(id,uuid,display),concept:(uuid,name:(display),datatype:(uuid,display))))"
    }, function(data) {
      for (var index = 0; index < data.results.length; ++index) {
        var enc = data.results[index];
        if (enc.obs !== null && enc.obs.length > 0 ) {
          for (var j = 0; j < enc.obs.length; ++j) {
            var obs = enc.obs[j];
            if ( obs.concept.uuid === "${ diagnosis }" && obs.valueCoded != null) {
              jq("#" + obs.valueCoded.uuid).prop('checked', true);
              if (obs.comment != null ) {
                console.log("obs.comment: " + obs.comment);
                jq("#" + obs.valueCoded.uuid + "_comment").val(obs.comment);
              }
            }
          }
        }
      }
    });
  });
</script>

<table>
    <tr><td colspan="4"><div class="top-section-title"><h2><b>Conditions at enrollment</b></h2></div></td><tr>

    <tr>
        <td>
            <input type="checkbox" id="${ prematureBirth }" disabled>
            <label>Premature Birth</label>
        </td>
        <td>
            <input type="checkbox" id="${ HIE }" disabled>
            <label>HIE</label>
        </td>
        <td>
            <input type="checkbox" id="${ lowBirthWeight }" disabled>
            <label>Low Birth Weight</label>
        </td>
        <td>
            <input type="checkbox" id="${ developmentalDelay }" disabled>
            <label>Developmental Delay</label>
        </td>
    </tr>
    <tr>
        <td>
            <input type="checkbox" id="${ hydrocephalus }" disabled>
            <label>Hydrocephalus</label>
        </td>
        <td>
            <input type="checkbox" id="${ cleftLip }" disabled>
            <label>Cleft Lip</label>
        </td>
        <td colspan="1">
            <input type="checkbox" id="${ cnsInfection }" disabled>
            <label>CNS Infection: </label>
            <input id="${ cnsInfection }_comment" type="text" disabled>
        </td>
        <td>
            <input type="checkbox" id="${ epilepsy }" disabled>
            <label>Epilepsy</label>
        </td>
    </tr>
    <tr>
        <td>
            <input type="checkbox" id="${ trisomy }" disabled>
            <label>Trisomy 21</label>
        </td>
        <td>
            <input type="checkbox" id="${ cleftpalate }" disabled>
            <label>Cleft Palate</label>
        </td>
        <td>
            <input type="checkbox" id="${ severeMalnutrition }" disabled>
            <label>Severe Malnutrition</label>
        </td>
        <td>
            <input type="checkbox" id="${ otherNonCoded }" disabled>
            <label>Other: </label>
            <input id="${ otherNonCoded }_comment" type="text" disabled>
        </td>
    </tr>

</table>


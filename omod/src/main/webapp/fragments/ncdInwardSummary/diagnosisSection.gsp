<%
	config.require("section")
%>
<% if (config.section.rows.size() > 0) { %>
    <td class="diagnosisSection">
        <table style="min-width:300px;">
            <tr><td colspan="2"><div class="top-section-title">${config.section.label}</div></td><tr>

            <tr>
                <td>Last Visit Date:</td>
                <td>${ui.format(config.section.latestEncounterDate)}</td>
            </tr>

            <% config.section.obsValues.each { obsValue -> %>
            <tr>
                <td>${obsValue.key}:</td>
                <td>${ui.format(obsValue.value)}</td>
            </tr>
            <% } %>
            <tr>
                <td>Current Medications:</td>
                <td style="white-space: normal; width: 100%;">
                    <% config.section.currentMedications.eachWithIndex { med, idx -> %>${ (idx == 0 ? "" : ", ") + ui.format(med)}<% } %>
                </td>
            </tr>

        </table>
    </td>
<% } %>
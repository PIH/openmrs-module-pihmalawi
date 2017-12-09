<%
	config.require("section")
%>
<% if (config.section.rows.size() > 0) { %>
    <td class="diagnosisSection">
        <table style="width:300px;">
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
                <td>
                    <% config.section.currentMedications.each { med -> %>
                    <div>${ui.format(med)}</div>
                    <% } %>
                </td>
            </tr>

        </table>
    </td>
<% } %>
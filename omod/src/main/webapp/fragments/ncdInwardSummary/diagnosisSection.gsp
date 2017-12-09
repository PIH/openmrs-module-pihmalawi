<%
	config.require("section")
%>
<table>
    <tr><td colspan="2"><div class="top-section-title">${config.section.label}</div></td><tr>
    <% if (config.section.rows.size() > 0) { %>
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
    <% } else { %>
        <tr><td colspan="2">Not enrolled</td><tr>
    <% } %>
</table>
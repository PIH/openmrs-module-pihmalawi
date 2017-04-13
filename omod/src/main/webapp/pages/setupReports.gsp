<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<table style="width:100%;">
    <tr>
        <th style="text-align:left;">Report</th>
        <th style="text-align:left;">Last Refreshed</th>
        <th style="text-align:left;">Actions</th>
    </tr>
    <% reportMap.each { m -> %>
        <tr>
            <td>${ m.key.name }</td>
            <% if (m.value == null) { %>
                <td></td>
                <td>
                    <form method="post">
                        <input type="hidden" name="mode" value="create"/>
                        <input type="hidden" name="uuid" value="${m.key.uuid}"/>
                        <input type="submit" value="Create"/>
                    </form>
                </td>
            <% } else { %>
                <td>${ ui.format(m.value.dateCreated ?: m.value.dateChanged) }</td>
                <td>
                    <form method="post">
                        <input type="hidden" name="mode" value="delete"/>
                        <input type="hidden" name="uuid" value="${m.key.uuid}"/>
                        <input type="submit" value="Delete"/>
                    </form>
                </td>
            <% } %>
            </td>
        </tr>
    <% } %>

</table>
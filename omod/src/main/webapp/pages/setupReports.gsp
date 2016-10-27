<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<table style="width:100%;">

    <% messages.each { m -> %>
        <tr>
            <td>${ m }</td>
        </tr>
    <% } %>

</table>
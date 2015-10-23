Mastercard Configuration
============

The mastercard framework expects two forms, a form representing the header, and a form representing each visit.

These forms need to be structured as follows:

* The encounterDate element needs to have an id of "visitDate":

	<encounterDate id="visitDate" size="20" default="today" />
	
* The encounterLocation element needs to have an id of "visitLocation":

	<encounterLocation id="visitLocation" />
	
* The Next Appointment Date observation needs to have an id of "appointmentDate".  This will enforce this date must be within 0-6 months of the visit date.

	<obs conceptId="$nextAppt" id="appointmentDate" allowFutureDates="true" />

* By putting the css class of 'focus-field' on an element that wraps a form field, this will ensure that this field has focus on it when the form is loaded

Example:

	<tr>
		<th>Height</th>
		<td class="focus-field"><obs conceptId="$height" id="heightInput" showUnits="true" /></td>
	</tr>
	
* The visit form must be structured in View Mode as a single table, with the following structure and css classes.  The visit date cell must have the 'visit-date' css class.

	<ifMode mode="VIEW" include="true">
		<table class="visit-table">
			<thead class="visit-table-header">
				...
			</thead>
			<tbody class="visit-table-body">
				<tr class="visit-table-row">
					<td class="nowrap visit-date">
						<encounterDate />
					</td>
					...
				</tr>
			</tbody>
		</table>
	</ifMode>
	
* The visit form should be structured in Edit Mode (not required, but takes advantage of built in css) with the form written as a table with css class 'visit-edit-table'

    <ifMode mode="VIEW" include="false">
		...
        <table class="visit-edit-table">
            <tr>
                <th>Visit Date</th>
                <td><encounterDate id="visitDate" size="20" default="today" /></td>
            </tr>
            ...
        </table>
    </ifMode>
    

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
<title>Project Estimation Tool</title>
<link rel="stylesheet" type="text/css" href="/kanban/header.css"/>

<style type="text/css">
table {
	border-collapse: collapse;
}

td,th {
	border: solid 2px black;
	padding: 3px;
}

table.nolines td {
	border: none;
	padding: 0px;
}
</style>

<script type="text/javascript">
	function changeProjectProperty(promptName, submitName) {
		var value = prompt("Change " + promptName + " to?");
		document.getElementById("projectPropertyNameHiddenField").value = submitName;
		document.getElementById("projectPropertyValueHiddenField").value = value;
	}
</script>
</head>

<body>
	<h1>Project Estimation Tool</h1>

	<p>
		<a href='../..'>Go back to main Kanban site</a> <br/>
		<a href='../../projects/${project.projectName}/wall'>Goto project <b>${project.projectName}</b> wall</a>
	</p>

	<form action="set-project-property">
		<table>
			<tr>
				<td>Budget</td>
				<td>$${project.budget}</td>
				<td><input id="projectPropertyNameHiddenField" type="hidden"
					name="name" value="" /> <input
					id="projectPropertyValueHiddenField" type="hidden" name="value"
					value="" /> <input type="submit" value="Edit"
					onclick="changeProjectProperty('budget', 'budget');" /></td>
			</tr>
			<tr>
				<td>Cost so far</td>
				<td>$${project.costSoFar}</td>
				<td><input type="submit" value="Edit"
					onclick="changeProjectProperty('cost so far', 'costSoFar');" /></td>
			</tr>
			<tr>
				<td>Cost per point (estimated)</td>
				<td>$${project.estimatedCostPerPoint}</td>
				<td><input type="submit" value="Edit"
					onclick="changeProjectProperty('estimated cost per point', 'estimatedCostPerPoint');" />
				</td>
			</tr>
		</table>
	</form>

	<h2>Planned features</h2>

	<table class="lined">
		<tr>
			<th rowspan="2" colspan="2"></th>
			<th rowspan="2">Description</th>
			<th rowspan="2">Importance</th>
			<th colspan="2">Feature points</th>
			<th colspan="2">Running estimate</th>
		</tr>
		<tr>
			<th>Best Case</th>
			<th>Worst Case</th>
			<th>Best Case</th>
			<th>Worst Case</th>
		</tr>
		<c:forEach items="${project.budgetEntries}" var="entry">
			<tr
				style="background: ${entry.feature.mustHave ? (entry.overBudgetInWorstCase ? 'Red' : 'Khaki') : (entry.overBudgetInBestCase ? 'LightGrey' : 'White') }">
				<td>
					<table class="nolines">
						<tr>
							<td colspan="2">
								<form action="set-feature-included-in-estimates">
									<div>
										<input type="hidden" name="id" value="${entry.feature.id}" />
										<input type="hidden" name="value"
											value="${entry.feature.mustHave ? 'false' : 'true'}" /> <input
											type="submit"
											value="${entry.feature.mustHave ? 'Nice to Have' : 'Must Have'}" />
									</div>
								</form>
							</td>
						</tr>
						<tr>
							<td>
								<form action="edit-feature">
									<div>
										<input type="hidden" name="id" value="${entry.feature.id}" />
										<input type="submit" value="Edit" />
									</div>
								</form>
							</td>
							<td>
								<form action="complete-feature">
									<div>
										<input type="hidden" name="id" value="${entry.feature.id}" />
										<input type="submit" value="Complete" />
									</div>
								</form>
							</td>
						</tr>
					</table>
				</td>
				<td>
					<form action="move-feature">
						<div>
							<input type="hidden" name="id" value="${entry.feature.id}" /> <input
								type="hidden" name="direction" value="up" /> <input
								type="submit" value="↑" />
						</div>
					</form>
					<form action="move-feature">
						<div>
							<input type="hidden" name="id" value="${entry.feature.id}" /> <input
								type="hidden" name="direction" value="down" /> <input
								type="submit" value="↓" />
						</div>
					</form>
				</td>
				<td
					style="${!entry.feature.mustHave && entry.overBudgetInBestCase ? 'text-decoration: line-through;' : ''}">${entry.feature.description}</td>
				<td>${entry.feature.mustHave ? 'Must have' : 'Nice to have'}</td>
				<td>${entry.feature.bestCaseEstimate}</td>
				<td>${entry.feature.worstCaseEstimate}</td>
				<td
					style="${!entry.feature.mustHave && entry.overBudgetInBestCase ? 'text-decoration: line-through;' : ''}">$${entry.bestCaseCumulativeCost}</td>
				<td
					style="${!entry.feature.mustHave && entry.overBudgetInBestCase ? 'text-decoration: line-through;' : ''}">$${entry.worstCaseCumulativeCost}</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="2" />
			<td colspan="6">
				<span style="color: #777777">To add features go to project wall</span>
			</td>
		</tr>
	</table>

	<h2>Complete features</h2>

	<table class="lined">
		<tr>
			<th>Description</th>
			<th>Feature points</th>
		</tr>
		<c:forEach items="${project.completedFeatures}" var="feature">
			<tr>
				<td>${feature.description}</td>
				<td>${feature.bestCaseEstimate}</td>
			</tr>
		</c:forEach>
	</table>

	<p>Cost per point so far: $${project.costPerPointSoFar}</p>

<!-- 
	<hr />

	<div style="background-color: #FFCCCC">
		To be removed:

		<form action="reset-demo">
			<p>
				<input type="submit" value="Reset the awesome demo" />
			</p>
		</form>
	</div>
-->	
</body>

</html>

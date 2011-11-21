<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<label class="labelClass" for="name">Name:</label>
<textarea id="name" name="name" rows="2" cols="40">${workItem.name}</textarea>
<br />

<label class="labelClass" for="averageCaseEstimate">Average Case:</label>
<input id="averageCaseEstimate" size="10" type="text" name="averageCaseEstimate" value="${workItem.averageCaseEstimate}" /> (Size)
<br />

<label class="labelClass" for="worstCaseEstimate">Worst Case:</label>
<input id="worstCaseEstimate" size="10" type="text" name="worstCaseEstimate" value="${workItem.worstCaseEstimate}" />
<br />

<label class="labelClass" for="importance">Importance:</label>
<input id="importance" size="10" type="text" name="importance" value="${workItem.importance}" />
<br />

<c:if test="${workItem.topLevel || topLevel}">
    <label class="labelClass" for="workStreams">Work streams:</label>
    <ul id="workStreams"></ul>
    <br />

</c:if>

<label class="labelClass" for="notes">Notes:</label>
<textarea id="notes" name="notes" rows="5" cols="40">${workItem.notes}</textarea>
<br />

<label class="labelClass" for="excluded">Excluded from reports:</label>
<c:choose>
    <c:when test="${workItem.excluded}">
        <input type="checkbox" name="excluded" checked="checked" />
    </c:when>
    <c:otherwise>
        <input type="checkbox" name="excluded" />
    </c:otherwise>
</c:choose>
<br />

<label class="labelClass" for="color">Color</label>
<input size="10" type="text" id="colorid" name="color" value="${workItem.colour == null ? '#FFFFFF'  : workItem.colour }" style="display: none" />
<div class="wrapper">
    <div id="colorSelector">
        <div style="background-color: ${workItem.colour}">&nbsp;</div>
    </div>
    <div>
        <div class="presetColor" style="background: #FFFFFF;"></div>
        <div class="presetColor" style="background: #D96666;"></div>
        <div class="presetColor" style="background: #F2A640;"></div>
        <div class="presetColor" style="background: #fbff00;"></div>
        <div class="presetColor" style="background: #7EC225;"></div>
        <div class="presetColor" style="background: #59BFB3;"></div>
        <div class="presetColor" style="background: #668CD9;"></div>
        <div class="presetColor" style="background: #B373B3;"></div>
    </div>
</div>
<br />
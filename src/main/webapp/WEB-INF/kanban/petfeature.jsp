<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>
	<title>${pageTitle}</title>
	<style>
	body {
		font: normal 11px verdana;
	}
	</style>
</head>

<body>
    <h1>${pageTitle}</h1>

    <form action="pet-save-feature">
        <fieldset>
            <legend>Feature</legend>
            <p>
                Description<br />
                <input name="description" size="60" value="${feature.name}" disabled="disabled" />
                <span style="color: #CCCCCC">To edit description go to the project wall</span>
            </p>
        </fieldset>
        <fieldset>
            <legend>Point estimate</legend>
            <p>
                Average case<br />
                <input name="averageCaseEstimate" value="${feature.averageCaseEstimate}" />
            </p>
            <p>
                Worst case<br />
                <input name="worstCaseEstimate" value="${feature.worstCaseEstimate}" />
            </p>
        </fieldset>
        <p>
            <input type="hidden" name="id" value="${feature.id}" />
            <input type="submit" value="Save" />
            <a href="project">Cancel</a>
        </p>
    </form>
</body>

</html>

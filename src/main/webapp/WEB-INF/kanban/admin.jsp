<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
	<jsp:include page="include/header-head.jsp"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/admin.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/admin.js"></script>
</head>
<body>
	<jsp:include page="include/header.jsp" />
    <h1>Project administration</h1>

    <ul class="admin-menu">
        <li><a href="edit-project?createNewProject=true">New Project</a></li>
        <li><a href="edit-project?createNewProject=false" >Edit Project</a></li>
        <li>
    <%--         <a href="javascript:addColumn();" > --%>
              <span>Add Column [not properly working]</span>          
    <%--         </a> --%>
        </li>
        <li>
    <%--     	  <a href="javascript:addWaitingColumn();" > --%>
            <span> Add Waiting Column [not implemented yet] </span>
    <%--     	  </a> --%>
          </li>
        <li>
    <%--      <a href="javascript:deleteColumn();" > --%>
            <span> Delete Column [not properly working] </span>
    <%--       </a> --%>
        </li>
    
        <c:forEach var="workItemType" items="${project.workItemTypes}">
            <li><a href="download?workItemTypeName=${workItemType.name}">Export ${workItemType.name}</a></li>
        </c:forEach>
    </ul>
</body>
</html>

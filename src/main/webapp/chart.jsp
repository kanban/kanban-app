<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
   "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <script type="text/javascript" src="${pageContext.request.contextPath}/header.js"></script>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/header.css"/>
        <title>Kanban</title>
    </head>
    
    <body>
        <jsp:include page="header.jsp"/>
    
        <div><img src="${imageName}?level=${workItemTypeName}" alt="[chart]"></img></div>
    </body>
</html>

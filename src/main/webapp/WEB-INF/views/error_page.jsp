<%--
  Created by IntelliJ IDEA.
  User: soohyun
  Date: 2025. 6. 10.
  Time: 오후 6:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
<h4><c:out value="${exception.getMessage()}"></c:out></h4>
<ul>
  <c:forEach items="${exception.getStackTrace() }" var="stack">
    <li><c:out value="${stack}"></c:out></li>
  </c:forEach>
</ul>
</body>
</html>

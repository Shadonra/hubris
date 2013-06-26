<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="error" scope="request" class="java.lang.String"/>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>

  <link rel="stylesheet" type="text/css" href="/resources/css/bootstrap.min.css" />
  <link rel="stylesheet" type="text/css" href="/resources/css/slider.css" />
  <link rel="stylesheet" type="text/css" href="/spacecase.css" />

  <script type="text/javascript" src="/resources/scripts/jquery.min.js"></script>
  <script type="text/javascript" src="/resources/scripts/bootstrap.min.js"></script>
  <script type="text/javascript" src="/resources/scripts/bootstrap-slider.js"></script>
  <script type="text/javascript" src="/resources/scripts/d3.min.js"></script>
  <script type="text/javascript" src="/spacecase.js"></script>

  <title>Hubris</title>
</head>

<body>
  <jsp:include page="/common/nav_no_login.jsp"/>

  <c:if test="${error != null}">
    <div class="alert-error">${error}</div>
  </c:if>

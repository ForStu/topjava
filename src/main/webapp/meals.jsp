<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<style>
    table, th, td {
        border:1px solid black;
    }
</style>
<head>
    <title>Meals</title>
</head>
<body>
<h2>You are at Meals!</h2>
<table>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach items="${mealsList}" var="meal">
        <tr>
            <td>
                <c:out value="${meal.dateTime}"/>
            </td>
            <td>
                <c:out value="${meal.description}"/>
            </td>
            <td>
                <c:out value="${meal.calories}"/>
            </td>
            <td>
                <a href="">Delete</a>
            </td>
            <td>
                <a href="">Update</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>

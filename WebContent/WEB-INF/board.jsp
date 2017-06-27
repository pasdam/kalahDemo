<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Kalah</title>
<style>
.board td {
	padding: 16px 17px;
	vertical-align: middle;
	text-align: center
}

.p0 {
	background-color: #D2E4FC
}

.p1 {
	background-color: #D0E000
}
</style>
</head>
<body>
	<form action="/kalah/">
		<input type="submit" value="Refresh">
	</form>
	<table>
		<tr>
			<td>You:</td>
			<td class="p${sessionPlayer}">${sessionPlayer >= 0 ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "not playing"}
			</td>
		</tr>
		<tr>
			<td>Current player:</td>
			<td class="p${currentPlayer}">&nbsp;&nbsp;&nbsp;&nbsp;</td>
		</tr>
	</table>
	<br />
	<c:if test="${p0Houses != null && p1Houses != null}">
		<table class="board">
			<tr>
				<td class="p0" rowspan="5">${p0Store}</td>
				<c:forEach begin="0" end="${fn:length(p0Houses)-1}" var="i">
					<td class="p0"><c:out value="${fn:length(p0Houses)-i-1}" /></td>
				</c:forEach>
				<td class="p1" rowspan="5">${p1Store}</td>
			</tr>
			<tr>
				<c:forEach begin="0" end="${fn:length(p0Houses)-1}" var="i">
					<td class="p0"><c:out
							value="${p0Houses[fn:length(p0Houses)-i-1]}" /></td>
				</c:forEach>
			</tr>
			<tr>
				<td colspan="6"></td>
			</tr>
			<tr>
				<c:forEach begin="0" end="${fn:length(p1Houses)-1}" var="i">
					<td class="p1"><c:out value="${i}" /></td>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach begin="0" end="${fn:length(p1Houses)-1}" var="i">
					<td class="p1"><c:out value="${p1Houses[i]}" /></td>
				</c:forEach>
			</tr>
		</table>
	</c:if>

	<c:if test="${sessionPlayer == currentPlayer}">
		<form action="/kalah/board" method="post">
			<label>Move house:</label> <input name="house" type="number">
			<input type="submit" value="Submit">
		</form>
	</c:if>
</body>
</html>
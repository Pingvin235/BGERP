<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>
<%--
list - перечень элементов
value - выбранное значение
 либо
values - набор с этим выбранным значением
--%>

<c:choose>
	<c:when test="${empty available}">
		<c:forEach var="item" items="${list}">
			<c:if test="${fn:startsWith(item.title, '@')==false}">
				<c:set var="selected" value=""/>
				<%-- может быть Set с одним элементом --%>
				<c:if test="${value eq item.id or u:contains( values, item.id )}">
					<c:set var="selected">selected='1'</c:set>
				</c:if>
				<option value="${item.id}" ${selected}>${item.title}</option>
			</c:if>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:forEach var="availableId" items="${available}">			
			<c:forEach var="listItem" items="${list}">
				<c:if test="${availableId == listItem.id}">
					<c:set var="item" value="${listItem}"/>
				</c:if>
			</c:forEach>
			<c:if test="${not empty item }">
				<c:if test="${fn:startsWith(item.title, '@')==false}">
					<c:set var="selected" value=""/>
					<%-- может быть Set с одним элементом --%>
					<c:if test="${value eq item.id or u:contains( values, item.id )}">
						<c:set var="selected">selected='1'</c:set>
					</c:if>
					<option value="${item.id}" ${selected}>${item.title}</option>
				</c:if>
			</c:if>
		</c:forEach>
	</c:otherwise>
</c:choose>
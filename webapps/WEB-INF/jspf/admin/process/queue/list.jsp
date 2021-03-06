<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<html:form action="admin/process" styleClass="in-mr1">
	<input type="hidden" name="action" value="queueList"/>

	<c:url var="url" value="/admin/process.do">
		<c:param name="action" value="queueGet"/>
		<c:param name="id" value="-1"/>
		<c:param name="returnUrl" value="${form.requestUrl}"/>
	</c:url>

	<ui:button type="add" onclick="$$.ajax.load('${url}', $$.shell.$content(this))"/>
	
	<ui:input-text name="filter" value="${form.param.filter}" size="40" placeholder="${l.l('Фильтр')}" title="${l.l('Фильтр по наименованию, конфигурации')}"
		onSelect="$$.ajax.load(this.form, $$.shell.$content(this)); return false;"/>

	<%@ include file="/WEB-INF/jspf/page_control.jsp"%>
</html:form>	


<table style="width: 100%;" class="data mt1">
	<tr>
		<td width="30">&#160;</td>
		<td width="30">ID</td>
		<td width="100%">${l.l('Наименование')}</td>
	</tr>
	<c:forEach var="item" items="${form.response.data.list}">
		<tr>
			<c:url var="editUrl" value="/admin/process.do">
				<c:param name="action" value="queueGet"/>
				<c:param name="id" value="${item.id}"/>
				<c:param name="returnUrl" value="${form.requestUrl}"/>
			</c:url>
			<c:url var="deleteUrl" value="/admin/process.do">
				<c:param name="action" value="queueDelete"/>
				<c:param name="id" value="${item.id}"/>
			</c:url>
			<c:url var="deleteAjaxCommandAfter" value="openUrlContent( '${form.requestUrl}' )"/>
			
			<%-- <td nowrap="nowrap"><%@ include file="/WEB-INF/jspf/edit_buttons.jsp"%></td> --%>

			<td nowrap="nowrap">
					<ui:button type="edit" styleClass="btn-small" onclick="$$.ajax.load('${editUrl}', $$.shell.$content(this))"/>
					<ui:button type="del" styleClass="btn-small" onclick="$$.ajax.post('${deleteUrl}').done(() => { $$.ajax.load('${form.requestUrl}', $$.shell.$content(this)) })"/>
			</td>

			<td>${item.id}</td>
			<td>${item.title}</td>
		</tr>
	</c:forEach>
</table>

<shell:title ltext="Очереди процессов"/>
<shell:state text=""/>
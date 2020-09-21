<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<c:set var="uiid" value="${u:uiid()}"/>

<c:set var="showCode" value="openUrlContent( formUrl( $('#${uiid}') ) );"/>

<html:form action="admin/user" styleClass="in-mr1 in-mb1" styleId="${uiid}" style="vertical-align: middle;">
	<c:url var="url" value="/admin/user.do">
	   <c:param name="action" value="userGet"/>
	   <c:param name="id" value="-1"/>	
	   <c:param name="returnUrl" value="${form.requestUrl}"/>
	</c:url>
	<button type="button" class="btn-green" onclick="openUrlContent( '${url}' )">+</button>
				
	<input type="hidden" name="action" value="userList"/>
	<input type="hidden" name="pageableId" value="userList"/>
   
    <ui:input-text name="title" value="${form.param['title']}" size="20" placeholder="Фильтр" styleClass="ml1" title="Фильтр по наименованию"
    	onSelect="openUrlContent( formUrl( this.form ) ); return false;"/>
    
    <ui:combo-single hiddenName="status" value="${form.param.status}" onSelect="${showCode}" 
    	prefixText="Статус:" widthTextValue="70px">
    	<jsp:attribute name="valuesHtml">
    		<li value="0">Активные</li>
			<li value="1">Заблокированные</li>			
			<li value="-1">Все</li>
    	</jsp:attribute>
    </ui:combo-single>
    
    <c:set var="perm" value="${p:get(form.user.id, 'ru.bgcrm.struts.action.admin.UserAction:userList')}"/>
    <c:if test="${empty perm['allowOnlyGroups'] or not empty perm['allowFilterGroups']}">
	   	<ui:select-single hiddenName="group" 
	    	list="${ctxUserGroupFullTitledList}" map="${ctxUserGroupFullTitledMap}"
	    	availableIdSet="${u:toIntegerSet(perm['allowFilterGroups'])}"
	    	value="${form.param.group}"
	    	onSelect="${showCode}" placeholder="Группа" style="width: 200px;"/>
	    	
	    <ui:date-time paramName="date" placeholder="Гр. на дату" value="${form.param.date}"/>       	
    </c:if>
    
    <ui:select-single list="${ctxUserPermsetList}" hiddenName="permset" value="${form.param.permset}" 
    	onSelect="${showCode}" placeholder="Набор прав" style="width: 200px;"/>
    
    <button class="btn-grey" type="button" onclick="${showCode}" title="Вывести">=&gt;</button>
    
    <%@ include file="/WEB-INF/jspf/page_control.jsp"%>
</html:form>

<table style="width: 100%;" class="data">
	<tr>
		<td width="30">&#160;</td>
		<td width="30">ID</td>
		<td>Статус</td>
		<td>Наименование</td>
		<td>Логин</td>
		<td>Наборы прав</td>
		<td>Группы</td>
		<td>Комментарий</td>
	</tr>
	<c:forEach var="item" items="${form.response.data.list}">
		<tr>
		 	<c:url var="editUrl" value="/admin/user.do">
				<c:param name="action" value="userGet"/>
				<c:param name="id" value="${item.id}"/>
				<c:param name="returnUrl" value="${form.requestUrl}"/>
			</c:url>
			<c:url var="deleteAjaxUrl" value="/admin/user.do">
				<c:param name="action" value="userDelete"/>
				<c:param name="id" value="${item.id}"/>
			</c:url>
			<c:url var="deleteAjaxCommandAfter" value="${showCode}"/>
				
			<td nowrap="nowrap"><%@ include file="/WEB-INF/jspf/edit_buttons.jsp"%></td>
			
			<td>${item.id}</td>
			<td>
				<c:choose>
					<c:when test="${item.status eq 0}">Активен</c:when>
					<c:when test="${item.status eq 1}">Заблокирован</c:when>
					<c:otherwise>Неизвестный статус (${item.status})</c:otherwise>
				</c:choose>
			</td>
			<td><ui:user-link id="${item.id}"/></td>
			<td>${item.login}</td>
			<td>${u:orderedObjectTitleList( ctxUserPermsetMap, item.permsetIds )}</td>
			<td>${u:objectTitleList( ctxUserGroupFullTitledList, item.groupIds )}</td>
			<td>${item.description}</td>			
		</tr>
	</c:forEach>
</table>

<shell:title ltext="Пользователи"/>
<shell:state text=""/>
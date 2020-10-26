<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<ui:menu-group ltitle="Создать">
	<jsp:attribute name="subitems">
		<ui:menu-item ltitle="Контрагент"
			action="ru.bgcrm.struts.action.CustomerAction:customerCreate"
			command="$$.customer.createAndEdit()" />
	</jsp:attribute>
</ui:menu-group>

<ui:menu-item ltitle="Мои процессы" href="process/my"
	action="ru.bgcrm.struts.action.ProcessAction"
	command="/user/process.do?action=userProcessList" hidden="1" />

<ui:menu-item ltitle="Поиск" href="search"
	action="ru.bgcrm.struts.action.SearchAction" command="/user/search.do" />

<ui:menu-item ltitle="Новости" href="news"
	action="ru.bgcrm.struts.action.NewsAction:newsList"
	command="/user/news.do?action=newsList&read=0" />

<ui:menu-item ltitle="Адреса" href="directory/address"
	action="ru.bgcrm.struts.action.DirectoryAddressAction"
	command="/user/directory/address.do" />

<ui:menu-item ltitle="Сообщения" href="message/queue"
	action="ru.bgcrm.struts.action.MessageAction:messageList"
	command="/user/message.do?action=messageList" />

<ui:menu-item ltitle="Процессы" href="process/queue"
	action="ru.bgcrm.struts.action.ProcessAction:queue"
	command="/user/process/queue.do?action=queue" />

<ui:menu-group ltitle="Администрирование">
	<jsp:attribute name="subitems">
	
		<ui:menu-item ltitle="Параметры" href="admin/param"
			action="ru.bgcrm.struts.action.admin.DirectoryAction"
			command="/admin/directory.do" />
		
		<ui:menu-group ltitle="Пользователи">
			<jsp:attribute name="subitems">

				<ui:menu-item ltitle="Наборы прав" href="admin/user/permset"
					action="ru.bgcrm.struts.action.admin.UserAction:permsetList"
					command="/admin/user.do?action=permsetList" />

				<ui:menu-item ltitle="Группы" href="admin/user/group"
					action="ru.bgcrm.struts.action.admin.UserAction:groupList"
					command="/admin/user.do?action=groupList" />

				<ui:menu-item ltitle="Пользователи" href="admin/user"
					action="ru.bgcrm.struts.action.admin.UserAction:userList"
					command="/admin/user.do?action=userList" />
			</jsp:attribute>
		</ui:menu-group>
		
		<ui:menu-group ltitle="Процессы">
			<jsp:attribute name="subitems">
			
				<ui:menu-item ltitle="Статусы процессов" href="admin/process/status"
					action="ru.bgcrm.struts.action.admin.ProcessAction:statusList"
					command="/admin/process.do?action=statusList" />
				
				<ui:menu-item ltitle="Типы процессов" href="admin/process/type"
					action="ru.bgcrm.struts.action.admin.ProcessAction:typeList"
					command="/admin/process.do?action=typeList" />
				
				<ui:menu-item ltitle="Очереди процессов" href="admin/process/queue"
					action="ru.bgcrm.struts.action.admin.ProcessAction:queueList"
					command="/admin/process.do?action=queueList" />
			</jsp:attribute>
		</ui:menu-group>
		
		<ui:menu-item ltitle="Динамический код" href="admin/dyncode"
			action="ru.bgcrm.struts.action.admin.DynamicAction"
			command="/admin/dynamic.do" />
		
		<ui:menu-item ltitle="WEB запросы" href="admin/log/request"
			action="ru.bgcrm.struts.action.admin.WebRequestAction"
			command="/admin/webRequest.do" />
		
		<ui:menu-item ltitle="Конфигурация" href="admin/config"
			action="ru.bgcrm.struts.action.admin.ConfigAction:list"
			command="/admin/config.do?action=list" />
		
		<ui:menu-group ltitle="Приложение">
			<jsp:attribute name="subitems">
			
				<ui:menu-item ltitle="Статус приложения" href="admin/app/status"
					action="ru.bgcrm.struts.action.admin.AppAction:status"
					command="/admin/app.do?action=status"/>
					
				<ui:menu-item ltitle="Авторизовавшиеся пользователи" href="admin/app/logged"
					action="ru.bgcrm.struts.action.admin.AppAction:userLoggedList"
					command="/admin/app.do?action=userLoggedList"/>
			</jsp:attribute>
		</ui:menu-group>
		
		<c:set var="endpoint" value="user.admin.menu.items.jsp" />
		<%@ include file="/WEB-INF/jspf/plugin_include.jsp"%>
	</jsp:attribute>
</ui:menu-group>

<ui:menu-item ltitle="Логирование" href="log/app"
	action="ru.bgcrm.struts.action.LogAction" command="/user/log.do" />

<c:set var="endpoint" value="user.menu.items.jsp"/>
<%@ include file="/WEB-INF/jspf/plugin_include.jsp"%>
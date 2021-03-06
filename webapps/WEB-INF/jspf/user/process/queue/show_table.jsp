<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<%-- генератор строк таблицы JEXL выражением --%>
<c:set var="rowExpression" value="${u:getConfig( queue.configMap, 'ru.bgcrm.model.process.config.RowExpressionConfig' )}"/>

<c:set var="tableUiid" value="${u:uiid()}"/>

<table class="data" id="${tableUiid}">
	<jsp:useBean id="headData" class="java.util.HashMap"/>

	<c:set var="checkAll">
		$(this).closest('table').find('input[name=processId]').each(
			function() {
				$(this).prop('checked', !$(this).prop('checked'));
			}
		);
	</c:set>

	<c:set var="showCheckColumn" value="${not empty queue.getProcessors(ctxIface) or not empty queue.configMap.checkColumn}"/>

	<c:set target="${headData}" property="checkAllLink">
		<a href="#" onclick="${checkAll}; return false;">✓</a>
	</c:set>

	<c:if test="${not empty rowExpression}">
		<c:set var="headExpressionHtml" value="${rowExpression.getHead( 'html', headData )}"/>
	</c:if>

	<c:set var="aggregateValues" value="${form.response.data.aggregateValues}"/>

	<c:choose>
		<c:when test="${not empty headExpressionHtml}">
			${headExpressionHtml}
		</c:when>
		<c:otherwise>
			<tr>
				<c:if test="${showCheckColumn}">
					<td width="20">${headData["checkAllLink"]}</td>
				</c:if>
				<c:forEach var="column" items="${columnList}" varStatus="status">
					<c:set var="show" value="${column.columnConf.value ne 'priority'}"/>

					<c:if test="${not empty column.title and show}">
						<td>${column.title}
							<c:if test="${not empty aggregateValues and not empty aggregateValues.get(status.index)}">
								<br/>[ ${aggregateValues.get(status.index)} ]
							</c:if>
						</td>
					</c:if>
				</c:forEach>
			</tr>
		</c:otherwise>
	</c:choose>

	<c:forEach var="row" items="${form.response.data.list}">
		<jsp:useBean id="rowData" class="java.util.HashMap"/>

		<c:set target="${rowData}" property="urgColor" value="${''}"/>

		<c:forEach begin="1" var="col" items="${row}" varStatus="status">
			<c:set var="column" value="${columnList[status.index - 1].columnConf}"/>

			<c:if test="${column.value eq 'priority'}">
				<c:set var="priority" value="${col}"/>
				<%@ include file="/WEB-INF/jspf/process_color.jsp"%>
				<c:set var="bgcolor" value="bgcolor='${color}'"/>
				<c:set target="${rowData}" property="urgColor" value="${color}"/>
			</c:if>
		</c:forEach>

		<c:set target="${rowData}" property="process" value="${row[0][0]}"/>
		<c:set target="${rowData}" property="linkedProcess" value="${row[0][1]}"/>

		<%-- расшифровка HTML значений столбцов --%>
		<c:forEach begin="1" var="col" items="${row}" varStatus="status">
			<c:set var="columnRef" value="${columnList[status.index - 1]}"/>
			<c:set var="column" value="${columnRef.columnConf}"/>

			<%-- процесс в зависимости от колонки либо основной либо связанный --%>
			<c:set var="process" value="${columnRef.firstColumn.getProcess(row[0])}"/>

			<c:set target="${rowData}" property="col${columnRef.columnId}">
				<c:choose>
					<c:when test="${column.value eq 'N'}">
						${status_from.count}
					</c:when>
					<c:when test="${column.value eq 'id' and not mob}">
						<ui:process-link process="${process}"/>
					</c:when>

					<c:when test="${fn:startsWith(column.value,'linkCustomerLink') or
									fn:startsWith(column.value,'linkedCustomerLink')}">
						<c:forEach var="customer" items="${fn:split(col,'$')}" varStatus="status">
							<c:set var="customerId" value="${fn:split(customer,':')[0]}"/>
							<c:set var="customerTitle" value="${fn:split(customer,':')[1]}"/>
							<c:choose>
								<c:when test="${mob}">${customerTitle}</c:when>
								<c:otherwise><a href="#" onclick="openCustomer(${customerId}); return false;">${customerTitle}</a></c:otherwise>
							</c:choose>
							<c:if test="${not status.last}">,</c:if>
						</c:forEach>
					</c:when>

					<c:when test="${fn:startsWith(column.value,'linkedObject:process')}">
						<c:forEach var="processId" items="${fn:split( col, ',' )}">
							<c:choose>
								<c:when test="${mob}">${processId}</c:when>
								<c:otherwise><ui:process-link id="${processId}"/></c:otherwise>
							</c:choose>
						</c:forEach>
					</c:when>

					<%-- TODO: Код этот должен быть по-правильному в плагине BGBilling --%>
					<c:when test="${fn:startsWith( column.value, 'linkObject:contract' ) or
									fn:startsWith( column.value, 'linkedObject:contract' )}">
						<c:forEach var="contractInfo" items="${fn:split( col, ',' )}">
							<c:set var="info" value="${fn:split( contractInfo, ':' )}"/>
							<c:choose>
								<c:when test="${mob}">${info[2]}</c:when>
								<c:otherwise><a href="#" onclick="bgbilling_openContract( '${info[0]}', '${info[1]}' ); return false;">${info[2]}</a></c:otherwise>
							</c:choose>
						</c:forEach>
					</c:when>

					<c:when test="${column.value eq 'actions'}">
						<c:set var="actionShowMode" value="${queue.configMap.actionShowMode}"/>

						<c:forEach var="action" items="${queue.actionList}">
							<c:if test="${u:contains( action.statusIds, process.statusId )}">
								<c:url var="url" value="/user/process.do">
									<c:param name="id" value="${process.id}"/>
									<c:param name="action" value="processDoCommands"/>
									<c:param name="commands" value="${action.commands}"/>
								</c:url>

								<c:choose>
									<c:when test="${actionShowMode eq 'buttons'}">
										<button class="btn-white btn-small" onclick="sendAJAXCommand( '${url}' );" title="${action.title}" style="${action.style}">${action.shortcut}</button>
									</c:when>
									<c:otherwise>
										<a href="#" onclick="sendAJAXCommand( '${url}' ); return false;" style="${action.style}">${action.title}</a><br/>
									</c:otherwise>
								</c:choose>
							</c:if>
						</c:forEach>
					</c:when>

					<c:when test="${fn:startsWith(column.value,'status')}">
						${fn:replace(col,'; ','</br>')}
					</c:when>

					<c:when test="${fn:startsWith(column.value,'linkProcessList') or fn:startsWith(column.value,'linkedProcessList')}">
						<div style="display: table">
							<c:forEach var="lp" items="${col}">
								<div style="display: table-row" class="in-table-cell in-pb05 in-pl05">
									<div><ui:process-link id="${lp.id}"/></div>
									<div>${lp.typeTitle}</div>
									<div><b>${lp.statusTitle}</b></div>
								</div>
							</c:forEach>
						</div>
					</c:when>

					<c:otherwise>
						<c:set var="title" value=""/>
						<c:if test="${not empty column.titleIfMore and col.length() gt column.titleIfMore}">
							<c:set var="title">title="${fn:escapeXml( col )}"</c:set>
							<c:set var="col">${fn:substring(col, 0, column.titleIfMore)}...</c:set>
						</c:if>

						<c:if test="${not empty column.formatToHtml}">
							<c:set var="col" value="${u:htmlEncode( col )}"/>
						</c:if>

						<span ${title}>
							<c:choose>
								<c:when test="${not empty column.cutIfMore}">
									<c:set var="maxLength" value="${u:int(column.cutIfMore)}"/>
									<c:if test="${maxLength gt 0}">
										<c:set var="text" value="${col}"/>
										<%@include file="/WEB-INF/jspf/short_text.jsp"%>
									</c:if>
								</c:when>
								<c:when test="${not empty column.showAsLink and not empty col}">
									<a href="${col}" target="_blank">
										<c:choose>
											<c:when test="${column.showAsLink eq 'linkUrl'}">${col}</c:when>
											<c:otherwise>${column.showAsLink}</c:otherwise>
										</c:choose>
									</a>
								</c:when>
								<c:when test="${column.value eq 'descriptionLink'}">
									<ui:process-link text="${col}" process="${process}"/>
								</c:when>
								<c:otherwise>${col}</c:otherwise>
							</c:choose>
						</span>
					</c:otherwise>
				</c:choose>
			</c:set>
		</c:forEach>

		<c:remove var="rowExpressionHtml"/>
		<c:if test="${not empty rowExpression}">
			<c:set var="rowExpressionHtml" value="${rowExpression.getRow( 'html', rowData )}"/>
		</c:if>

		<c:set var="process" value="${row[0][0]}"/>

		<c:choose>
			<c:when test="${not empty rowExpressionHtml}">
				${rowExpressionHtml}
			</c:when>
			<c:otherwise>
				<c:set var="openProcessId">
					<ui:when type="open">
						<c:if test="${u:getConfig(ctxSetup, 'org.bgerp.action.open.ProcessAction$Config').isOpen(process)}">${process.id}</c:if>
					</ui:when>
				</c:set>

				<tr ${bgcolor} processId="${process.id}" openProcessId="${openProcessId}">
					<c:set var="onceFlag" value="0"/>
					<c:forEach begin="1" var="col" items="${row}" varStatus="status">
						<c:set var="columnRef" value="${columnList[status.index - 1]}"/>
						<c:set var="column" value="${columnRef.columnConf}"/>

						<c:set var="nowrap" value=""/>
						<c:set var="align" value=""/>
						<c:set var="bgcolor" value=""/>
						<c:set var="style" value=""/>
						<c:set var="show" value="${column.value ne 'priority'}"/>

						<c:if test="${column.nowrap eq '1'}">
							<c:set var="nowrap" value="nowrap='nowrap'"/>
						</c:if>
						<c:if test="${not empty column.align}">
							<c:set var="align" value="align='${column.align}'"/>
						</c:if>
						<c:if test="${not empty column['style']}">
							<c:set var="style">style="${column['style']}"</c:set>
						</c:if>

						<c:set var="nas" value="${nowrap} ${align} ${style}"/>

						<c:if test="${showCheckColumn and onceFlag ne '1'}">
							<c:set var="onceFlag" value="1"/>
							<td align="center"><input type="checkbox" name="processId" value="${process.id}"/></td>
						</c:if>
						<c:if test="${show}">
							<td ${nas}>${rowData['col'.concat( columnRef.columnId )]}</td>
						</c:if>
					</c:forEach>
				</tr>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</table>

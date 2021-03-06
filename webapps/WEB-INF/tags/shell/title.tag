<%@ tag body-content="empty" pageEncoding="UTF-8" description="Manipulations with shell's title area"%> 
<%@ include file="/WEB-INF/jspf/taglibs.jsp"%>

<%@ attribute name="ltext" description="Plain text, previously localized"%>
<%@ attribute name="text" description="Text for setting, HTML supported"%>

<c:if test="${not empty ltext}">
	<c:set var="text" value="${l.l(ltext)}"/>
</c:if>

<c:if test="${not empty text}">
	<c:set var="title" value="${text.replaceAll('\\\\r', '').replaceAll('\\\\n', ' ')}"/>
</c:if>

<script>
	$(function () {
		$$.shell.debug("title", "${title}");
		$('#title > .status:visible h1.title').html("${title}");
	})
</script>

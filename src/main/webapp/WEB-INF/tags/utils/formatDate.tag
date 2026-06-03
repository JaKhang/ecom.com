<%@ tag body-content="empty" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ tag import="java.time.format.DateTimeFormatter" %>
<%@ attribute name="value" required="true" type="java.time.LocalDateTime" rtexprvalue="true" %>
<%@ attribute name="pattern" required="false" type="java.lang.String" %>

<%
    if (value != null) {
        String patternStr = (pattern != null && !pattern.isEmpty()) ? pattern : "dd/MM/yyyy HH:mm";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternStr);
            out.print(value.format(formatter));
        } catch (Exception e) {
            out.print(value.toString());
        }
    }
%>
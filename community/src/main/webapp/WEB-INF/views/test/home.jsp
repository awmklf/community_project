<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
</head>
<body>
	test : ${result.testVal}
	<form action="community/test/log" method="post">
		<input type="text" name="testVal"> <input type="submit">
		<sec:csrfInput />
	</form>
</body>
</html>

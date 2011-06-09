<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>Posts</title>
		<link rel="stylesheet" href="styles/main.css" media="screen"/>
		<script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
		<script type="text/javascript" src="scripts/posts_structure.js"></script>
	</head>
	<body>
		<h1>Posts</h1>
		
		<ul>
			<c:forEach var="post" items="${posts}">
				<li>
					<h2>${post.title}</h2>
					<span class="author">${post.author}</span>
					<span class="date">${post.postedOn}</span>
					<span class="message">${post.message}</span>
				</li>
			</c:forEach>
		</ul>
		
		<a href="${pageContext.request.contextPath}/">Home</a>
	</body>
</html>
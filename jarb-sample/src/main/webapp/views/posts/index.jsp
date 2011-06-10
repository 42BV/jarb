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
		<script type="text/javascript" src="scripts/posts_submit.js"></script>
	</head>
	<body>
		<h1>Posts</h1>
		
		<section class="posts">
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
		</section>
		
		<section class="status">
			<p>${status}</p>
		</section>
		
		<section class="create">
			<form method="post">
				<p>Create a new post</p>
				<fieldset>
					<label for="author">Author</label>
					<input name="author"/>
				</fieldset>
				<fieldset>
					<label for="title">Title</label>
					<input name="title"/>
				</fieldset>
				<fieldset>
					<label for="message">Message</label>
					<input name="message"/>
				</fieldset>
				<button type="submit">Submit</button>
			</form>
		</section>
		
		<nav>
			<a href="${pageContext.request.contextPath}/">Home</a>
		</nav>
	</body>
</html>
<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>Posts</title>
		<link rel="stylesheet" href="styles/main.css" media="screen"/>
		<script type="text/javascript" src="scripts/jquery.js"></script>
		<script type="text/javascript" src="scripts/jquery.validate.js"></script>
		<script type="text/javascript" src="scripts/posts_structure.js"></script>
		<script type="text/javascript" src="scripts/posts_submit.js"></script>
	</head>
	<body>
	    <header>
	       <h1 id="title">Messages board</h1>
	    </header>
	    
	    <!-- TODO: Make working again. -->
	    <section id="status">
	       <p></p>
	    </section>

		<section class="posts">
		  <table>
		      <thead>
		          <tr>
		              <th>Title</th>
		              <th>Author</th>
		              <th>Date</th>
		              <th>Message</th>
		          </tr>
		      </thead>
		      <tbody>
		          <c:forEach var="post" items="${posts}">
                    <tr>
                        <td>${post.title}</td>
                        <td class="author">${post.author}</td>
                        <td class="date">${post.postedOn}</td>
                        <td class="message">${post.message}</td>
                    </tr>
                </c:forEach>
		      </tbody>
		  </table>
        </section>

        <!-- TODO: The 'posted on' is not mapped to JSON correctly -->
		<section id="create-post">
			<form method="post">
				<h2>Create a new post</h2>
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
		
		<!-- TODO: Make dragable and closable with a yellow background per item -->
		<ul id="notes">
			<li>Form input fields are enriched with backend constraints.</li>
			<li>Try adding a post with a title that already exists.</li>
		</ul>
	</body>
</html>
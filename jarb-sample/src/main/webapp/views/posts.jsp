<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>Posts</title>
		<link rel="stylesheet" href="styles/main.css" media="screen"/>
		<script type="text/javascript" src="scripts/lib/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="scripts/lib/jquery.validate-1.11.1.js"></script>
		<script type="text/javascript" src="scripts/custom/jquery.form.constraints.js"></script>
		<script type="text/javascript" src="scripts/application.js"></script>
	</head>
	<body>
	    <section id="container">
		    <header>
	           <h1 id="title">Messages board <p class="description">JaRB sample</p></h1>
	        </header>
	        
	        <!-- TODO: Make working again. -->
	        <section id="status">
	           <p></p>
	        </section>
	
	        <section class="posts">
	          <h2>Posts</h2>
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
	    </section>
		
		<ul id="notes">
			<li>Form input fields are enriched with backend constraints.</li>
			<li>The author label received an (*) suffix, because it is not null in our database.</li>
			<li>The author input has to be an email address, because it is annotated as @Email.</li>
			<li>Try adding a post with a title that already exists.</li>
		</ul>
	</body>
</html>
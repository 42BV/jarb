$(document).ready(function() {
    var createForm = $('.create form');
    createForm.submit(function(e) {
		$.ajax({  
			type: 'POST',  
			data: createForm.serialize(),
			success: function(data) {
				$('.status p').text(data.status);
				if(data.success) {
					var newPost = '<li>';
					newPost += '<h2>' + data.post.title + '</h2>';
					newPost += '<span class="author">' + data.post.author + '</span>'
					// TODO: Posted on data is provided in milliseconds, convert to correct date format
					newPost += '<span class="date">' + data.post.postedOn + '</span>'
					newPost += '<span class="message">' + data.post.message + '</span>'
					newPost += '</li>'
					$('.posts ul').append(newPost);
					$('.posts li:last').fadeIn();
				}
			}
		});  
		return false;
	});
});
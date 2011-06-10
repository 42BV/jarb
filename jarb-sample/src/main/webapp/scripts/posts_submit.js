$(document).ready(function() {
    var createForm = $('.create form');
    createForm.validate();
    
    createForm.submit(function(e) {
    	// Perform validation checks before posting
    	var validator = createForm.validate();
    	if(validator.form()) {
	    	// Send post to server
			$.ajax({  
				type: 'POST',
				data: createForm.serialize(),
				success: function(data) {
					$('.status p').text(data.status);
					if(data.success) {
						// New post has been accepted accepted
						var newPost = '<li>';
						newPost += '<h2>' + data.post.title + '</h2>';
						newPost += '<span class="author">' + data.post.author + '</span>'
						newPost += '<span class="date">' + data.post.postedOn + '</span>'
						newPost += '<span class="message">' + data.post.message + '</span>'
						newPost += '</li>'
						$('.posts ul').append(newPost);
						$('.posts li:last').fadeIn();
						$(':input', '.create')
							.not(':button, :submit, :reset, :hidden')
							.removeAttr('checked')
							.removeAttr('selected')
							.val('');
						validator.resetForm();
					}
				}
			});  
    	}
		return false;
	});
    
});
$(document).ready(function() {
	
	$('#notes li').click(function() {
		$(this).remove();
	});
	
    $.ajax('/posts', {
    	type: 'GET',
    	success: function(data) {
    		$('.posts tbody').empty();
    		$.each(data, function(index, post) {
    			appendPost(post);
    		});
    	}
    });
    
    var appendPost = function(post) {
		var newPost = '<tr>';
		newPost += '<td>' + post.title + '</td>';
		newPost += '<td class="author">' + post.author + '</td>'
		newPost += '<td class="date">' + post.postedOn + '</td>'
		newPost += '<td class="message">' + post.message + '</td>'
		newPost += '</tr>'
		$('.posts tbody').append(newPost);
    }
    
    var createForm = $('#create-post form');
    createForm.constraints('/posts/constraints');
    createForm.validate(); // Init validator
    
    createForm.submit(function(e) {
    	// Perform validation checks before posting
    	var validator = createForm.validate();
    	if (validator.form()) {
	    	// Send post to server
			$.ajax('/posts', {  
				type: 'POST',
				data: createForm.serialize(),
				success: function(data) {
					$('.status p').text(data.message);
					
					if (data.success) {
						appendPost(data.post);
						$('.posts tbody tr:last').hide();
						$('.posts tbody tr:last').fadeIn();
						
						// Clear creation form
						$(':input', '#create-post')
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
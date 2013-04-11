/**
 * Intercepts create post submits to perform client side validation
 * and add newly created posts to the index without page refreshing.
 * 
 * @author Jeroen van Schagen
 */

$(document).ready(function() {
    var createForm = $('#create-post form');
    
    createForm.constraints('constraints.json');
    createForm.validate(); // Init validator
    
    createForm.submit(function(e) {
    	// Perform validation checks before posting
    	var validator = createForm.validate();
    	if(validator.form()) {
	    	// Send post to server
			$.ajax({  
				type: 'POST',
				data: createForm.serialize(),
				success: function(data) {
					$('.status p').text(data.message);
					
					if(data.success) {
						// New post has been accepted accepted
						// Append post to the index, meaning we dont have to refresh
						var newPost = '<tr>';
						newPost += '<td>' + data.post.title + '</td>';
						newPost += '<td class="author">' + data.post.author + '</td>'
						newPost += '<td class="date">' + data.post.postedOn + '</td>'
						newPost += '<td class="message">' + data.post.message + '</td>'
						newPost += '</tr>'
						$('.posts tbody').append(newPost);
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
/**
 * Retrieve constraint information about posts and apply
 * that information to the creation form.
 * 
 * @author Jeroen van Schagen
 */

$.getJSON('posts/structure.json', function(data) {

	$.each(data.beanConstraintDescription.propertyDescriptions, function(index, propertyDescription) {
		var inputField = $('input[name=' + propertyDescription.name + ']');
		inputField.attr('minlength', propertyDescription.minimumLength);
		inputField.attr('length', propertyDescription.maximumLength);
		if(propertyDescription.required) {
			$('label[for=' + propertyDescription.name + "]").append(" (*)");
			inputField.addClass('required');
		}
		$.each(propertyDescription.types, function(index, propertyType) {
			inputField.addClass(propertyType);
		});
	});

});

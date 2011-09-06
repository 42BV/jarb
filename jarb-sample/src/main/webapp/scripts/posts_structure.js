/**
 * Retrieve constraint information about posts and apply
 * that information to the creation form.
 * 
 * @author Jeroen van Schagen
 */

$.getJSON('posts/structure.json', function(data) {

	$.each(data.beanConstraintDescription.properties, function(index, property) {
		var inputField = $('input[name=' + property.name + ']');
		inputField.attr('minlength', property.minimumLength);
		inputField.attr('length', property.maximumLength);
		if(property.required) {
			$('label[for=' + property.name + "]").append(" (*)");
			inputField.addClass('required');
		}
		$.each(property.types, function(index, propertyType) {
			inputField.addClass(propertyType);
		});
	});

});

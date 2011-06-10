/**
 * Retrieve constraint information about posts and applies
 * that information to the creation form.
 * @author Jeroen van Schagen
 */

$.getJSON('posts/structure.json', function(data) {

	$.each(data.beanConstraintMetadata.propertiesMetadata, function(index, propertyMetadata) {
		var inputField = $('input[name=' + propertyMetadata.name + ']');
		inputField.attr('minlength', propertyMetadata.minimumLength);
		inputField.attr('length', propertyMetadata.maximumLength);
		if(propertyMetadata.required) {
			$('label[for=' + propertyMetadata.name + "]").append(" (*)");
			inputField.addClass('required');
		}
		$.each(propertyMetadata.types, function(index, propertyType) {
			if(propertyType == 'EMAIL') {
				inputField.addClass('email');
			}
		});
	});

});

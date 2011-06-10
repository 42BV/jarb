$.getJSON('posts/structure.json', function(json) {

	$.each(json.beanConstraintMetadata.propertiesMetadata, function(index, propertyMetadata) {
		var inputField = $('input[name=' + propertyMetadata.propertyName + ']');
		inputField.attr('minlength', propertyMetadata.minimumLength);
		inputField.attr('length', propertyMetadata.maximumLength);
		if(propertyMetadata.required) {
			$('label[for=' + propertyMetadata.propertyName + "]").append(" (*)");
			inputField.addClass('required');
		}
	});

});

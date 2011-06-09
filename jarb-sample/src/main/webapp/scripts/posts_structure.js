$.getJSON('http://localhost:8080/jarb-sample/posts/structure.json', function(json) {

	$.each(json.beanConstraintMetadata.propertiesMetadata, function(index, propertyMetadata) {
		var inputField = $('input[name=' + propertyMetadata.propertyName + ']');
		inputField.attr('length', propertyMetadata.maximumLength);
		if(propertyMetadata.required) {
			$('label[for=' + propertyMetadata.propertyName + "]").append(" (*)");
			inputField.addClass('required');
		}
	});

});

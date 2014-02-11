/**
 * Plugin that enhances form input with backend constraint information.
 * 
 * Dependencies:
 * 	jQuery (http://www.jquery.com)
 * 
 * @author Jeroen van Schagen
 */

(function($){  
 $.fn.constraints = function(url, options) {
	 
	var defaults = {  
		requiredSuffix: " (*)" 
	};
	
	var options = $.extend(defaults, options);
	
	var container = this;
	 
	$.getJSON(url, function(data) {
		$.each(data.propertyDescriptions, function(index, propertyDescription) {
			var inputField = container.find('input[name=' + propertyDescription.name + ']');
			var inputLabel = container.find('label[for=' + propertyDescription.name + "]");
			
			inputField.attr('minlength', propertyDescription.minimumLength);
			inputField.attr('length', propertyDescription.maximumLength);
			
			if (propertyDescription.required) {
				inputLabel.addClass('required').append(options.requiredSuffix);
				inputField.addClass('required');
			}
			
			$.each(propertyDescription.types, function(index, propertyType) {
				inputField.addClass(propertyType);
			});
		});
	});
	
 };
})(jQuery);

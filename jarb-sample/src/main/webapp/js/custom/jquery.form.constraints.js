/**
 * Plugin that enhances form input with backend constraint information.
 * 
 * Dependencies:
 * 	jQuery (http://www.jquery.com)
 * 
 * @author Jeroen van Schagen
 */

(function($) {
	$.fn.constraints = function(url, options) {

		var defaults = {
			requiredSuffix : " (*)"
		};

		var options = $.extend(defaults, options);

		var container = this;

		$.getJSON(url, function(data) {
			$.each(data.properties, function(index, property) {
				var inputField = container.find('input[name=' + property.name + ']');
				var inputLabel = container.find('label[for=' + property.name + "]");

				inputField.attr('minlength', property.minimumLength);
				inputField.attr('length', property.maximumLength);

				if (property.required) {
					inputLabel.addClass('required').append(options.requiredSuffix);
					inputField.addClass('required');
				}

				$.each(property.types, function(index, type) {
					inputField.addClass(type);
				});
			});
		});

	};
})(jQuery);

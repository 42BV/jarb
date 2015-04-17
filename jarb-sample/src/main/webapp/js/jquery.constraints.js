/**
 * Plugin that enhances form input with backend constraint information.
 * 
 * Dependencies:
 * 	jQuery (http://www.jquery.com)
 * 
 * @author Jeroen van Schagen
 */
(function($) {
	$.fn.constraints = function(url, args) {

		var defaults = {
		    requiredSuffix : " (*)",
		    possibleTypes : [ 'color', 'datetime-local', 'datetime', 'month', 'week', 'date', 'time', 'email', 'tel', 'number', 'url', 'password', 'file', 'image', 'text' ]
		};

		var options = $.extend(defaults, args);

		var container = this;

		$.getJSON(url, function(data) {
			$.each(data.properties, function(name, property) {
				var input = container.find('input#' + name);
				var label = container.find('label[for=' + name + "]");

				if (property.required) {
					label.addClass('required').append(options.requiredSuffix);
					input.attr('required', 'required').addClass('required');
				}

				input.attr('minlength', property.minimumLength);
				input.attr('length', property.maximumLength);

				input.attr('min', property.min);
				input.attr('max', property.max);

				input.attr('pattern', property.pattern);

				$.each(property.types, function(index, type) {
					input.addClass(type);
				});

				if (!input.attr('type')) {
					for (var index = 0; index < options.possibleTypes.length; index++) {
						var type = options.possibleTypes[index];
						if (property.types.indexOf(type) >= 0) {
							input.attr('type', type);
							break;
						}
					}
				}
			});
		});
	};
})(jQuery);

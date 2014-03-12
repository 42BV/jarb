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
			requiredSuffix : " (*)"
		};

		var options = $.extend(defaults, args);

		var container = this;

		$.getJSON(url, function(data) {
			$.each(data.properties, function(index, property) {
				var input = container.find('input[name=' + property.name + ']');
				var label = container.find('label[for=' + property.name + "]");

				if (property.required) {
					label.addClass('required').append(options.requiredSuffix);
					input.attr('required', 'required').addClass('required');
				}

				input.attr('minlength', property.minimumLength);
				input.attr('length', property.maximumLength);

				// TODO: min, max

				$.each(property.types, function(index, type) {
					input.addClass(type);
				});

				if (!input.attr('type')) {
					if ($.inArray(property.types, 'email')) {
						input.attr('type', 'email');
					}
				}
			});
		});

		/*  
		    TODO: Include all input types
		 
			color
			date
			datetime
			datetime-local
			email
			month
			number
			range?
			search?
			tel
			time
			url
			week
			
			@Temporal
			Date
			LocalDate
		*/

	};
})(jQuery);

var jarb = angular.module('jarb', []);

/**
 * Adapts the constraints plugin with AngularJS.
 */
jarb.directive('constraints', function($http, $compile) {
	return {
	    restrict : 'A',
	    link : function(scope, element, attrs) {
		    scope.$eval(function() {
			    var url = attrs.constraints;
			    $(element).constraints(url);
		    });
	    }
	};
});

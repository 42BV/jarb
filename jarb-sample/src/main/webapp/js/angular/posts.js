var postsApp = angular.module('postsApp', []);
phonecatApp.controller('PostCtrl', function($scope, $http) {

	$scope.message = "";
	$scope.posts = [];
	$scope.newPost = {};

	$scope.create = function() {
		$http.post('/posts').success(function(response) {
			$scope.newPost = {};
		});
	};

});
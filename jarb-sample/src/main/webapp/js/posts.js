var postsApp = angular.module('PostApp', [ 'jarb' ]);
postsApp.controller('PostCtrl', function($scope, $http, $timeout) {

	$scope.message = "";
	$scope.newPost = {};

	$scope.posts = [];
	$http.get('/posts').success(function(posts) {
		$scope.posts = posts;
	});

	$scope.create = function() {
		$http.post('/posts', $scope.newPost).success(function(post) {
			$scope.posts.push(post);
			$scope.newPost = {};
			showMessage("Post was placed");
		}).error(function(data) {
			showMessage(data.error.message);
		});
	};

	var showMessage = function(message) {
		$scope.message = message;
		$timeout(function() {
			$scope.message = "";
		}, 3000);
	}

});
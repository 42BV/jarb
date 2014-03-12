var postsApp = angular.module('PostApp', [ 'jarb' ]);
postsApp.controller('PostCtrl', function($scope, $http) {

	$scope.message = "";
	$scope.newPost = {};

	$scope.posts = [];
	$http.get('/posts').success(function(posts) {
		$scope.posts = posts;
	});

	$scope.create = function() {
		$http.post('/posts', $scope.newPost).success(function(response) {
			$scope.message = response.message;
			if (response.success) {
				$scope.posts.push(response.post);
				$scope.newPost = {};
			}
		});
	};

});
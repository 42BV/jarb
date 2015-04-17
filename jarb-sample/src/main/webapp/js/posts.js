var postsApp = angular.module('PostApp', [ 'jarb' ]);
postsApp.controller('PostCtrl', function($scope, $http, $timeout) {

	$scope.message = {
	    text : '',
	    type : ''
	};

	$scope.newPost = {};

	$scope.posts = [];
	$http.get('/posts').success(function(posts) {
		$scope.posts = posts;
	});

	$scope.create = function() {
		$http.post('/posts', $scope.newPost).success(function(post) {
			angular.forEach($scope.posts, function(post) {
				delete post.created;
			});
			$scope.posts.push(angular.extend(post, { created: true }));
			$scope.newPost = {};
			notify({ text : 'Post was placed', type : 'success' });
		}).error(function(data) {
			if (data.fields) {
				var text = 'Invalid data is provided: ';
				angular.forEach(data.fields, function(field) {
					text += field.propertyPath + '=' + field.message + ', ';
				});
				notify({ text : text, type : 'warning' });
			} else {
				notify({ text : data.error.message, type : 'danger' });
			}
		});
	};

	var notify = function(message) {
		$scope.message = message;
		$timeout(function() {
			$scope.message = "";
		}, 5000);
	};

});
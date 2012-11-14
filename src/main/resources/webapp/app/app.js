var pillReminderModule = angular.module("pillReminder", ["ngResource"]);

function PillReminderDemoController($scope, $resource) {
  $scope.searchResource = $resource("search-patient/:motechId");
  $scope.enrollResource = $resource("enrollment");
  
  $scope.search = {"motechId": ""};
  $scope.patient = {};
  $scope.phonenumber = "";
  $scope.pinnumber = "";
  $scope.errors = [];
  
  $scope.searching = false;
  $scope.enrolling = false;
  
  $scope.searchPatient = function () {
    $scope.searching = true;
    $scope.patient = $scope.searchResource.get($scope.search, function() {
    	$scope.searching = false;
    });
  }
  
  $scope.enroll = function() {
  	$scope.errors = [];
  	
  	var pinPattern = /[0-9]{4}/;
  	if ($scope.pinnumber.length < 4 || !pinPattern.test($scope.pinnumber)) {
  		$scope.errors.push("Pin number must be 4 digits");
  	}

	var phonePattern = /[1-9][0-9]{9}/;
	if ($scope.phonenumber.length < 10 || !phonePattern.test($scope.phonenumber)) {
		$scope.errors.push("Phone number must be 10 digits, and not start with 0");
	}
	
	if ($scope.errors.length > 0) {
		return;
	}
  	
  	var enrollment = {"motechId": $scope.patient.motechId, 
  					  "pin": $scope.pinnumber, 
  					  "phonenumber": $scope.phonenumber};
	$scope.enrolling = true;
	var callback = function() {
        $scope.enrolling = false;
  	};
  	
  	$scope.enrollResource.save(enrollment, callback, callback);
  }
}

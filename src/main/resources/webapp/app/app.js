var pillReminderModule = angular.module("pillReminder", ["ngResource"]);

function TabController($scope) {
  $scope.templateUrl = "enroll";
  $scope.searchSelected = "active";
  $scope.pillReminderListSelected = "";
  
  $scope.switchToListing = function() {
    $scope.templateUrl = "search";
    $scope.searchSelected = "";
    $scope.pillReminderListSelected = "active";
  }
  
  $scope.switchToSearch = function() {
    $scope.templateUrl = "enroll";
    $scope.searchSelected = "active";
    $scope.pillReminderListSelected = "";
  }
}

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
  $scope.lastEnrollment = {};
  
  $scope.searchPatient = function () {
    $scope.searching = true;
    $scope.patient = $scope.searchResource.get($scope.search, function() {
        $scope.searching = false;
    });
  }
  
  $scope.enroll = function() {
  	$scope.errors = [];
  	
  	var pinPattern = /^[0-9]{4}$/;
  	if (!pinPattern.test($scope.pinnumber)) {
  		$scope.errors.push("Pin number must be 4 digits");
  	}

	var phonePattern = /^[1-9][0-9]{10}$/;
	if (!phonePattern.test($scope.phonenumber)) {
		$scope.errors.push("Phone number must be 11 digits, and not start with 0");
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
  	
    $scope.lastEnrollment = $scope.enrollResource.save(enrollment, callback, callback);
  }
}

function SearchController($scope, $resource) {
    $scope.pillReminderResource = $resource("pillreminders/:motechId");
    $scope.pillReminder = {};
    $scope.motechId = "";
    
    $scope.searchPillReminder = function() {
        $scope.pillReminder = $scope.pillReminderResource.get({"motechId": $scope.motechId});
    }
    
    $scope.deletePillReminder = function() {
        $scope.pillReminder = {};
        $scope.pillReminderResource.delete({"motechId": $scope.motechId});
    }
}

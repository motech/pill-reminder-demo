var pillReminderModule = angular.module("pillReminder", ["ngResource"]);

function EnrollController($scope, $resource) {
  $scope.enrollmentResource = $resource("enrollment");
  $scope.enrollment = {"motechId": "", "startTime": ""}
  
  $scope.enrollPatient = function enrollPatient() {
    $scope.enrollmentResource.save($scope.enrollment);
  }
}

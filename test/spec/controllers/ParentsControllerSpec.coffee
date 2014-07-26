'use strict'

describe 'Controller: ParentsCtrl', ($alert) ->

  # load the controller's module
  beforeEach module 'kulebaoAdmin'

  ParentsCtrl = {}
  scope = {}
  $httpBackend = {}

  # Initialize the controller and a mock scope
  beforeEach inject ($controller, $rootScope, classService, schoolService, _$httpBackend_) ->
    scope = $rootScope.$new()
    ParentsCtrl = $controller 'unconnectedParentCtrl', {
      $scope: scope
      classService: classService
      schoolService: schoolService
      $stateParams:
        kindergarten: 93740362
      $alert
    }
    $httpBackend = _$httpBackend_

    $httpBackend.expectGET('/kindergarten/93740362/parent?connected=false')
    .respond [
      id: 1
      name: 'name'
      school_id: 1
      phone: 123
    ]


  it 'should attach a list of parents to the scope', () ->
    $httpBackend.flush()
    expect(scope.parents.length).toBe 1

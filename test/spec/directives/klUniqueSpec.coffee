'use strict'

describe 'Directive: klUnique', () ->

  # load the directive's module
  beforeEach ->
    module 'kulebaoAdmin'

  scope = {}
  $httpBackend = {}

  beforeEach inject ($controller, $rootScope, _$httpBackend_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_


  afterEach ->
    scope.$destroy()

  it 'should compare parent phone by default', inject ($compile) ->
    $httpBackend.expectPOST('api/v1/phone_check/phone')
    .respond(
      {
        error_code: 0
      }
    )
    $httpBackend.expectPOST('api/v1/phone_check/invalid_phone')
    .respond(
      {
        error_code: 1
      }
    )
    scope.parent =
      parent_id: 'parent_id'
      phone: 'phone'
    element = angular.element '<div kl-unique="parent" ng-model="parent.phone"></div>'
    element = $compile(element)(scope)
    scope.$digest()
    scope.parent.phone = 'invalid_phone'
    scope.$digest()
    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false

  it 'should compare employee phone while unique-type is present', inject ($compile) ->
    $httpBackend.expectPOST('api/v1/phone_check/phone?employee=employee')
    .respond(
      {
        error_code: 0
      }
    )
    $httpBackend.expectPOST('api/v1/phone_check/invalid_phone?employee=employee')
    .respond(
      {
        error_code: 1
      }
    )

    scope.employee =
      parent_id: 'employee_id'
      phone: 'phone'

    element = angular.element '<div kl-unique="employee" unique-type="employee" ng-model="employee.phone"></div>'
    element = $compile(element)(scope)
    scope.$digest()
    scope.employee.phone = 'invalid_phone'
    scope.$digest()
    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false

  it 'should compare phone using unique-identity for id field', inject ($compile) ->
    $httpBackend.expectPOST('api/v1/phone_check/phone?employee=employee')
    .respond(
      {
        error_code: 0
      }
    )
    $httpBackend.expectPOST('api/v1/phone_check/invalid_phone?employee=employee')
    .respond(
      {
        error_code: 1
      }
    )
    scope.employee =
      id: 'employee_id'
      phone: 'phone'

    element = angular.element '<div kl-unique="employee" unique-type="employee" unique-identity="id" ng-model="employee.phone"></div>'
    element = $compile(element)(scope)
    scope.$digest()
    scope.employee.phone = 'invalid_phone'
    scope.$digest()
    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false


  it 'should report error where service checking failed', inject ($compile) ->
    $httpBackend.expectPOST('api/v1/phone_check/phone')
    .respond(
      {
        error_code: 0
      }
    )
    $httpBackend.expectPOST('api/v1/phone_check/valid_new_phone')
    .respond(
      {
        error_code: 0
      }
    )

    scope.parent =
      parent_id: 'parent_id'
      phone: 'phone'
    element = angular.element '<div kl-unique="parent" ng-model="parent.phone"></div>'
    element = $compile(element)(scope)
    scope.$digest()
    scope.parent.phone = 'valid_new_phone'
    scope.$digest()
    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe true
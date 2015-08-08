'use strict'

describe 'Directive: klUniqueCard', () ->

# load the directive's module
  beforeEach ->
    module 'kulebaoAdmin'

  scope = {}
  $httpBackend = {}

  beforeEach inject ($controller, $rootScope, _$httpBackend_) ->
    scope = $rootScope.$new()
    $httpBackend = _$httpBackend_

    scope.relationship =
      id: 1
      card: 'initial_card'
      school_id: 123

  afterEach ->
    scope.$destroy()

  it 'should pass card checking according to service', inject ($compile) ->

    $httpBackend.expectGET('api/v3/kindergarten/123/card_check/0123456789?id=1')
    .respond { error_code: 0}

    element = angular.element '<input kl-unique-card="relationship" ng-model="relationship.card" />'
    element = $compile(element)(scope)
    scope.$digest()
    scope.relationship.card = '0123456789'
    scope.$digest()

    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe true
    expect(element.controller('ngModel').$error).isEmpty

  it 'should fail card checking according to service error 1', inject ($compile) ->

    $httpBackend.expectGET('api/v3/kindergarten/123/card_check/0123456789?id=1')
    .respond { error_code: 1}

    element = angular.element '<input kl-unique-card="relationship" ng-model="relationship.card" />'
    element = $compile(element)(scope)
    scope.$digest()
    scope.relationship.card = '0123456789'
    scope.$digest()

    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false
    expect(element.controller('ngModel').$error.unique).toBe true


  it 'should fail card checking according to service error 2', inject ($compile) ->
    $httpBackend.expectGET('api/v3/kindergarten/123/card_check/0123456789?id=1')
    .respond { error_code: 2}

    element = angular.element '<input kl-unique-card="relationship" ng-model="relationship.card" />'
    element = $compile(element)(scope)
    scope.$digest()
    scope.relationship.card = '0123456789'
    scope.$digest()

    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false
    expect(element.controller('ngModel').$error.unique).toBe true

  it 'should fail card checking according to service error 3', inject ($compile) ->
    $httpBackend.expectGET('api/v3/kindergarten/123/card_check/0123456789?id=1')
    .respond { error_code: 3}

    element = angular.element '<input kl-unique-card="relationship" ng-model="relationship.card" />'
    element = $compile(element)(scope)
    scope.$digest()
    scope.relationship.card = '0123456789'
    scope.$digest()
    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false
    expect(element.controller('ngModel').$error.registered).toBe true


  it 'should fail check card by default', inject ($compile) ->
    $httpBackend.expectGET('api/v3/kindergarten/123/card_check/0123456789?id=1')
    .respond 500, {}

    element = angular.element '<input kl-unique-card="relationship" ng-model="relationship.card" />'
    element = $compile(element)(scope)
    scope.$digest()
    scope.relationship.card = '0123456789'
    scope.$digest()


    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe false
    expect(element.controller('ngModel').$error.unique).toBe true
    expect(element.controller('ngModel').$error.registered).toBe true

  it 'should not pass id if no id present', inject ($compile) ->
    $httpBackend.expectGET('api/v3/kindergarten/123/card_check/0123456789')
    .respond { error_code: 0}

    scope.relationship =
      card: 'initial_card'
      school_id: 123

    element = angular.element '<input kl-unique-card="relationship" ng-model="relationship.card" />'
    element = $compile(element)(scope)
    scope.$digest()
    scope.relationship.card = '0123456789'
    scope.$digest()


    $httpBackend.flush()

    expect(element.controller('ngModel').$valid).toBe true
    expect(element.controller('ngModel').$error).isEmpty

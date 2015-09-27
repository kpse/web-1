describe 'Controller: RelationshipController', ($alert) ->

  # load the controller's module
  beforeEach module 'kulebaoAdmin'

  controller = {}
  $scope = {}
  $rootScope = {}
  $controller = {}
  $httpBackend = {}
  $stateParams = {}

  beforeEach inject (_$controller_, _$rootScope_, _$httpBackend_, _$stateParams_) ->
    $controller = _$controller_
    $rootScope = _$rootScope_
    $httpBackend = _$httpBackend_
    $stateParams = _$stateParams_

  it 'should extract relationships from excel data', () ->
    $scope = $rootScope.$new()

    batchImportCtrl = $controller 'batchImportCtrl', {
      $scope: $scope
      $stateParams:
        kindergarten: 93740362
    }

    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '班级': '二班', '学生姓名': '二宝'])

    $httpBackend.expectPOST('api/v1/kindergarten/93740362/phone_check/12345678991')
    .respond phoneNumberIsFine

    $httpBackend.flush()

    expect($scope.relationships.length).toBe 1
    expect($scope.classesScope.length).toBe 1

  phoneNumberIsFine = ->
    error_code: 0
  classOfId = (id) ->
    "class_id": id
    "class_name": "name"
  chargeOfSchool = (schoolId) ->
    "school_id": schoolId,
    "total_phone_number": 5,
    "expire_date": "2014-08-01",
    "status": 1,
    "used": 6,
    "total_video_account": 10,
    "video_user_name": "username",
    "video_user_password": "password"

  videoMemberOfId = (id) ->
    "phone": "12345678998",
    "account": "account",
    "password": "password"
    "id" : id
  parentOfPhone = (phone) ->
    "school_id": 1,
    "phone": phone
    "parent_id": "1_2_3"
    "name": "display name"
  relationshipOfPhone = (phone) ->
    "school_id": 1,
    "parent" : parentOfPhone phone
    "child" :
      "child_id": "2_3_1"
      "class_id": 1
      "class_name": '二班'
      "name": '二宝'
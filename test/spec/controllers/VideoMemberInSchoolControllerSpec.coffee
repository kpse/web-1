'use strict'

describe 'Controller: VideoMemberManagementCtrl', ($alert) ->

  # load the controller's module
  beforeEach module 'kulebaoAdmin'

  videoMemberController = {}
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

    $httpBackend.expectGET('/kindergarten/93740362/class')
    .respond [
      classOfId 1
    ]
    $httpBackend.expectGET('/kindergarten/93740362/charge')
    .respond [
      chargeOfSchool 93740362
    ]
    $httpBackend.expectGET('/api/v1/kindergarten/93740362/video_member')
    .respond [
      videoMemberOfId "1_2_3"
    ]
    $httpBackend.expectGET('/kindergarten/93740362/sender/1_2_3?type=p')
    .respond parentOfPhone "12345678998"

    $httpBackend.expectGET('/kindergarten/93740362/relationship?parent=12345678998')
    .respond [
      relationshipOfPhone "12345678998"
    ]


  it 'should attach a list of parents to the scope', () ->
    $scope = $rootScope.$new()

    videoMemberController = $controller 'VideoMemberManagementCtrl', {
      $scope: $scope
      $stateParams:
        kindergarten: 93740362
    }
    $httpBackend.flush()

    $scope.onSuccess(sheet1: ['家长手机号': '12345678991', '家长姓名': 'display name', '班级': '二班', '学生姓名': '二宝'])

    $httpBackend.expectGET('/kindergarten/93740362/parent')
    .respond [
      parentOfPhone "12345678998"
      parentOfPhone "12345678991"
    ]

    $httpBackend.expectGET('/kindergarten/93740362/relationship')
    .respond [
      relationshipOfPhone "12345678998"
      relationshipOfPhone "12345678991"
    ]
    $httpBackend.flush()

    expect($scope.importingData.length).toBe 1

  it 'should attach no parent when no valid data', () ->
    $scope = $rootScope.$new()

    videoMemberController = $controller 'VideoMemberManagementCtrl', {
      $scope: $scope
      $stateParams:
        kindergarten: 93740362
    }
    $httpBackend.flush()

    spyOn(window, 'alert');
    $scope.onSuccess({})

    expect(window.alert).toHaveBeenCalledWith('导入文件格式错误，每行至少要包含‘家长手机号’，‘家长姓名’， ‘班级’，‘学生姓名’列，请检查excel内容。');
    expect($scope.importingData).toBeUndefined()

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
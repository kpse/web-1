#Jasmine test, see http://pivotal.github.com/jasmine/for more information
describe 'VideoMemberController', ->
  beforeEach module 'kulebaoOp'

  $httpBackend = {}
  $controller = {}
  $rootScope = {}
  $stateParams = {}

  beforeEach inject (_$controller_, _$rootScope_, _$httpBackend_, _$stateParams_) ->
    $controller = _$controller_
    $httpBackend = _$httpBackend_
    $rootScope = _$rootScope_
    $stateParams = _$stateParams_



  describe 'excel exporting', ->
    it 'should export all video account', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()
      $stateParams.school_id = 1
      $stateParams.class_id = 1

      controller = $controller('OpVideoMemberInClassCtrl', $scope: $scope, $rootScope: rootScope)
      prepareForRefreshing()
      $httpBackend.flush()

      result = $scope.accountsInSchool($scope.parents)

      expect(result.length).toBe 1
      expect(_.first(result).account).toBe 'account'
      expect(_.first(result).password).toBe 'password'
      expect(_.first(result).name).toBe '13060003721' + "display name"

    it 'should ignore video accounts in another class', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()
      $stateParams.school_id = 1
      $stateParams.class_id = 2

      controller = $controller('OpVideoMemberInClassCtrl', $scope: $scope, $rootScope: rootScope)
      prepareForRefreshing()
      $httpBackend.flush()

      result = $scope.accountsInSchool($scope.parents, 2)

      expect(result.length).toBe 0

    it 'should export video accounts in matched class', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()
      $stateParams.school_id = 1
      $stateParams.class_id = 1

      controller = $controller('OpVideoMemberInClassCtrl', $scope: $scope, $rootScope: rootScope)
      prepareForRefreshing()
      $httpBackend.flush()

      result = $scope.accountsInSchool($scope.parents, 1)

      expect(result.length).toBe 1

  videoMemberOfPhone = (phone) ->
    "phone": phone,
    "account": "account",
    "password": "password"

  parentOfPhone = (phone) ->
    "school_id": 1,
    "phone": phone
    "parent_id": "1_2_3"
    "name": "display name"

  schoolOfId = (id) ->
    "school_id": id,
    "phone": "13991855476",
    "timestamp": 1387649057933,
    "desc": "1",
    "school_logo_url": "url",
    "name": "第三军区幼儿园",
    "token": "123",
    "full_name": "成都市第三军区幼儿园",
    "properties": [{"name": "backend", "value": "false"}, {"name": "video_user_name", "value": "username"},
      {"name": "video_user_password", "value": "password"}],
    "created_at": 0
  classOfId = (id) ->
    "class_id": id
    "class_name": "name"
  relationshipOfPhone = (phone) ->
    "school_id": 1,
    "parent" : parentOfPhone phone
    "child" :
      "child_id": "2_3_1"
      "class_id": 1


  prepareForRefreshing = ->
    $httpBackend.expectGET('/kindergarten/1')
    .respond schoolOfId 1


    $httpBackend.expectGET('/kindergarten/1/class')
    .respond [
      classOfId 1
    ]

    $httpBackend.expectGET('/api/v1/kindergarten/1/video_member')
    .respond [ videoMemberOfPhone '13060003721' ]

    $httpBackend.expectGET('/kindergarten/1/sender?type=p')
    .respond parentOfPhone '13060003721'

    $httpBackend.expectGET('/kindergarten/1/relationship?parent=13060003721')
    .respond [relationshipOfPhone '13060003721']






#Jasmine test, see http://pivotal.github.com/jasmine/for more information
describe 'SchoolManageController', ->
  beforeEach module 'kulebaoOp'

  $httpBackend = {}
  $controller = {}
  $rootScope = {}

  beforeEach inject (_$controller_, _$rootScope_, _$httpBackend_) ->
    $controller = _$controller_
    $httpBackend = _$httpBackend_
    $rootScope = _$rootScope_



  describe 'loading', ->
    it 'should retrieve all schools', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()

      controller = $controller('OpSchoolCtrl', $scope: $scope, $rootScope: rootScope)
      prepareForRefreshing()
      $httpBackend.flush()

      expect($scope.kindergartens.length).toEqual 3

  describe 'editing school', ->
    it 'should update school config', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()

      controller = $controller('OpSchoolCtrl', $scope: $scope, $rootScope: rootScope)
      prepareForRefreshing()
      $httpBackend.flush()

      $scope.editSchool({"school_id":2})
      $httpBackend.expectGET('/kindergarten/2/charge')
      .respond [
        {"school_id":2,"total_phone_number":5,"expire_date":"2014-08-01","status":1,"used":6,"total_video_account":10,"video_user_name":"username","video_user_password":"password"}
      ]
      $httpBackend.expectGET('templates/op/edit_school.html')
      .respond '<div></div>'
      $httpBackend.flush()

      $scope.endEditing({"school_id":2, config: {
        backend: 'true'
        hideVideo: 'true'
        disableMemberEditing: 'true'
        bus: 'true'}})

      $httpBackend.expectPOST('/kindergarten/2', {"school_id":2,"config":{"backend":"true","hideVideo":"true","disableMemberEditing":"true","bus":"true"},"properties":[{"name":"backend","value":"true"},{"name":"hideVideo","value":"true"},{"name":"disableMemberEditing","value":"true"},{"name":"bus","value":"true"}]})
      .respond {error_code: 0}
      prepareForRefreshing()
      $httpBackend.flush()

  employeeOfSchool = (phone, schoolId=1) ->
    "id": "3_93740362_9971",
    "name": "蜘蛛侠",
    "phone": phone,
    "gender": 1,
    "workgroup": "保安组",
    "workduty": "员工",
    "portrait": "",
    "birthday": "1982-06-04",
    "school_id": schoolId,
    "login_name": "admin",
    "timestamp": 0,
    "privilege_group": "principal",
    "status": 1,
    "created_at": 0,
    "uid": 5
  chargeOfSchool = (schoolId) ->
    "school_id": schoolId,
    "total_phone_number": 5,
    "expire_date": "2014-08-01",
    "status": 1,
    "used": 6,
    "total_video_account": 10,
    "video_user_name": "username",
    "video_user_password": "password"
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
  configOfSchool = (schoolId) ->
    "school_id": schoolId,
    "config": [{"name": "backend", "value": "false"}, {"name": "video_user_name", "value": "username"},
      {"name": "video_user_password", "value": "password"}]


  prepareForRefreshing = ->
    $httpBackend.expectGET('/api/v2/kindergarten_preview')
    .respond [
      {id: 1, school_id: 1, timestamp: 0},
      {id: 2, school_id: 2, timestamp: 0},
      {id: 3, school_id: 3, timestamp: 0}
    ]


    $httpBackend.expectGET('/api/v2/kindergarten?from=0&most=8')
    .respond [
      schoolOfId 1
      schoolOfId 2
      schoolOfId 3
    ]

    $httpBackend.expectGET('/employee')
    .respond [ employeeOfSchool '13060003721' ]

    $httpBackend.expectGET('/kindergarten/1/principal')
    .respond [ employeeOfSchool '13060003722', employeeOfSchool '13258249821']

    $httpBackend.expectGET('/kindergarten/1/charge')
    .respond [chargeOfSchool 1]


    $httpBackend.expectGET('/api/v2/school_config/1')
    .respond configOfSchool 1

    $httpBackend.expectGET('/kindergarten/2/principal')
    .respond [employeeOfSchool('13060003723', 2)]

    $httpBackend.expectGET('/kindergarten/2/charge')
    .respond [chargeOfSchool 2]

    $httpBackend.expectGET('/api/v2/school_config/2')
    .respond configOfSchool 2

    $httpBackend.expectGET('/kindergarten/3/principal')
    .respond [employeeOfSchool('13060003724', 3)]

    $httpBackend.expectGET('/kindergarten/3/charge')
    .respond [chargeOfSchool 3]

    $httpBackend.expectGET('/api/v2/school_config/3')
    .respond configOfSchool 3

    $httpBackend.expectGET('/employee/13060003722')
    .respond employeeOfSchool('13060003722')

    $httpBackend.expectGET('/employee/13060003723')
    .respond employeeOfSchool('13060003723', 2)

    $httpBackend.expectGET('/employee/13060003724')
    .respond employeeOfSchool('13060003724', 3)




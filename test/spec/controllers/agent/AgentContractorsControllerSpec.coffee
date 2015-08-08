#Jasmine test, see http://pivotal.github.com/jasmine/for more information
describe 'AgentContractorsController', ->
  beforeEach module 'kulebaoAgent'

  $httpBackend = {}
  $controller = {}
  $rootScope = {}

  currentAgent = {id: 1, schools:[{school_id: 1}, {school_id: 2}]}
  Agent = {}
  User = {}
  loggedUser = {}

  beforeEach inject (_$controller_, _$rootScope_, _$httpBackend_) ->
    $controller = _$controller_
    $httpBackend = _$httpBackend_
    $rootScope = _$rootScope_


  describe 'loading', ->
    it 'should retrieve all contractors', ->
      $scope = $rootScope.$new()
      $scope.waitForSchoolsReady = ->
      rootScope = $rootScope.$new()

      controller = $controller('AgentContractorsCtrl', $scope: $scope, $rootScope: rootScope, currentAgent: currentAgent, Agent: Agent, User: User, loggedUser: loggedUser)
      prepareForRefreshing()
      $httpBackend.flush()

      expect($scope.schools.length).toEqual 2
      expect($scope.contractors.length).toEqual 2

  describe 'connecting', ->
    it 'should add new schools into contractor', ->
      $scope = $rootScope.$new()
      $scope.waitForSchoolsReady = ->
      rootScope = $rootScope.$new()

      controller = $controller('AgentContractorsCtrl', $scope: $scope, $rootScope: rootScope, currentAgent: currentAgent, Agent: Agent, User: User, loggedUser: loggedUser)
      prepareForRefreshing()
      $httpBackend.flush()

      $scope.distribute(contractor2)
      $scope.connect({"school_id":2}, contractor2)

      $httpBackend.expectPOST('/api/v4/agent/1/kindergarten/2/contractor')
      .respond 200
      $httpBackend.expectGET('templates/agent/distribute_to_school.html')
      .respond '<div></div>'

      $httpBackend.flush()

  describe 'disconnecting', ->
    it 'should remove school from contractor', ->
      $scope = $rootScope.$new()
      $scope.waitForSchoolsReady = ->
      rootScope = $rootScope.$new()

      controller = $controller('AgentContractorsCtrl', $scope: $scope, $rootScope: rootScope, currentAgent: currentAgent, Agent: Agent, User: User, loggedUser: loggedUser)
      prepareForRefreshing()
      $httpBackend.flush()

      $scope.distribute(contractor2)
      $scope.disconnect($scope.schools[1], contractor2)

      $httpBackend.expectDELETE('/api/v4/agent/1/kindergarten/2/contractor/4')
      .respond 200
      $httpBackend.expectGET('templates/agent/distribute_to_school.html')
      .respond '<div></div>'

      $httpBackend.flush()

  prepareForRefreshing = ->
    $httpBackend.expectGET('/api/v4/agent/1/contractor')
    .respond [contractor2, contractor1]

    $httpBackend.expectGET('/api/v4/agent/1/activity')
    .respond [activity1, activity2]

    $httpBackend.expectGET('/api/v4/agent/1/kindergarten/1/active')
    .respond {"school_id":1,"activated":6,"all":9,"member":8,"video":2,"check_in_out":0,"children":600}
    $httpBackend.expectGET('/api/v4/agent/1/kindergarten/2/active')
    .respond {"school_id":2,"activated":0,"all":9,"member":8,"video":2,"check_in_out":0,"children":500}
    $httpBackend.expectGET('/api/v4/agent/1/kindergarten/1/contractor')
    .respond [
      {"id":1,"agent_id":1,"contractor_id":1,"school_id":1,"updated_at":1393395313123}
      {"id":2,"agent_id":1,"contractor_id":2,"school_id":1,"updated_at":1393395313123}
    ]
    $httpBackend.expectGET('/api/v4/agent/1/kindergarten/2/contractor')
    .respond [
      {"id":3,"agent_id":1,"contractor_id":1,"school_id":2,"updated_at":1393395313123}
      {"id":4,"agent_id":1,"contractor_id":2,"school_id":2,"updated_at":1393395313123}
    ]

  contractor2 = {"id":2,"agent_id":1,"category":"培训教育","title":"悲剧","address":"上海","contact":"13333452147","time_span":"2015-12-02~2016-12-03","detail":"要悲剧","logo":"","updated_at":1393399313123,"publishing":{"publish_status":3,"published_at":0,"reject_reason":"名字太长"},"location":{"latitude":123.231,"longitude":321.123,"address":""}}
  contractor1 = {"id":1,"agent_id":1,"category":"其他","title":"雅芳","address":"天津","contact":"13333652147","time_span":"2015-07-02~2015-08-03","detail":"去广告中心，领免费机票","logo":"","updated_at":1393395313123,"publishing":{"publish_status":2,"published_at":1393395313123},"location":{"latitude":123.231,"longitude":321.123,"address":""}}

  activity2 = {
    "id": 2,
    "agent_id": 1,
    "contractor_id": 1,
    "title": "T下线",
    "address": "四川",
    "contact": "13333653147",
    "detail": "特斯拉要不要",
    "logo": "",
    "updated_at": 1393399313123,
    "publishing": {"publish_status": 5, "published_at": 1393399313123},
    "price": {"origin": 10000.0, "discounted": 5000.0}
  }
  activity1 = {
    "id": 1,
    "agent_id": 1,
    "contractor_id": 1,
    "title": "T上线",
    "address": "四川",
    "contact": "13333653147",
    "detail": "特斯拉要不要",
    "logo": "",
    "updated_at": 1393399313123,
    "publishing": {"publish_status": 4, "published_at": 1393399313123},
    "price": {"origin": 10000.0, "discounted": 5000.0}
  }


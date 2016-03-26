#Jasmine test, see http://pivotal.github.com/jasmine/for more information
describe 'OpReportingCtrl', ->
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



  describe 'reporting page filter', ->
    it 'should display all while filter condition is empty', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()

      controller = $controller('OpReportingCtrl', $scope: $scope, $rootScope: rootScope)

      result = $scope.contentSearch({school_id: 123, full_name: 'fullname'})

      expect(result).toBe true

    it 'should display all while filter condition is matched', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()
      $scope.searchText = '12'

      controller = $controller('OpReportingCtrl', $scope: $scope, $rootScope: rootScope)

      result = $scope.contentSearch({school_id: 123, full_name: 'fullname'})

      expect(result).toBe true

    it 'should display none while filter condition is not matched', ->
      $scope = $rootScope.$new()
      rootScope = $rootScope.$new()
      $scope.searchText = '32'

      controller = $controller('OpReportingCtrl', $scope: $scope, $rootScope: rootScope)

      result = $scope.contentSearch({school_id: 123, full_name: 'fullname'})

      expect(result).toBe false






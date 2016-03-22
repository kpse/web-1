describe 'Controller: RelationshipController', ($alert) ->

  # load the controller's module
  beforeEach module 'kulebaoAdmin'
  beforeEach module ($urlRouterProvider) ->
    $urlRouterProvider.deferIntercept()

  $scope = {}
  $rootScope = {}
  $controller = {}
  $httpBackend = {}
  $stateParams = {}
  $state = {}

  beforeEach inject (_$controller_, _$rootScope_, _$httpBackend_, _$stateParams_) ->
    $controller = _$controller_
    $rootScope = _$rootScope_
    $httpBackend = _$httpBackend_
    $stateParams = _$stateParams_
    $state =
      go : (data) -> console.log 'go : ' + data
      reload : () -> console.log 'reload state'

    $scope = $rootScope.$new()
    $scope.backend = true
    $scope.kindergarten =
      classes: [
        name: '二班'
        class_id: 1000
      ]
    batchImportCtrl = $controller 'batchImportCtrl', {
      $scope: $scope
      $stateParams:
        kindergarten: '93740362'
        type: 'batchImport'
      $state: $state
    }

  it 'should extract relationships from excel data', () ->
    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '干爹'])

    $httpBackend.expectPOST('api/v1/phone_check/12345678991').respond phoneNumberIsFine
    $httpBackend.expectPOST('/api/v1/kindergarten/93740362/child_name_check?name=%E4%BA%8C%E5%AE%9D').respond nameIsFine
    $httpBackend.flush()

    expect($scope.relationships.length).toBe 1
    expect($scope.relationships[0].relationship).toBe '干爹'
    expect($scope.classesScope.length).toBe 1
    expect($scope.importingErrorMessage).toBe ''
    expect($scope.errorItems.length).toBe 0

  it 'should accept parents with multiple children ', () ->

    $scope.onSuccess(sheet1: [
      {'家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'}
      {'家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '三宝', '家长A亲属关系': '爸爸'}
    ])

    $httpBackend.expectPOST('api/v1/phone_check/12345678991').respond phoneNumberIsFine
    $httpBackend.expectPOST('api/v1/phone_check/12345678991').respond phoneNumberIsFine
    $httpBackend.expectPOST('/api/v1/kindergarten/93740362/child_name_check?name=%E4%BA%8C%E5%AE%9D').respond nameIsFine
    $httpBackend.expectPOST('/api/v1/kindergarten/93740362/child_name_check?name=%E4%B8%89%E5%AE%9D').respond nameIsFine
    $httpBackend.flush()

    expect($scope.relationships.length).toBe 2
    expect($scope.classesScope.length).toBe 1
    expect($scope.importingErrorMessage).toBe ''
    expect($scope.errorItems.length).toBe 0

  it 'should warning duplicated parents name from excel', () ->

    $scope.onSuccess(sheet1: [
      {'家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'}
      {'家长A手机号': '12345678992', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '三宝', '家长A亲属关系': '爸爸'}
    ])

    expect($scope.importingErrorMessage).toBe '以下家长姓名重复但是手机号不一样，请修正后重新导入。'
    expect($scope.errorItems.length).toBe 1

  it 'should warning duplicated children name from excel', () ->

    $scope.onSuccess(sheet1: [
      {'家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'}
      {'家长A手机号': '12345678992', '家长A姓名': 'display name2', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'}
    ])

    expect($scope.importingErrorMessage).toBe '以下小孩名字重复，请修正后重新导入。'
    expect($scope.errorItems[0]).toBe '二宝 分别出现在第 1,2 行。'

  it 'should warning empty parent phone from excel', () ->

    $scope.onSuccess(sheet1: [
      {'家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'},
      {'家长A姓名': 'display name2', '所属班级': '二班', '宝宝姓名': 'sa宝', '家长A亲属关系': '爸爸'}
    ])

    expect($scope.importingErrorMessage).toBe '以下家长没有提供手机号，请修正后重新导入。'
    expect($scope.errorItems.length).toBe 2

  it 'should warning empty parent phone from excel', () ->

    $scope.onSuccess(sheet1: [
      {'家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'}
      {'家长A手机号': '12345678992', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': 'sa宝', '家长A亲属关系': '爸爸'}
      {'家长A手机号': '12345678992', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': 'saa宝', '家长A亲属关系': '爸爸'}
    ])

    expect($scope.importingErrorMessage).toBe '以下家长姓名重复但是手机号不一样，请修正后重新导入。'
    expect($scope.errorItems.length).toBe 1

  it 'should warning non existing class name from excel', () ->

    $scope.onSuccess(sheet1: [
      {'家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '九班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'}
    ])

    expect($scope.importingErrorMessage).toBe '以下班级当前并不存在，请检查调整后重新输入。'
    expect($scope.errorItems.length).toBe 1

  it 'should warning existing phone number from excel', () ->

    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'])

    $httpBackend.expectPOST('api/v1/phone_check/12345678991').respond phoneNumberIsDuplicated
    $httpBackend.flush()

    expect($scope.importingErrorMessage).toBe '以下手机号码已经存在，请检查调整后重新输入。'
    expect($scope.errorItems[0]).toBe '12345678991'

  it 'should warning wrong birthday format from excel', () ->

    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸', '出生日期': '11/11/2000'])

    expect($scope.importingErrorMessage).toBe '以下学生生日格式不正确，请确保所有生日的格式都是2015-01-31。'
    expect($scope.errorItems.length).toBe 1

  it 'should warning for empty class name', () ->

    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'])

    expect($scope.importingErrorMessage).toBe '以下小孩没有分配班级，请检查调整后重新输入。'
    expect($scope.errorItems.length).toBe 1

  it 'should warning for empty child name', () ->

    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '宝宝姓名': '', '所属班级': '二班', '家长A亲属关系': '爸爸'])

    expect($scope.importingErrorMessage).toBe '以下家长的孩子名字为空，请检查调整后重新输入。'
    expect($scope.errorItems.length).toBe 1

  it 'should warning for empty child name 2', () ->

    $scope.onSuccess(sheet1: ['家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '家长A亲属关系': '爸爸'])

    expect($scope.importingErrorMessage).toBe '以下家长的孩子名字为空，请检查调整后重新输入。'
    expect($scope.errorItems.length).toBe 1

  it 'should accept multiple parents for single child from excel', () ->

    $scope.onSuccess(sheet1: [
      '家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸','家长B手机号': '12345678992', '家长B姓名': 'display name2', '家长B亲属关系': '妈妈'
    ])

    $httpBackend.expectPOST('api/v1/phone_check/12345678991').respond phoneNumberIsFine
    $httpBackend.expectPOST('api/v1/phone_check/12345678992').respond phoneNumberIsFine
    $httpBackend.expectPOST('/api/v1/kindergarten/93740362/child_name_check?name=%E4%BA%8C%E5%AE%9D').respond nameIsFine
    $httpBackend.flush()

    expect($scope.relationships.length).toBe 2
    expect($scope.relationships[0].relationship).toBe '爸爸'
    expect($scope.relationships[1].relationship).toBe '妈妈'
    expect($scope.relationships[0].child.id).toBe $scope.relationships[1].child.id

  it 'should warning child name is already existing', () ->

    $scope.onSuccess(sheet1: [
      '家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸','家长B手机号': '12345678992', '家长B姓名': 'display name2', '家长B亲属关系': '妈妈'
    ])

    $httpBackend.expectPOST('api/v1/phone_check/12345678991').respond phoneNumberIsFine
    $httpBackend.expectPOST('api/v1/phone_check/12345678992').respond phoneNumberIsFine
    $httpBackend.expectPOST('/api/v1/kindergarten/93740362/child_name_check?name=%E4%BA%8C%E5%AE%9D').respond nameIsDuplicated
    $httpBackend.flush()

    expect($scope.importingErrorMessage).toBe '以下学生名字在学校中已经存在，无法判断是否同一学生，建议重名学生使用快速创建功能，避免信息冲突。'
    expect($scope.errorItems.length).toBe 1

  it 'should accept all child name when no backend present', () ->

    $scope.backend = false
    $scope.onSuccess(sheet1: [
      '家长A手机号': '12345678991', '家长A姓名': 'display name', '所属班级': '二班', '宝宝姓名': '二宝', '家长A亲属关系': '爸爸'
    ])

    $httpBackend.expectPOST('api/v1/kindergarten/93740362/phone_check/12345678991').respond phoneNumberIsFine

    $httpBackend.flush()

    expect($scope.relationships.length).toBe 1
    expect($scope.relationships[0].relationship).toBe '爸爸'
    
    $scope.applyAllChange()

    $httpBackend.expectDELETE('/kindergarten/93740362/class').respond error_code: 0
    $httpBackend.expectPOST('/kindergarten/93740362/class/1000').respond error_code: 0
    $httpBackend.expectPOST('/api/v1/batch_import/93740362/parents').respond error_code: 0
    $httpBackend.expectPOST('/api/v1/batch_import/93740362/children').respond error_code: 0
    $httpBackend.expectPOST('/api/v1/batch_import/93740362/relationships').respond error_code: 0
    $httpBackend.flush()

    expect($scope.relationships.length).toBe 1


  phoneNumberIsDuplicated =
    error_code: 1
  phoneNumberIsFine =
    error_code: 0
  nameIsDuplicated =
    error_code: 1
  nameIsFine =
    error_code: 0
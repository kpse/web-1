angular.module('kulebaoOp').controller 'OpAgentManagementCtrl',
  ['$scope', '$rootScope', '$filter', 'agentManagementService', '$modal', 'principalService', 'allEmployeesService',
   '$resource', 'chargeService', 'adminCreatingService', '$alert', '$location', 'agentSchoolService', 'schoolService',
    'agentSchoolService',
    (scope, rootScope, $filter, Agent, Modal, Principal, Employee, $resource, Charge, AdminCreating, Alert, location,
     School, AllSchool, AgentSchool) ->
      rootScope.tabName = 'agent'

      scope.refresh = ->
        scope.agents = Agent.query ->
          _.forEach scope.agents, (a) ->
            a.schools = School.query a
            a.expireDisplayValue = $filter('date')(a.expire, 'yyyy-MM-dd')
            a.schoolIds = AgentSchool.query a, ->
              a.schools = _.map a.schoolIds, (kg) -> AllSchool.get(school_id: kg.school_id)
        scope.kindergartens = AllSchool.query()


      scope.refresh()



      scope.editAgent = (agent) ->
        scope.currentAgent = angular.copy agent
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_agent.html'

      scope.goChargePage = ->
        location.path '/main/charge'

      scope.endEditing = (kg) ->
        Agent.save kg, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.addSchool = (agent) ->
        scope.currentAgent = angular.copy agent
        partition = _.partition scope.kindergartens, (kg) ->
          _.find scope.currentAgent.schools, (k) ->
            k.school_id == kg.school_id
        scope.unSelectedSchools = partition[1]
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/connect_school.html'

      scope.deleteAgent = (agent)->
        agent.$delete ->
          scope.refresh()


      scope.newAgent = ->
        new Agent
          name: ''
          area: ''
          phone: ''
          login_name: ''
          expire: new Date("#{new Date().getFullYear() + 1}-01-01")
          logo_url: ''
          contact_info: ''
          memo: ''


      scope.addAgent = ->
        scope.currentAgent = scope.newAgent()
        scope.currentModal = Modal
          scope: scope
          contentTemplate: 'templates/op/add_agent.html'

      handleError = (res) ->
        Alert
          title: '无法保存代理商信息'
          content: if res.data.error_msg? then res.data.error_msg else res.data
          placement: "top"
          type: "danger"
          show: true
          container: '.modal-dialog .panel-body'
          duration: 3

      scope.saveAgent = (agent) ->
        agent.expire = new Date(agent.expireDisplayValue).getTime()
        Agent.save agent, ->
          scope.refresh()
          scope.currentModal.hide()
        , (res) ->
          handleError res

      scope.advancedEdting = false
      scope.advanced = ->
        scope.advancedEdting = true
      scope.simpleDialog = ->
        scope.advancedEdting = false

      scope.addSchools = (agent) ->


      scope.checkAll = (check) ->
        _.forEach scope.kindergartens, (r) -> r.checked = check

      scope.checkAgentAll = (check) ->
        _.forEach scope.kindergartens, (r) -> r.checked = check

      scope.multipleDelete = ->
        checked = _.filter scope.kindergartens, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (c) -> Child.delete(school_id: stateParams.kindergarten, child_id: c.child_id).$promise
        all = $q.all queue
        all.then (q) ->
          scope.refreshChildren()

      scope.hasSelection = (kindergartens) ->
        _.some kindergartens, (r) -> r.checked? && r.checked == true

      scope.singleSelection = (kg) ->
        allChecked = _.every scope.kindergartens, (r) -> r.checked? && r.checked == true
        scope.selection.allCheck = allChecked

      scope.selection =
        allCheck: false
        allAgentCheck: false

  ]


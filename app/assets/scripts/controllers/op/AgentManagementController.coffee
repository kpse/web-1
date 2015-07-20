angular.module('kulebaoOp').controller 'OpAgentManagementCtrl',
  ['$scope', '$rootScope', '$filter', '$q', 'agentManagementService', '$modal', 'principalService',
   'allEmployeesService', '$resource', 'chargeService', 'adminCreatingService', '$alert', '$location',
   'agentSchoolService', 'schoolService', 'agentContractorService', 'agentRawActivityService', 'fullResponseService',
    (scope, rootScope, $filter, $q, Agent, Modal, Principal, Employee, $resource, Charge, AdminCreating, Alert, location,
     AgentSchool, AllSchool, AgentContractor, AgentActivity, FullResponse) ->
      rootScope.tabName = 'agent'

      scope.refresh = (agent)->
        Agent.query (data) ->
          scope.agents = _.each data, (a) ->
            a.expireDisplayValue = $filter('date')(a.expire, 'yyyy-MM-dd')
            FullResponse(AgentSchool, agent_id: a.id, most:5).then (d2) ->
              a.schoolIds = d2
              a.schools = _.map a.schoolIds, (kg) -> AllSchool.get(school_id: kg.school_id)
              _.each a.schools, (kg) -> kg.checked = false

            AgentContractor.query agent_id: a.id, (data) ->
              a.waitingContractors = _.filter data, (d) -> d.publishing.publish_status == 99
            AgentActivity.query agent_id: a.id, (data) ->
              a.waitingActivities = _.filter data, (d) -> d.publishing.publish_status == 99
          if agent?
            scope.currentAgent = _.find scope.agents, (a) -> a.id == agent.id
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

      selectedSchools = (agent) ->
        partition = _.partition scope.kindergartens, (kg) ->
          _.find agent.schools, (k) ->
            k.school_id == kg.school_id
        partition[0]

      scope.addSchool = (agent) ->
        scope.resetSelection()
        scope.currentAgent = angular.copy agent
        scope.unSelectedSchools = _.reject scope.kindergartens, (r) ->
          _.find (_.flatten _.map scope.agents, (a) -> selectedSchools a), (u) ->
            r.school_id == u.school_id
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

      scope.disconnect = (kg, currentAgent) ->
        deletedSchool = _.find currentAgent.schoolIds, (k) -> k.school_id == kg.school_id
        currentAgent.schoolIds = _.reject currentAgent.schoolIds, (k) -> k.school_id == kg.school_id
        currentAgent.schools = _.reject currentAgent.schools, (k) -> k.school_id == kg.school_id
        scope.unSelectedSchools.push _.find scope.kindergartens , (k) -> k.school_id == kg.school_id
        AgentSchool.delete(agent_id: currentAgent.id, kg: deletedSchool.id).$promise

      scope.connect = (kg, currentAgent) ->
        currentAgent.schoolIds = [] unless currentAgent.schoolIds?
        currentAgent.schools = [] unless currentAgent.schools?
        currentAgent.schoolIds.push school_id: kg.school_id
        currentAgent.schools.push _.find scope.kindergartens, (k) -> k.school_id == kg.school_id
        scope.unSelectedSchools = _.reject scope.unSelectedSchools , (k) -> k.school_id == kg.school_id
        AgentSchool.save(agent_id: currentAgent.id, school_id: kg.school_id, name: kg.full_name).$promise


      scope.checkAll = (check) ->
        scope.unSelectedSchools = [] unless scope.unSelectedSchools?
        scope.unSelectedSchools = _.each scope.unSelectedSchools, (r) ->
          r.checked = check
          r

      scope.checkAgentAll = (check) ->
        if scope.currentAgent?
          scope.currentAgent.schools = _.map scope.currentAgent.schools, (r) ->
            r.checked = check
            r

      scope.multipleDelete = ->
        checked = _.filter scope.currentAgent.schools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.disconnect kg, scope.currentAgent
        all = $q.all queue
        all.then (q) ->
            scope.resetSelection()
            scope.refresh(scope.currentAgent)
          , (res) ->
            handleError res

      scope.multipleAdd = ->
        checked = _.filter scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        queue = _.map checked, (kg) -> scope.connect kg, scope.currentAgent
        all = $q.all queue
        all.then (q) ->
            scope.resetSelection()
            scope.refresh(scope.currentAgent)
          , (res) ->
            handleError res

      scope.hasSelection = (kindergartens) ->
        _.some kindergartens, (r) -> r.checked? && r.checked == true

      scope.singleAgentSelection = (kg) ->
        allChecked = _.every scope.currentAgent.schools, (r) -> r.checked? && r.checked == true
        scope.selection.allAgentCheck = allChecked && scope.currentAgent.schools.length > 0

      scope.singleSelection = (kg) ->
        allChecked = _.every scope.unSelectedSchools, (r) -> r.checked? && r.checked == true
        scope.selection.allCheck = allChecked && scope.unSelectedSchools.length > 0

      scope.selection =
        allCheck: false
        allAgentCheck: false

      scope.resetSelection = ->
        scope.selection =
          allCheck: false
          allAgentCheck: false
        scope.checkAgentAll false
        scope.checkAll false

  ]


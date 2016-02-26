'use strict'

describe 'Service: AgentService', () ->

  # load the service's module
  beforeEach module 'kulebao.services'

  # instantiate service
  agentService = {}
  beforeEach inject (_agentLocationService_) ->
    agentService = _agentLocationService_

  it 'should analyse province', () ->
    expect(agentService.provinceOf '四川省').toBe '四川省'
    expect(agentService.provinceOf '四川省双流县').toBe '四川省'
    expect(agentService.provinceOf '双流县四川省').toBe ''
    expect(agentService.provinceOf '蘑菇岛').toBe ''
    expect(agentService.provinceOf '').toBe ''

  it 'should analyse city', () ->
    expect(agentService.cityOf '四川省').toBe ''
    expect(agentService.cityOf '四川省双流县').toBe ''
    expect(agentService.cityOf '双流县成都市四川省').toBe ''
    expect(agentService.cityOf '蘑菇岛').toBe ''
    expect(agentService.cityOf '').toBe ''
    expect(agentService.cityOf '四川省成都市双流县').toBe '成都市'
    expect(agentService.cityOf '四川省双流县成都市').toBe ''

  it 'should analyse county', () ->
    expect(agentService.countyOf '四川省').toBe ''
    expect(agentService.countyOf '四川省双流县').toBe ''
    expect(agentService.countyOf '双流县成都市四川省').toBe ''
    expect(agentService.countyOf '蘑菇岛').toBe ''
    expect(agentService.countyOf '').toBe ''
    expect(agentService.countyOf '四川省成都市双流县').toBe '双流县'
    expect(agentService.countyOf '四川省双流县成都市').toBe ''
    expect(agentService.countyOf '黑龙江省美国市澳大利亚区').toBe '澳大利亚区'
    expect(agentService.countyOf '黑龙江省澳大利亚区美国市').toBe ''

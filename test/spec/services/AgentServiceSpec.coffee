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
    expect(agentService.provinceOf undefined ).toBe ''
    expect(agentService.provinceOf '新疆维吾尔自治区' ).toBe '新疆维吾尔自治区'
    expect(agentService.provinceOf '香港特别行政区' ).toBe '香港特别行政区'
    expect(agentService.provinceOf '澳门特别行政区' ).toBe '澳门特别行政区'

  it 'should analyse city', () ->
    expect(agentService.cityOf '四川省').toBe ''
    expect(agentService.cityOf '四川省双流县').toBe ''
    expect(agentService.cityOf '双流县成都市四川省').toBe ''
    expect(agentService.cityOf '蘑菇岛').toBe ''
    expect(agentService.cityOf '').toBe ''
    expect(agentService.cityOf '四川省成都市双流县').toBe '成都市'
    expect(agentService.cityOf '四川省双流县成都市').toBe ''
    expect(agentService.cityOf undefined).toBe ''

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
    expect(agentService.countyOf '黑龙江省美国市亚利桑那市').toBe '亚利桑那市'
    expect(agentService.countyOf undefined).toBe ''

  it 'should analyse special cities', () ->
    expect(agentService.provinceOf '重庆市开县蘑菇路120号').toBe ''
    expect(agentService.cityOf '重庆市开县蘑菇路120号').toBe '重庆市'
    expect(agentService.countyOf '重庆市开县蘑菇路120号').toBe '开县'


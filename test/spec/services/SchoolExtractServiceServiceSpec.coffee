'use strict'

describe 'Service: schoolConfigExtractService', () ->

  # load the service's module
  beforeEach module 'kulebao.services'

  # instantiate service
  service = {}
  beforeEach inject (_schoolConfigExtractService_) ->
    service = _schoolConfigExtractService_

  it 'should extract config value with given name', () ->
    allConfig = ['name': 'a', value: 'realValue']
    expect(service(allConfig, 'a', '') ).toBe 'realValue'

  it 'should use default value', () ->
    allConfig = []
    expect(service(allConfig, 'a', 'defaultValue') ).toBe 'defaultValue'

  it 'should use empty string as default value', () ->
    allConfig = []
    expect(service(allConfig, 'a') ).toBe ''

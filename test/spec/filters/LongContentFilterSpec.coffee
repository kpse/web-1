'use strict'

describe 'Filter: LongContentFilter', () ->

  # load the filter's module
  beforeEach module 'kulebao.filters'

  # initialize a new instance of the filter before each test
  truncate = {}
  beforeEach inject ($filter) ->
    truncate = $filter 'truncate'

  it 'should truncate long content', () ->
    text = '12345678901'
    expect(truncate(text, '3')).toBe ('123……')

  it 'should output short content intact', () ->
    text = '12345678901'
    expect(truncate(text, '30')).toBe ('12345678901')

  it 'should handle empty input', () ->
    text = ''
    expect(truncate(text, '30')).toBe ('')

  it 'should handle null input', () ->
    text = undefined
    expect(truncate(text, '30')).toBe ('')

  it 'should handle non-digit length limitation', () ->
    text = '123'
    expect(truncate(text, 'xx')).toBe ('')

describe 'Operator page', ->

  it 'should display main page after login', ->
    browser.get('/operation')
    expect(browser.getTitle()).toBe '幼乐宝|后台管理'


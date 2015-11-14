describe 'Login page', ->

  it 'should display username and password inputs', ->
    browser.get('/operation')
    expect(browser.getTitle()).toBe '幼乐宝|后台管理'


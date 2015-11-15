describe 'WelcomePage', ->

  it 'should display welcome page of school page after login', ->
    browser.get('/admin')
    expect(browser.getTitle()).toBe '幼乐宝|后台管理'


describe 'WelcomePage', ->

  it 'should display welcome page of school page after login', ->
    browser.get('/agent')
    expect(browser.getTitle()).toBe '幼乐宝|后台管理'


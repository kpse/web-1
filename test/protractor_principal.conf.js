require('coffee-script/register');
exports.config = {
  allScriptsTimeout: 11000,

  specs: [
    'e2e/operator/*.coffee'
  ],

  capabilities: {
    'browserName': 'chrome'
  },

  baseUrl: 'http://localhost:9100',

  framework: 'jasmine',

  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  },

  onPrepare: function () {
    global.By = global.by;
    browser.driver.get(browser.baseUrl + '/login');

    browser.driver.findElement(by.id('username')).sendKeys('e0001');
    browser.driver.findElement(by.id('password')).sendKeys('secret');
    browser.driver.findElement(by.id('re-captcha')).sendKeys('anything');
    browser.driver.findElement(by.id('login-button')).click();

    // Login takes some time, so wait until it's done.
    // For the test app's login, we know it's done when it redirects to
    // index.html.
    return browser.driver.wait(function() {
      return browser.driver.getCurrentUrl().then(function(url) {
        return /admin/.test(url);
      });
    }, 10000);
  }
};
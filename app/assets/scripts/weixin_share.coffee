base = window.location.href
wxconfig1 =
  title: '【幼乐宝】宝宝成长记录'
  desc: '快来看看我在幼乐宝里面的珍藏吧'
  link: base
  imgUrl: 'http://7d9m4e.com1.z0.glb.clouddn.com/youlebao_logo.jpg'
wxconfig2 =
  title: '【幼乐宝】宝宝成长记录'
  desc: '快来看看我在幼乐宝里面的珍藏吧'
  link: base
  imgUrl: 'http://7d9m4e.com1.z0.glb.clouddn.com/youlebao_logo.jpg'
uri = window.location.href.split('#')[0]
$.get "https://#{location.host}/api/v3/weixin_signature?url=#{encodeURIComponent(uri)}", (data) ->
  apilist = [
    'onMenuShareTimeline'
    'onMenuShareAppMessage'
  ]
  wx.config
    debug: false
    appId: data.appid
    timestamp: data.timestamp
    nonceStr: data.noncestr
    signature: data.signature
    jsApiList: apilist
  wx.error (res) ->
    alert JSON.stringify(res)
  wx.ready ->
    wx.onMenuShareTimeline wxconfig1
    wx.onMenuShareAppMessage wxconfig2

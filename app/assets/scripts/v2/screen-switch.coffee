$ ->
  $('.pc-screen-frame .feature').mouseover ->
    images =  ['file', 'security', 'reception', 'health', 'finance', 'warehouse', 'food', 'communication']
    index = parseInt $(this).attr('data')
    $('.feature-pc-screen').attr("src", '/images/v2/feature-pc-screen-' + images[index] + '.png');
    $('.feature-pc-screen').css('margin-left', '410px')
    $('.feature-pc-screen').animate({'margin-left': "-=410"}, 200);

  $('.phone-screen-frame .feature').mouseover ->

    images =  ['history', 'video', 'push', 'conversation', 'schedule', 'food', 'bus', 'bulletin', 'performance']
    index = parseInt $(this).attr('data')
    imageUrl = 'url(../images/v2/app-frame-' + images[index] + '.png)'
    console.log 'hover' + imageUrl
    $('.feature-phone-screen').css('background-image', imageUrl)
    $('.feature-phone-screen').css('background-size', '277px 559px')

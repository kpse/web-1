$ ->
  $('.feature').mouseover ->
    images =  ['file', 'security', 'reception', 'health', 'finance', 'warehouse', 'food', 'communication']
    index = parseInt $(this).attr('data')
    $('.feature-pc-screen').attr("src", '/images/v2/feature-pc-screen-' + images[index] + '.png');
    $('.feature-pc-screen').css('margin-left', '410px')
    $('.feature-pc-screen').animate({'margin-left': "-=410"}, 200);

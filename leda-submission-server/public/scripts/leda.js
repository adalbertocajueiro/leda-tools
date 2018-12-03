$( document ).ready(function() {
    $('.leftmenutrigger').on('click', function(e) {
    $('.side-nav').toggleClass("open");
    e.preventDefault(); 
   });
});

function redirect(url){
    window.location.href = url;
}
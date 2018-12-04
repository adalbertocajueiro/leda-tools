const script = function(src, async){
    return {
        src: src,
        async: async
    }
};

function loadScript(src, async = false) {
    let script = document.createElement('script');
    script.src = src;
    script.async = async;
    $(script).appendTo('head');
}

const scripts = [
    script("components/footer/LedaFooter.js", false),
    script("components/user-nav/UserNav.js", false)
]

window.addEventListener("load", function(event){
    scripts.forEach(function(script){
        loadScript(script.src, script.async);
    })
});

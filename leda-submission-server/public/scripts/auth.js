function authStatus(){
    return gapi.auth2.getAuthInstance().isSignedIn.get();
}

function init() {
    gapi.load('auth2', function () {
        auth2 = gapi.auth2.init({
            client_id: '93476930298-dcdjdcu4uk9kb9892isecsl80go05mlo.apps.googleusercontent.com',
            cookiepolicy: 'single_host_origin',
            hosted_domain: 'ccc.ufcg.edu.br'
        });
    });
}

// function renderAccountSettings(googleUser) {
//     let status = gapi.auth2.getAuthInstance().isSignedIn.get();
//     let status = googleUser.isSignedIn();
//     let account = $("#account");
//     if (status) {
//         account.css("display", "block");
//     } else {
//         account.css("display", "none");
//     }
// }

function signOut() {
    gapi.auth2.getAuthInstance().signOut()
    location.reload(true);
}

function updateUserInfo(profile) {
    $('#logout').css('display', 'block');
    $('#user-img').css('display', 'block');
    $('#user-img').attr('src', profile.getImageUrl());
    console.log($('#my-signin2 > div > div > span > span').text(profile.getGivenName()));

}

function onSuccess(googleUser) {
    let profile = googleUser.getBasicProfile();
    console.log('Logged in as: ' + profile.getGivenName());
    console.log(profile);
    console.log(googleUser.getAuthResponse().id_token);
    updateUserInfo(profile);
}

function onFailure(error) {
    console.log(error);
}

function renderButton() {
    gapi.signin2.render('my-signin2', {
        'scope': 'profile email',
        'width': 'auto',
        'onsuccess': onSuccess,
        'onfailure': onFailure
    });
}
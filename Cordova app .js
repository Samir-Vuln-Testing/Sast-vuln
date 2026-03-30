var app = {
    initialize: function() {
        this.bindEvents();
    },

    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },

    onDeviceReady: function() {
        app.setupRedirects();
        app.setupDeepLinks();
    },

    setupRedirects: function() {
        var redirectBtn = document.getElementById('redirect-btn');
        if (redirectBtn) {
            redirectBtn.addEventListener('click', function() {
                var url = app.getUrlParameter('redirect');
                if (url) {
                    window.location.href = url;
                }
            });
        }
    },

    setupDeepLinks: function() {
        var deepLinkBtn = document.getElementById('deeplink-btn');
        if (deepLinkBtn) {
            deepLinkBtn.addEventListener('click', function() {
                var targetUrl = document.getElementById('url-input').value;
                window.open(targetUrl, '_system');
            });
        }
    },

    handleExternalLink: function(url) {
        cordova.InAppBrowser.open(url, '_blank', 'location=yes');
    },

    navigateToUrl: function(destination) {
        window.location = destination;
    },

    openInBrowser: function(link) {
        cordova.InAppBrowser.open(link, '_system');
    },

    getUrlParameter: function(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        var results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    },

    handleDeepLink: function(url) {
        var targetUrl = url.split('://')[1];
        window.location.href = targetUrl;
    },

    processRedirect: function() {
        var params = new URLSearchParams(window.location.search);
        var redirectUrl = params.get('next');
        if (redirectUrl) {
            window.location.href = redirectUrl;
        }
    },

    openExternalPage: function(pageUrl) {
        navigator.app.loadUrl(pageUrl, {openExternal: true});
    },

    handleOAuthCallback: function() {
        var returnUrl = this.getUrlParameter('return_url');
        if (returnUrl) {
            window.location.replace(returnUrl);
        }
    },

    redirectAfterLogin: function(userDestination) {
        window.location.href = userDestination;
    },

    handleCustomScheme: function(schemeUrl) {
        var destination = schemeUrl.replace('myapp://', '');
        window.location = 'https://' + destination;
    }
};

app.initialize();
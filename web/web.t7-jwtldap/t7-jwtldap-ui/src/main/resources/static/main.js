$(document).ready(function () {

        registerActions();
        updateAppInfo();
        validateUserCredentials();

        function doGet(_url, useToken, _callback, _errorHandler) {
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: _url,
                beforeSend: function (xhr) {
                    if (useToken && localStorage.token) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                    }
                }
            }).done(_callback)
                .fail(function (xhr, textStatus) {
                    if (_errorHandler) {
                        _errorHandler(xhr, textStatus);
                    } else {
                        alert("GET failed for url: " + _url + ": Error:" + xhr.status + ":" + xhr.responseText);
                    }
                });
        }

        function doPost(_url, _body, callback) {
            $.ajax({
                type: "POST",
                contentType: "application/json",
                url: _url,
                beforeSend: function (xhr) {
                    if (localStorage.token) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                    }
                },
                data: JSON.stringify(_body)
            }).done(callback)
                .fail(function (xhr, textStatus) {
                    alert("POST failed for url: " + _url + ": Error:" + xhr.status + ":" + xhr.responseText);
                });
        }

        function updateAppInfo() {
            doGet("info", false, function (data) {
                $('.app-name-id').html(data.applicationName);
                $('.app-version-id').html(data.buildVersion);
                $('.app-timestamp-id').html(data.buildTimestamp);

                var userStatus = "[NONE]";
                if ((localStorage.appUserName)) {
                    userStatus = "Current user:" + localStorage.appUserName;
                }
                $('.app-user-status-id').html(userStatus);
            });
        };

        function refreshToken() {
            doGet("auth/refresh", true, function (data) {
                // alert("OK, got data:" + data);
            })
        };

        function hasLocalToken() {
            if ((localStorage.token) && (localStorage.token.length > 0)) {
                return true;
            }
            return false;
        }

        function validateUserCredentials() {
            setUserInfo("?", "", "");
            setUserAuthorities("");

            if (!hasLocalToken()) {
                updateUserCredentials();
            } else {
                doGet("auth/validate", true,
                    function (data) {
                        updateUserCredentials();
                    },
                    function (xhr) {
                        alert("Failed to validate current Credentials: "+xhr.responseText);
                        console.error("Error:" + xhr.status);
                        localStorage.clear();
                        updateUserCredentials();
                    })
            }
        };

        function updateUserCredentials() {

            let hasToken = hasLocalToken();

            $('#login-but').prop('disabled', hasToken);
            $('#logout-but').prop('disabled', !hasToken);

            console.log("hasToken =" + hasToken);
            console.log("token    =" + localStorage.token);

            if (!hasToken) {
                setUserInfo("[NONE]", "?", "?");
                setUserAuthorities("[NONE]");
                return;
            }

            doGet("user", true, function (data) {
                console.log("DATA:" + data);

                if (data === "") {
                    data = null;
                }
                let userStatus = "[NONE]";
                if (localStorage.token) {
                    userStatus = "[HAS TOKEN]";
                }
                setUserInfo(userStatus,
                    (data) ? data.username : "[NONE]",
                    (data) ? data.fullname : "[NONE]");

                // keep
                if ((data) && (data.username)) {
                    localStorage.appUserName = data.username;
                } else {
                    localStorage.appUserName = null;
                }
            });
            console.log("BEFORE user/authorities/");
            doGet("user/authorities", true, function (data) {
                console.log("DATA:" + data);
                let list = JSON.stringify(data);
                setUserAuthorities(list);
            });
        }

        function setUserInfo(status, name, fullName) {
            $('#app-user-status-id').html(status);
            $('#app-user-name-id').html(name);
            $('#app-user-fullname-id').html(fullName);
        }

        function setUserAuthorities(auths) {
            $('#app-user-authorities-id').html(auths);
        }

        //
        function doLogin() {
            let usernameField = $('#login-username').val();
            let pwdField = $('#login-password').val();

            doPost("/auth", {username: usernameField, password: pwdField},
                function (data) {
                    console.log(data)
                    localStorage.token = data.token;
                    alert('Got a token from the server! Token: ' + data.token.substring(0, 16) + "...");
                    updateUserCredentials();
                });
        }

        function getDomainData() {
            $.ajax({
                type: 'GET',
                contentType: "application/json",
                url: '/data/ships',
                beforeSend: function (xhr) {
                    if (localStorage.token) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                    }
                },
                success: function (data) {
                    alert('Hello ' + localStorage.appUserName + '! You have successfully accessed to /data/');
                },
                error: function (xhr, textStatus) {
                    alert("Failed to get data. Error:" + xhr.responseText);
                }
            });
        }

        function getAdminData() {
            $.ajax({
                type: 'GET',
                contentType: "application/json",
                url: '/users',
                beforeSend: function (xhr) {
                    if (localStorage.token) {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                    }
                },
                success: function (data) {
                    alert('Hello ' + localStorage.appUserName + '! You have successfully accessed to /user/');
                },
                error: function (xhr, textStatus) {
                    alert("Failed to get data. Error:" + xhr.responseText);
                }
            });
        }

        function registerActions() {
            $('#login-form').on("submit", function () {
                doLogin();
            });
            //
            $('#login-but').click(function () {
                doLogin();
            });
            //
            $('#logout-but').click(function () {
                localStorage.token = null;
                localStorage.clear();
                updateUserCredentials();
            });
            $('#test-but').click(function () {
                getDomainData();
            });
            $('#test-admin-but').click(function () {
                getAdminData();
            });
        }
    }
)


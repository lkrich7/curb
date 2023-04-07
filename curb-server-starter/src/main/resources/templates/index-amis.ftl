<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <title>${(curbApp.name)!''} - ${(curbPage.name)!''}</title>

    <script type="text/javascript">
        function setAmisCss(theme, isIE) {
            var fileName = theme === 'default' ? 'sdk' : theme;
            if (isIE) {
                fileName = fileName + "-ie11";
            }
            var cssLink = document.createElement('link');
            cssLink.setAttribute('rel', 'stylesheet');
            cssLink.setAttribute('title', theme);
            cssLink.setAttribute('href', '/amis/' + fileName + '.css');
            document.head.appendChild(cssLink);
        }
        var theme = localStorage.getItem('theme') || 'default';
        var isIE = (window.navigator.userAgent.indexOf('Trident') >= 0);
        setAmisCss(theme, isIE);
    </script>

    <link rel="stylesheet" href="/amis/helper.css" />
    <script src="/amis/sdk.js"></script>

    <style>
        html,
        body,
        .app-wrapper {
            position: relative;
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>
<div id="root" class="app-wrapper"></div>
<script type="text/javascript">
    (function () {
        const app = {
            type: 'app',
            name: 'rootApp',
            brandName: '${(curbApp.name)?js_string}',
            logo: '${curbLogo?js_string}',
            header: {
                type: 'flex',
                className: 'w-full',
                justify: 'flex-end',
                alignItems: 'flex-end',
                items: [
                    {
                        type: "dropdown-button",
                        label: "${curbApp.name?json_string}",
                        block: true,
                        buttons: ${curbApps},
                        closeOnClick: true,
                        level: "link"
                    },
                    {
                        type: "avatar",
                        icon: "fa fa-user",
                        size: 20,
                    },
                    {
                        type: 'tpl',
                        tpl: '${(curbUser.name?json_string)!"匿名"} (${(curbUser.username?json_string)!"未登录"}) '
                    },
                    {
                        type: 'link',
                        href: '/logout',
                        body: '<i class="fa fa-sign-out"/>退出'
                    }
                ]
            },
            // footer: '<div class="p-2 text-center bg-light">底部区域</div>',
            // asideBefore: '<div class="p-2 text-center"><a href="/"><i class="fa fa-home"/>首页</a></div>',
            // asideAfter: '<div class="p-2 text-center">菜单后面区域</div>',
            pages: [
                {
                children: ${curbMenu}
                }
            ]
        };

        function normalizeLink(to, location = window.location) {
            to = to || '';

            if (to && to[0] === '#') {
                to = location.pathname + location.search + to;
            } else if (to && to[0] === '?') {
                to = location.pathname + to;
            }

            const idx = to.indexOf('?');
            const idx2 = to.indexOf('#');
            let pathname = ~idx
                    ? to.substring(0, idx)
                    : ~idx2
                            ? to.substring(0, idx2)
                            : to;
            let search = ~idx ? to.substring(idx, ~idx2 ? idx2 : undefined) : '';
            let hash = ~idx2 ? to.substring(idx2) : location.hash;

            if (!pathname) {
                pathname = location.pathname;
            } else if (pathname[0] != '/' && !/^https?\:\/\//.test(pathname)) {
                let relativeBase = location.pathname;
                const paths = relativeBase.split('/');
                paths.pop();
                let m;
                while ((m = /^\.\.?\//.exec(pathname))) {
                    if (m[0] === '../') {
                        paths.pop();
                    }
                    pathname = pathname.substring(m[0].length);
                }
                pathname = paths.concat(pathname).join('/');
            }

            return pathname + search + hash;
        }

        var _wr = function(type) {
            var orig = history[type];
            return function() {
                var rv = orig.apply(this, arguments);
                var e = new Event(type);
                e.arguments = arguments;
                window.dispatchEvent(e);
                return rv;
            };
        };
        window.history.pushState = _wr('pushState');
        window.history.replaceState = _wr('replaceState');

        let amis = amisRequire('amis/embed');
        var amisScoped = amis.embed(
            '#root',
            app,
            {
                locale: 'zh-CN'
            },
            {
                watchRouteChange: fn => {
                    console.log("watchRouteChange:" + fn);
                    window.addEventListener('popstate', fn);
                    window.addEventListener('pushState', fn);
                    window.addEventListener('replaceState', fn);
                    return () => {
                        window.removeEventListener('popstate', fn);
                        window.removeEventListener('pushState', fn);
                        window.removeEventListener('replaceState', fn);
                    };
                },
                jumpTo: (to, action) => {
                    to = normalizeLink(to)
                    console.log("jumpTo:" + to + ",action: " + JSON.stringify(action));
                    if (action) {
                        location.href = to;
                    } else {
                        window.history.pushState(to, document.title, to);
                    }
                },
                updateLocation: (to, replace) => {
                    to = normalizeLink(to);
                    console.log("updateLocation:" + to + "====" + replace);
                    window.history.pushState(to, document.title, to);
                },

                theme: theme
            }
        );
    })();
</script>
</body>
</html>
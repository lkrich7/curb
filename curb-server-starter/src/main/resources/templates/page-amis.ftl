<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">
    <title>${curbPage.name}</title>

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

    <link rel="stylesheet" href="/amis/helper.css"/>
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
        const page = ${curbPageSchema};
        let amis = amisRequire('amis/embed');
        var amisScoped = amis.embed(
                '#root',
                page,
                {
                    locale: 'zh-CN'
                },
                {
                    affixOffsetTop: 0,
                    theme: theme
                }
        );
    })();
</script>
</body>
</html>
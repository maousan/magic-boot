$ErrorActionPreference = 'Stop'

$files = @(
    'data/dongxinheping/function/aims/控制灯/根据articleId开关灯.ms',
    'data/dongxinheping/function/aims/控制灯/根据labelCode开关灯.ms'
)

foreach ($file in $files) {
    if (-not (Test-Path $file)) {
        throw "target file not found: $file"
    }

    $content = Get-Content -Raw -Encoding UTF8 -LiteralPath $file
    $parts = $content -split "(?m)^================================\s*$", 2
    if ($parts.Count -ne 2) {
        throw "missing magic-api separator: $file"
    }

    $script = $parts[1]
    foreach ($needle in @(
        'import java.nio.charset.StandardCharsets',
        'import cn.hutool.json.JSONUtil',
        'JSONUtil.toJsonStr(ledItems).getBytes(StandardCharsets.UTF_8)',
        '.contentType(''application/json;charset=UTF-8'')',
        '.addHeader(''Accept'', ''application/json;charset=UTF-8'')',
        '.executeAsResponse()'
    )) {
        if (-not $script.Contains($needle)) {
            throw "missing expected fragment in ${file}: $needle"
        }
    }

    if ($script.Contains('.addBody(ledItems)')) {
        throw "raw ledItems collection must not be passed to Forest.addBody: $file"
    }

    if ($script.Contains('.contentTypeJson()')) {
        throw "contentTypeJson must not be used for raw top-level JSON array body: $file"
    }

    if ($script.Contains('JsonUtils.toJsonString(ledItems)') -or $script.Contains('JSONUtil.toJsonStr(ledItems))')) {
        throw "string JSON body must be converted to UTF-8 byte[] before Forest.addBody: $file"
    }
}

'aims led json body static checks passed'

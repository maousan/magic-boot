$ErrorActionPreference = 'Stop'

$path = (Get-ChildItem -Path 'data/dongxinheping/api/东信和平' -Filter '*.ms' | Where-Object { $_.Name -eq 'RFID控制.ms' } | Select-Object -First 1).FullName
if (-not $path) {
    throw 'target api file not found'
}

$content = Get-Content -Raw -LiteralPath $path
$parts = $content -split "(?m)^================================\s*$", 2
if ($parts.Count -ne 2) {
    throw 'missing magic-api separator'
}

$meta = $parts[0] | ConvertFrom-Json
if ($meta.name -ne 'RFID控制') { throw 'unexpected api name' }
if ($meta.id.Length -ne 32) { throw 'api id must be 32 chars' }
if ($meta.id -notmatch '^[0-9a-z]{32}$') { throw 'api id must be lower alphanumeric' }
if ($meta.path -ne '/rfid') { throw 'unexpected api path' }
if ($meta.method -ne 'POST') { throw 'unexpected api method' }
if ($meta.groupId -ne '2ud9c5419ag5484mbxxsafin0b0myji0') { throw 'unexpected groupId' }

$script = $parts[1]
$required = @(
    'import request',
    'let apiResponse = (code, message) =>',
    "mode != 'on' && mode != 'off'",
    'request.getClientIP()',
    'import java.net.InetAddress',
    'isLoopbackIp',
    'zintisRfidDefaultDeviceId',
    'InetAddress.getLocalHost()',
    '无法获取deviceId',
    "mode == 'on'",
    'data.locationId不能为空',
    'data.sku不能为空',
    'zintisRfidCommandUrl',
    'plugin/zintis-rfid-plugin/api/rfid/command',
    'deviceId: deviceId',
    'command: mode',
    'params: requestData',
    'Forest.post(pluginUrl)',
    "return apiResponse(200, '操作成功')"
)

foreach ($needle in $required) {
    if (-not $script.Contains($needle)) {
        throw "missing expected script fragment: $needle"
    }
}

if ($content.Contains('body.deviceId')) {
    throw 'deviceId must not be read from request body'
}

if ($meta.requestBody.Contains('"deviceId"')) {
    throw 'request body example must not contain deviceId'
}

$requestBodyDefinitionText = $meta.requestBodyDefinition | ConvertTo-Json -Depth 20
if ($requestBodyDefinitionText.Contains('"deviceId"')) {
    throw 'request body definition must not contain deviceId'
}

$httpPath = 'http/test-dongxinheping-rfid-control.http'
if (-not (Test-Path $httpPath)) {
    throw 'http test file missing'
}
$httpContent = Get-Content -Raw -LiteralPath $httpPath
foreach ($needle in @('POST http://localhost:9999/api/rfid', '"mode": "on"', '"mode": "off"', '"locationId"', '"sku"')) {
    if (-not $httpContent.Contains($needle)) {
        throw "missing expected http fragment: $needle"
    }
}
if ($httpContent.Contains('"deviceId"')) {
    throw 'http test file must not send deviceId'
}

'rfid control api static checks passed'

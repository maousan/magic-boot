$ErrorActionPreference = 'Stop'

$path = (Get-ChildItem -Path 'data/dongxinheping/function/aims/数据' -Filter '*.ms' | Where-Object { $_.Name -eq '更新article信息.ms' } | Select-Object -First 1).FullName
if (-not $path) {
    throw 'target function file not found'
}

$content = Get-Content -Raw -LiteralPath $path
$parts = $content -split "(?m)^================================\s*$", 2
if ($parts.Count -ne 2) {
    throw 'missing magic-api separator'
}

$meta = $parts[0] | ConvertFrom-Json
if ($meta.name -ne '更新article信息') {
    throw 'unexpected function name'
}

$script = $parts[1]
$required = @(
    'const batchSize = 100',
    'import com.google.common.collect.Lists',
    'let batches = Lists.partition(dataList, batchSize)',
    'let callUpdateArticleBatch = (batchDataList, batchIndex) =>',
    '.addBody("dataList", batchDataList)',
    'for(batch in batches)',
    'batchIndex = batchIndex + 1',
    'failedBatches',
    'successCount',
    'totalCount: dataList.size()'
)

foreach ($needle in $required) {
    if (-not $script.Contains($needle)) {
        throw "missing expected script fragment: $needle"
    }
}

if ($script.Contains('.addBody("dataList", dataList)')) {
    throw 'function still posts the full dataList in one request'
}

if ($script.Contains('let splitBatches =')) {
    throw 'manual splitBatches should be replaced by Lists.partition'
}

'aims update article info batch static checks passed'

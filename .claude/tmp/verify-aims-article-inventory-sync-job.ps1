$ErrorActionPreference = 'Stop'

$path = (Get-ChildItem -Path 'data/dongxinheping/job/aims' -Filter '*.ms' | Where-Object { $_.Name -eq '定时同步article数据.ms' } | Select-Object -First 1).FullName
if (-not $path) {
    throw 'target job file not found'
}

$content = Get-Content -Raw -LiteralPath $path
$parts = $content -split "(?m)^================================\s*$", 2
if ($parts.Count -ne 2) {
    throw 'missing magic-api separator'
}

$meta = $parts[0] | ConvertFrom-Json
if ($meta.name -ne '定时同步article数据') {
    throw 'unexpected job name'
}
if ($meta.jobType -ne 'script') {
    throw 'unexpected job type'
}

$script = $parts[1]
$required = @(
    "import '@/aims/articles/updateArticleInfo' as updateArticleInfo",
    'const batchSize',
    'const maxLocationPages = 3',
    'select',
    'i.location_id',
    'select ii.sku',
    'from t_inventory',
    'group by i.location_id',
    'limit #{batchSize} offset #{offset}',
    'locationItems.add',
    'sku: normalizeText',
    'queryInventoryRowsByLocationPage',
    'where location_id = #{locationId}',
    'limit #{batchSize} offset #{pageOffset}',
    'order by edit_time desc',
    'as modified_date',
    "'%Y-%m-%dT%H:%i:%s.'",
    "'+0000'",
    'buildArticleDataItem',
    'buildArticleDataItem(locationItem.locationId, locationItem.sku)',
    'name: normalizeText(sku)',
    'appendInventoryRowsToArticleData',
    'putIndexedField',
    'let globalRowIndex = ((pageIndex - 1) * batchSize) + rowIndex',
    "fieldName + '_' + globalRowIndex",
    'while(pageIndex <= maxLocationPages)',
    'let pageOffset = (pageIndex - 1) * batchSize',
    'let rowIndex = 1',
    'rowIndex = rowIndex + 1',
    "articleItem.modifiedDate = valueOf(row, 'modified_date', 'modifiedDate')",
    'updateArticleInfo(dataList)',
    'failedBatches',
    'updatedArticles'
)

foreach ($needle in $required) {
    if (-not $script.Contains($needle)) {
        throw "missing expected script fragment: $needle"
    }
}

if ($script.Contains("return 'Hello magic-api-job'")) {
    throw 'job still contains placeholder return'
}

if ($script.Contains('where location_id in (')) {
    throw 'inventory rows must be read per locationId page, not as a location batch'
}

if ($script.Contains("fieldName + '_' + pageIndex + '_' + rowIndex")) {
    throw 'indexed fields must use global row index, not pageIndex_rowIndex format'
}

'aims article inventory sync job static checks passed'

$ErrorActionPreference = 'Stop'

$path = (Get-ChildItem -Path 'data/dongxinheping/job/wms' -Filter '*.ms' | Where-Object { $_.Name -eq '定时同步库存接口数据.ms' } | Select-Object -First 1).FullName
if (-not $path) {
    throw 'target job file not found'
}

$content = Get-Content -Raw -LiteralPath $path
$parts = $content -split "(?m)^================================\s*$", 2
if ($parts.Count -ne 2) {
    throw 'missing magic-api separator'
}

$meta = $parts[0] | ConvertFrom-Json
if ($meta.name -ne '定时同步库存接口数据') {
    throw 'unexpected job name'
}
if ($meta.jobType -ne 'script') {
    throw 'unexpected job type'
}

$script = $parts[1]
$required = @(
    "import '@/utils/queryInventoryByLocations' as queryInventoryByLocations",
    'select warehouse_code',
    'group by warehouse_code',
    'from t_location_warehouse',
    'limit #{pageSize} offset #{offset}',
    'queryInventoryByLocations(warehouseCode, locationIds)',
    'insert into t_inventory',
    'warehouse_id',
    'location_id',
    'lot_att09',
    'qty_allocated',
    'qty_pa',
    'str_to_date(nullif(#{editTime',
    'on duplicate key update',
    'values(qty)',
    'values(user_define5)',
    'failedPages',
    'skippedItems'
)

foreach ($needle in $required) {
    if (-not $script.Contains($needle)) {
        throw "missing expected script fragment: $needle"
    }
}

if ($script.Contains('create table if not exists t_inventory')) {
    throw 'job must not create t_inventory'
}

if ($script.Contains('select distinct warehouse_code')) {
    throw 'queryWarehouseCodes must use group by instead of distinct'
}

'wms inventory sync job static checks passed'

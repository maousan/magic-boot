{
  "properties" : { },
  "id" : "c8c3f5f21fd44868a74d2ef0d6fca2e8",
  "script" : null,
  "groupId" : "89d74bcccfbc468a8dfcd06d6cc2fc56",
  "name" : "库位状态工具",
  "createTime" : 1777000000000,
  "updateTime" : 1776937000803,
  "lock" : null,
  "createBy" : "admin",
  "updateBy" : "admin",
  "path" : "/locationStatusUtils",
  "description" : "t_location_status 真值表工具：回填、查询、亮灭灯分流",
  "returnType" : null,
  "mappingPath" : null,
  "parameters" : [ {
    "name" : "action",
    "value" : null,
    "description" : "动作标识",
    "required" : false,
    "dataType" : "String",
    "type" : "java.lang.String",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  }, {
    "name" : "data",
    "value" : null,
    "description" : "动作参数",
    "required" : false,
    "dataType" : "Object",
    "type" : "java.util.Map",
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ]
}
================================
let normalizeLocationCodes = (locationCodes) => {
    let result = []
    let uniq = {}
    if(locationCodes == null){
        return result
    }
    for(code in locationCodes){
        if(code == null){
            continue
        }
        let key = (code + '').trim()
        if(key == ''){
            continue
        }
        if(uniq[key] == null){
            uniq[key] = 1
            result.add(key)
        }
    }
    return result
}

let upsertLocationStatus = (locationCode, status0UserCount, status1UserCount) => {
    db.update("""
        insert into t_location_status (
            location_code,
            status0_user_count,
            status1_user_count,
            updated_time,
            version
        ) values (
            #{locationCode},
            #{status0UserCount},
            #{status1UserCount},
            #{updatedTime},
            1
        )
        on duplicate key update
            status0_user_count = values(status0_user_count),
            status1_user_count = values(status1_user_count),
            updated_time = values(updated_time),
            version = version + 1
    """, {
        locationCode: locationCode,
        status0UserCount: status0UserCount,
        status1UserCount: status1UserCount,
        updatedTime: new Date()
    })
}

let refreshOneLocationStatus = (locationCode) => {
    if(locationCode == null || locationCode == ''){
        return {
            locationCode: locationCode,
            status0UserCount: 0,
            status1UserCount: 0
        }
    }

    let row = db.selectOne("""
        select
            sum(case when t.unpicked_cnt > 0 then 1 else 0 end) as status0_user_count,
            sum(case when t.unpicked_cnt = 0 then 1 else 0 end) as status1_user_count
        from (
            select
                d.bill_id,
                sum(case when ifnull(d.status, 0) <> 1 then 1 else 0 end) as unpicked_cnt
            from t_picking_upload_detail d
            where d.location_code = #{locationCode}
            group by d.bill_id
        ) t
    """, {
        locationCode: locationCode
    })

    let status0UserCount = 0
    let status1UserCount = 0
    if(row != null){
        status0UserCount = row.status0_user_count
        if(status0UserCount == null){
            status0UserCount = row.status0UserCount
        }
        if(status0UserCount == null){
            status0UserCount = 0
        }
        status1UserCount = row.status1_user_count
        if(status1UserCount == null){
            status1UserCount = row.status1UserCount
        }
        if(status1UserCount == null){
            status1UserCount = 0
        }
    }

    upsertLocationStatus(locationCode, status0UserCount, status1UserCount)

    return {
        locationCode: locationCode,
        status0UserCount: status0UserCount,
        status1UserCount: status1UserCount
    }
}

let refreshLocationStatuses = (locationCodes) => {
    let normalized = normalizeLocationCodes(locationCodes)
    let resultMap = {}
    for(locationCode in normalized){
        let refreshed = refreshOneLocationStatus(locationCode)
        resultMap[locationCode] = {
            status0UserCount: refreshed.status0UserCount,
            status1UserCount: refreshed.status1UserCount
        }
    }
    return resultMap
}

let queryStatusSnapshotMap = (locationCodes) => {
    let normalized = normalizeLocationCodes(locationCodes)
    let result = {}
    if(normalized.size() == 0){
        return result
    }

    let params = {}
    let inSql = ""
    let idx = 0
    for(locationCode in normalized){
        let key = 'locationCode' + idx
        params[key] = locationCode
        if(idx > 0){
            inSql = inSql + ','
        }
        inSql = inSql + '#{' + key + '}'
        idx = idx + 1
    }

    let rows = db.select("""
        select
            location_code,
            status0_user_count,
            status1_user_count
        from t_location_status
        where location_code in (""" + inSql + """)
    """, params)

    if(rows != null){
        for(row in rows){
            let locationCode = row.location_code
            if(locationCode == null || locationCode == ''){
                locationCode = row.locationCode
            }
            if(locationCode == null || locationCode == ''){
                continue
            }

            let status0UserCount = row.status0_user_count
            if(status0UserCount == null){
                status0UserCount = row.status0UserCount
            }
            if(status0UserCount == null){
                status0UserCount = 0
            }

            let status1UserCount = row.status1_user_count
            if(status1UserCount == null){
                status1UserCount = row.status1UserCount
            }
            if(status1UserCount == null){
                status1UserCount = 0
            }

            result[locationCode] = {
                status0UserCount: status0UserCount,
                status1UserCount: status1UserCount
            }
        }
    }

    for(locationCode in normalized){
        if(result[locationCode] == null){
            result[locationCode] = {
                status0UserCount: 0,
                status1UserCount: 0
            }
        }
    }

    return result
}

let splitTurnOnLocations = (locationCodes) => {
    let normalized = normalizeLocationCodes(locationCodes)
    let statusMap = queryStatusSnapshotMap(normalized)
    let singleUserLocationCodes = []
    let multiUserLocationCodes = []
    for(locationCode in normalized){
        let snapshot = statusMap[locationCode]
        let status0UserCount = snapshot == null ? 0 : snapshot.status0UserCount
        if(status0UserCount > 1){
            multiUserLocationCodes.add(locationCode)
        } else if(status0UserCount == 1){
            singleUserLocationCodes.add(locationCode)
        }
    }
    return {
        singleUserLocationCodes: singleUserLocationCodes,
        multiUserLocationCodes: multiUserLocationCodes
    }
}

let resolveTurnOffLocations = (locationCodes) => {
    let normalized = normalizeLocationCodes(locationCodes)
    let statusMap = queryStatusSnapshotMap(normalized)
    let turnOffLocationCodes = []
    for(locationCode in normalized){
        let snapshot = statusMap[locationCode]
        let status0UserCount = snapshot == null ? 0 : snapshot.status0UserCount
        if(status0UserCount <= 0){
            turnOffLocationCodes.add(locationCode)
        }
    }
    return turnOffLocationCodes
}

if(action == 'normalizeLocationCodes'){
    return normalizeLocationCodes(data.locationCodes)
}

if(action == 'refreshLocationStatuses'){
    return refreshLocationStatuses(data.locationCodes)
}

if(action == 'queryStatusSnapshotMap'){
    return queryStatusSnapshotMap(data.locationCodes)
}

if(action == 'splitTurnOnLocations'){
    return splitTurnOnLocations(data.locationCodes)
}

if(action == 'resolveTurnOffLocations'){
    return resolveTurnOffLocations(data.locationCodes)
}

return {
    ok: false,
    message: 'unsupported action: ' + action
}

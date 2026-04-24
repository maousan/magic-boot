INSERT INTO t_location_status (
  location_code,
  status0_user_count,
  status1_user_count,
  updated_time,
  version
)
SELECT
  s.location_code,
  SUM(CASE WHEN s.unpicked_cnt > 0 THEN 1 ELSE 0 END) AS status0_user_count,
  SUM(CASE WHEN s.unpicked_cnt = 0 THEN 1 ELSE 0 END) AS status1_user_count,
  NOW() AS updated_time,
  1 AS version
FROM (
  SELECT
    d.location_code,
    d.bill_id,
    SUM(CASE WHEN IFNULL(d.status, 0) <> 1 THEN 1 ELSE 0 END) AS unpicked_cnt
  FROM t_picking_upload_detail d
  WHERE IFNULL(d.location_code, '') <> ''
    AND IFNULL(d.bill_id, '') <> ''
  GROUP BY d.location_code, d.bill_id
) s
GROUP BY s.location_code
ON DUPLICATE KEY UPDATE
  status0_user_count = VALUES(status0_user_count),
  status1_user_count = VALUES(status1_user_count),
  updated_time = VALUES(updated_time),
  version = version + 1;

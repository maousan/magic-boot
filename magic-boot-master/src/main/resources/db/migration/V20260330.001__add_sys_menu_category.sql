-- Add workspace category to sys_menu and backfill existing data
SET @category_column_exists := (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_menu'
    AND COLUMN_NAME = 'category'
);

SET @sql_add_category := IF(
  @category_column_exists = 0,
  'ALTER TABLE sys_menu ADD COLUMN category VARCHAR(32) NULL DEFAULT NULL COMMENT ''Workspace category: system, business, app-center, settings'' AFTER keep_alive',
  'SELECT 1'
);

PREPARE stmt_add_category FROM @sql_add_category;
EXECUTE stmt_add_category;
DEALLOCATE PREPARE stmt_add_category;

-- First inherit category from parent menus for child nodes
UPDATE sys_menu child
LEFT JOIN sys_menu parent ON parent.id = child.pid
SET child.category = COALESCE(child.category, parent.category)
WHERE child.is_del = 0
  AND (child.category IS NULL OR child.category = '');

-- Then infer by route path for remaining records
SET @path_column_exists := (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_menu'
    AND COLUMN_NAME = 'path'
);

SET @url_column_exists := (
  SELECT COUNT(1)
  FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_menu'
    AND COLUMN_NAME = 'url'
);

SET @route_column := IF(@path_column_exists > 0, 'path', IF(@url_column_exists > 0, 'url', NULL));

SET @sql_backfill_by_route := IF(
  @route_column IS NULL,
  'SELECT 1',
  CONCAT(
    'UPDATE sys_menu ',
    'SET category = CASE ',
    'WHEN category IS NOT NULL AND category <> '''' THEN category ',
    'WHEN ', @route_column, ' = ''/profile'' OR ', @route_column, ' LIKE ''/profile/%'' THEN ''settings'' ',
    'WHEN ', @route_column, ' = ''/app-center'' OR ', @route_column, ' LIKE ''/app-center/%'' THEN ''app-center'' ',
    'WHEN ', @route_column, ' = ''/business'' OR ', @route_column, ' LIKE ''/business/%'' THEN ''business'' ',
    'WHEN ', @route_column, ' = ''/system'' OR ', @route_column, ' LIKE ''/system/%'' THEN ''system'' ',
    'WHEN ', @route_column, ' = ''/dashboard'' OR ', @route_column, ' LIKE ''/dashboard/%'' THEN ''system'' ',
    'WHEN ', @route_column, ' = ''/security'' OR ', @route_column, ' LIKE ''/security/%'' THEN ''system'' ',
    'ELSE ''system'' END ',
    'WHERE is_del = 0 AND (category IS NULL OR category = '''')'
  )
);

PREPARE stmt_backfill_by_route FROM @sql_backfill_by_route;
EXECUTE stmt_backfill_by_route;
DEALLOCATE PREPARE stmt_backfill_by_route;

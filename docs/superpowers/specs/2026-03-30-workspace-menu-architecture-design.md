# Workspace-Driven Menu Architecture Design

**Date:** 2026-03-30
**Status:** Approved

## Context

The project introduced a workspace layer (left rail icons) that groups menus into functional areas (system, business, app-center). However, workspaces are currently a **pure frontend concept** — the backend has no awareness of them. Menu assignment to workspaces is done by hardcoded `pathPrefixes` string matching, which is fragile and doesn't account for permission-driven visibility (a user with no system menus should not see the system workspace icon).

This design moves workspace assignment into the backend menu data model via a `category` field, making workspace visibility **permission-driven** while keeping workspace UI configuration (icon, name, order) on the frontend.

## Decision: Hybrid Approach

- **Backend**: Add `category` field to menu table; existing `/system/menu/current/menus` endpoint returns it
- **Frontend**: Group menus by `category`, only show workspaces that have accessible menus
- **No new API endpoints needed** — `category` is just an additional field on existing menu data

## Backend Changes

### Menu Table Schema

```sql
ALTER TABLE sys_menu ADD COLUMN category VARCHAR(32) DEFAULT NULL
  COMMENT 'Workspace category: system, business, app-center, etc.';
```

**Migration of existing data:**

```sql
-- Assign category to all menus under "系统管理" top-level directory
UPDATE sys_menu SET category = 'system'
  WHERE parent_id = (SELECT id FROM sys_menu WHERE name = '系统管理');

-- Assign category to all menus under "业务管理" top-level directory
UPDATE sys_menu SET category = 'business'
  WHERE parent_id = (SELECT id FROM sys_menu WHERE name = '业务管理');

-- Then: either delete the top-level directory nodes or mark them as non-renderable
```

### API Response Change

**`POST /system/menu/current/menus`** — each menu node now includes `category`:

```jsonc
// Before
[
  {
    "name": "系统管理",
    "path": "/system",
    "meta": { "icon": "ion:settings-outline" },
    "children": [
      { "name": "用户管理", "path": "/system/user" },
      { "name": "角色管理", "path": "/system/role" }
    ]
  }
]

// After
[
  { "name": "用户管理", "path": "/system/user", "category": "system" },
  { "name": "角色管理", "path": "/system/role", "category": "system" },
  {
    "name": "系统配置",
    "path": "/system/config",
    "category": "system",
    "children": [
      { "name": "参数设置", "path": "/system/config/param", "category": "system" }
    ]
  }
]
```

Key structural change: **top-level directory nodes** (like "系统管理") are no longer menu nodes. Instead, their children become top-level menu items tagged with `category`.

### Menu Management UI Changes

- Menu form: add `category` dropdown field (values: system, business, app-center)
- Top-level "directory" nodes can be deprecated or kept as internal grouping (not rendered in sidebar)

## Frontend Changes

### 1. WorkspaceConfig Update

```typescript
// Before
interface WorkspaceConfig {
  id: string;
  name: string;
  icon: string;
  defaultPath: string;
  pathPrefixes: string[];  // ❌ removed
  pinned?: boolean;
}

// After
interface WorkspaceConfig {
  id: string;
  name: string;
  icon: string;
  defaultPath: string;
  category: string;        // ✅ matches backend menu.category
  pinned?: boolean;
}
```

```typescript
const WORKSPACE_CONFIGS: WorkspaceConfig[] = [
  { id: 'system', category: 'system', icon: 'lucide:layout-dashboard', name: 'workspace.system', defaultPath: '/system' },
  { id: 'business', category: 'business', icon: 'lucide:briefcase', name: 'workspace.business', defaultPath: '/business' },
  { id: 'app-center', category: 'app-center', icon: 'lucide:layout-grid', name: 'workspace.appCenter', defaultPath: '/app-center' },
  { id: 'settings', category: 'settings', icon: 'lucide:settings', name: 'workspace.settings', defaultPath: '/profile', pinned: true },
];
```

### 2. Workspace Store Changes

Replace `pathPrefixes`-based filtering with `category`-based grouping:

```typescript
// New: group menus by category
const menuByCategory = computed(() => {
  const map: Record<string, MenuRecordRaw[]> = {};
  for (const menu of accessStore.accessMenus) {
    const cat = (menu as any).category || 'uncategorized';
    if (!map[cat]) map[cat] = [];
    map[cat].push(menu);
  }
  return map;
});

// New: only show workspaces with accessible menus
const visibleWorkspaces = computed(() =>
  workspaces.value.filter(ws =>
    ws.pinned || (menuByCategory.value[ws.category]?.length > 0)
  )
);

const visibleRegularWorkspaces = computed(() =>
  visibleWorkspaces.value.filter(ws => !ws.pinned)
);

const visiblePinnedWorkspaces = computed(() =>
  visibleWorkspaces.value.filter(ws => ws.pinned)
);
```

- Remove `filterMenusByWorkspace()` — replaced by category grouping
- Remove `cachedMenus` / `initMenus()` — no longer needed since backend returns menus already categorized
- `switchWorkspace()` — set current workspace and set filtered menus by category
- `detectWorkspaceFromPath()` — auto-detect workspace from current route path

### 3. Left Rail Component

Use `visibleRegularWorkspaces` and `visiblePinnedWorkspaces` instead of static lists. Workspaces without menus are automatically hidden.

### 4. Route Generation

`generateRoutesByBackend()` in `packages/utils/src/helpers/generate-routes-backend.ts` needs to handle the flattened top-level structure. Menus are no longer nested under a virtual directory — they come as flat items with `category` and optional `children`.

## Migration Steps

1. **Backend**: Add `category` column to `sys_menu` table
2. **Backend**: Migrate existing menus — assign category based on current parent directory
3. **Backend**: Update `POST /system/menu/current/menus` response to include `category`
4. **Backend**: Update menu save API to accept `category`
5. **Frontend**: Update `WorkspaceConfig` interface and configs
6. **Frontend**: Refactor workspace store — category-based grouping
7. **Frontend**: Update route generation for flattened structure
8. **Frontend**: Update left-rail to use dynamic visibility
9. **Cleanup**: Remove `pathPrefixes`, `cachedMenus`, `initMenus`

## Open Questions

- Should the backend menu tree endpoint (`GET /system/menu/tree`) also be updated for the admin UI?
- Should category values be configurable (new table) or hardcoded enum?
- How to handle menus without a category (e.g., global/shared menus)?

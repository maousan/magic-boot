# 变更日志：LED 映射页面 Tabs 样式调整

- 日期：2026-04-07
- 文件：`magic-boot-master/src/main/resources/static/pda/led-mapping.html`

## 变更说明
- 将 tabs 从“按钮式”视觉改为“经典标签页”视觉。
- 保留原有 HTML 结构与 JS 切换逻辑，仅调整 CSS 样式。

## 具体调整
- `.tabs`：去除间距，增加底部分隔线，形成标签栏。
- `.tab-btn`：改为透明背景、上圆角标签形态，移除按钮感边框表现。
- `.tab-btn.active`：改为白底+边框+高亮文字的选中标签样式。

## 影响评估
- 仅影响页面视觉样式，不影响接口调用、表单提交和 tab 切换逻辑。

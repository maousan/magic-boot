import{ai as Ce,bY as xe,an as we,a4 as ne,J as f,Q as _,al as A,aM as C,S as ge,cg as te,ct as j,cn as he,cu as re,a5 as R,bJ as N,ae as Se,bF as fe,P as m,H as V,d as ke,aQ as Te,Y as U,c9 as se,aB as Pe,bA as Be,cy as _e,aa as Y,cA as x,cd as n,bC as M,aj as v,a9 as H,ac as W,ah as D,c8 as K,ab as Z,B as ie,bL as Re,bp as Ie,cD as de,b as Le,K as ze,bR as $e,l as Me,co as Ne,bH as Ee,$ as ce,bQ as ue,cp as Oe,aJ as je,Z as Ae,bo as Fe}from"./index-gM8gYDrW.js";import{c as Ve,a as De}from"./auth-JQBcyy66.js";import{f as G,h as Ue,u as Ye,j as He,l as We}from"./request-DjoaAKzv.js";import{g as Ke}from"./license-B6oEKxXK.js";import{p as le,l as qe,N as Je}from"./Menu-CqqsBp8_.js";import{C as Qe}from"./Dropdown-wzQ5MubL.js";import{a as Xe}from"./Tag-DD5Qx2J6.js";import{N as Ze}from"./Alert-DP6hwBHs.js";import{N as ee}from"./FormItem-CrbqmZvk.js";import{a as oe}from"./Input-C7jhpyHw.js";import{_ as Ge}from"./_plugin-vue_export-helper-DlAUqK2U.js";import"./core-BNwxd6Ld.js";import"./Tooltip-yMKRqAts.js";import"./light-BOPZrXOH.js";import"./create-DT8ZRqrG.js";import"./Follower-CISMgRu2.js";import"./light-DvILBqKA.js";import"./light-Dk8H8wQ2.js";import"./happens-in-CM8LO42l.js";import"./use-keyboard-vHLPcj40.js";import"./_common-Dh8oehso.js";import"./light-D9WTscqZ.js";function eo(e){const{baseColor:o,textColor2:r,bodyColor:b,cardColor:d,dividerColor:s,actionColor:B,scrollbarColor:w,scrollbarColorHover:p,invertedColor:S}=e;return{textColor:r,textColorInverted:"#FFF",color:b,colorEmbedded:B,headerColor:d,headerColorInverted:S,footerColor:B,footerColorInverted:S,headerBorderColor:s,headerBorderColorInverted:S,footerBorderColor:s,footerBorderColorInverted:S,siderBorderColor:s,siderBorderColorInverted:S,siderColor:d,siderColorInverted:S,siderToggleButtonBorder:`1px solid ${s}`,siderToggleButtonColor:o,siderToggleButtonIconColor:r,siderToggleButtonIconColorInverted:r,siderToggleBarColor:ne(b,w),siderToggleBarColorHover:ne(b,p),__invertScrollbar:"true"}}const ae=Ce({name:"Layout",common:we,peers:{Scrollbar:xe},self:eo}),oo=f("layout",`
 color: var(--n-text-color);
 background-color: var(--n-color);
 box-sizing: border-box;
 position: relative;
 z-index: auto;
 flex: auto;
 overflow: hidden;
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
`,[f("layout-scroll-container",`
 overflow-x: hidden;
 box-sizing: border-box;
 height: 100%;
 `),_("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),to={embedded:Boolean,position:le,nativeScrollbar:{type:Boolean,default:!0},scrollbarProps:Object,onScroll:Function,contentClass:String,contentStyle:{type:[String,Object],default:""},hasSider:Boolean,siderPlacement:{type:String,default:"left"}},me=Se("n-layout");function ve(e){return A({name:e?"LayoutContent":"Layout",props:Object.assign(Object.assign({},j.props),to),setup(o){const r=N(null),b=N(null),{mergedClsPrefixRef:d,inlineThemeDisabled:s}=te(o),B=j("Layout","-layout",oo,ae,o,d);function w(g,y){if(o.nativeScrollbar){const{value:k}=r;k&&(y===void 0?k.scrollTo(g):k.scrollTo(g,y))}else{const{value:k}=b;k&&k.scrollTo(g,y)}}fe(me,o);let p=0,S=0;const I=g=>{var y;const k=g.target;p=k.scrollLeft,S=k.scrollTop,(y=o.onScroll)===null||y===void 0||y.call(o,g)};he(()=>{if(o.nativeScrollbar){const g=r.value;g&&(g.scrollTop=S,g.scrollLeft=p)}});const L={display:"flex",flexWrap:"nowrap",width:"100%",flexDirection:"row"},c={scrollTo:w},E=R(()=>{const{common:{cubicBezierEaseInOut:g},self:y}=B.value;return{"--n-bezier":g,"--n-color":o.embedded?y.colorEmbedded:y.color,"--n-text-color":y.textColor}}),T=s?re("layout",R(()=>o.embedded?"e":""),E,o):void 0;return Object.assign({mergedClsPrefix:d,scrollableElRef:r,scrollbarInstRef:b,hasSiderStyle:L,mergedTheme:B,handleNativeElScroll:I,cssVars:s?void 0:E,themeClass:T==null?void 0:T.themeClass,onRender:T==null?void 0:T.onRender},c)},render(){var o;const{mergedClsPrefix:r,hasSider:b}=this;(o=this.onRender)===null||o===void 0||o.call(this);const d=b?this.hasSiderStyle:void 0,s=[this.themeClass,e&&`${r}-layout-content`,`${r}-layout`,`${r}-layout--${this.position}-positioned`];return C("div",{class:s,style:this.cssVars},this.nativeScrollbar?C("div",{ref:"scrollableElRef",class:[`${r}-layout-scroll-container`,this.contentClass],style:[this.contentStyle,d],onScroll:this.handleNativeElScroll},this.$slots):C(ge,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,contentClass:this.contentClass,contentStyle:[this.contentStyle,d]}),this.$slots))}})}const be=ve(!1),ro=ve(!0),lo=f("layout-header",`
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 box-sizing: border-box;
 width: 100%;
 background-color: var(--n-color);
 color: var(--n-text-color);
`,[_("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 `),_("bordered",`
 border-bottom: solid 1px var(--n-border-color);
 `)]),ao={position:le,inverted:Boolean,bordered:{type:Boolean,default:!1}},no=A({name:"LayoutHeader",props:Object.assign(Object.assign({},j.props),ao),setup(e){const{mergedClsPrefixRef:o,inlineThemeDisabled:r}=te(e),b=j("Layout","-layout-header",lo,ae,e,o),d=R(()=>{const{common:{cubicBezierEaseInOut:B},self:w}=b.value,p={"--n-bezier":B};return e.inverted?(p["--n-color"]=w.headerColorInverted,p["--n-text-color"]=w.textColorInverted,p["--n-border-color"]=w.headerBorderColorInverted):(p["--n-color"]=w.headerColor,p["--n-text-color"]=w.textColor,p["--n-border-color"]=w.headerBorderColor),p}),s=r?re("layout-header",R(()=>e.inverted?"a":"b"),d,e):void 0;return{mergedClsPrefix:o,cssVars:r?void 0:d,themeClass:s==null?void 0:s.themeClass,onRender:s==null?void 0:s.onRender}},render(){var e;const{mergedClsPrefix:o}=this;return(e=this.onRender)===null||e===void 0||e.call(this),C("div",{class:[`${o}-layout-header`,this.themeClass,this.position&&`${o}-layout-header--${this.position}-positioned`,this.bordered&&`${o}-layout-header--bordered`],style:this.cssVars},this.$slots)}}),so=f("layout-sider",`
 flex-shrink: 0;
 box-sizing: border-box;
 position: relative;
 z-index: 1;
 color: var(--n-text-color);
 transition:
 color .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 min-width .3s var(--n-bezier),
 max-width .3s var(--n-bezier),
 transform .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 background-color: var(--n-color);
 display: flex;
 justify-content: flex-end;
`,[_("bordered",[m("border",`
 content: "";
 position: absolute;
 top: 0;
 bottom: 0;
 width: 1px;
 background-color: var(--n-border-color);
 transition: background-color .3s var(--n-bezier);
 `)]),m("left-placement",[_("bordered",[m("border",`
 right: 0;
 `)])]),_("right-placement",`
 justify-content: flex-start;
 `,[_("bordered",[m("border",`
 left: 0;
 `)]),_("collapsed",[f("layout-toggle-button",[f("base-icon",`
 transform: rotate(180deg);
 `)]),f("layout-toggle-bar",[V("&:hover",[m("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),m("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])])]),f("layout-toggle-button",`
 left: 0;
 transform: translateX(-50%) translateY(-50%);
 `,[f("base-icon",`
 transform: rotate(0);
 `)]),f("layout-toggle-bar",`
 left: -28px;
 transform: rotate(180deg);
 `,[V("&:hover",[m("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),m("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})])])]),_("collapsed",[f("layout-toggle-bar",[V("&:hover",[m("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),m("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])]),f("layout-toggle-button",[f("base-icon",`
 transform: rotate(0);
 `)])]),f("layout-toggle-button",`
 transition:
 color .3s var(--n-bezier),
 right .3s var(--n-bezier),
 left .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 cursor: pointer;
 width: 24px;
 height: 24px;
 position: absolute;
 top: 50%;
 right: 0;
 border-radius: 50%;
 display: flex;
 align-items: center;
 justify-content: center;
 font-size: 18px;
 color: var(--n-toggle-button-icon-color);
 border: var(--n-toggle-button-border);
 background-color: var(--n-toggle-button-color);
 box-shadow: 0 2px 4px 0px rgba(0, 0, 0, .06);
 transform: translateX(50%) translateY(-50%);
 z-index: 1;
 `,[f("base-icon",`
 transition: transform .3s var(--n-bezier);
 transform: rotate(180deg);
 `)]),f("layout-toggle-bar",`
 cursor: pointer;
 height: 72px;
 width: 32px;
 position: absolute;
 top: calc(50% - 36px);
 right: -28px;
 `,[m("top, bottom",`
 position: absolute;
 width: 4px;
 border-radius: 2px;
 height: 38px;
 left: 14px;
 transition: 
 background-color .3s var(--n-bezier),
 transform .3s var(--n-bezier);
 `),m("bottom",`
 position: absolute;
 top: 34px;
 `),V("&:hover",[m("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),m("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})]),m("top, bottom",{backgroundColor:"var(--n-toggle-bar-color)"}),V("&:hover",[m("top, bottom",{backgroundColor:"var(--n-toggle-bar-color-hover)"})])]),m("border",`
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 width: 1px;
 transition: background-color .3s var(--n-bezier);
 `),f("layout-sider-scroll-container",`
 flex-grow: 1;
 flex-shrink: 0;
 box-sizing: border-box;
 height: 100%;
 opacity: 0;
 transition: opacity .3s var(--n-bezier);
 max-width: 100%;
 `),_("show-content",[f("layout-sider-scroll-container",{opacity:1})]),_("absolute-positioned",`
 position: absolute;
 left: 0;
 top: 0;
 bottom: 0;
 `)]),io=A({props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return C("div",{onClick:this.onClick,class:`${e}-layout-toggle-bar`},C("div",{class:`${e}-layout-toggle-bar__top`}),C("div",{class:`${e}-layout-toggle-bar__bottom`}))}}),co=A({name:"LayoutToggleButton",props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return C("div",{class:`${e}-layout-toggle-button`,onClick:this.onClick},C(ke,{clsPrefix:e},{default:()=>C(Qe,null)}))}}),uo={position:le,bordered:Boolean,collapsedWidth:{type:Number,default:48},width:{type:[Number,String],default:272},contentClass:String,contentStyle:{type:[String,Object],default:""},collapseMode:{type:String,default:"transform"},collapsed:{type:Boolean,default:void 0},defaultCollapsed:Boolean,showCollapsedContent:{type:Boolean,default:!0},showTrigger:{type:[Boolean,String],default:!1},nativeScrollbar:{type:Boolean,default:!0},inverted:Boolean,scrollbarProps:Object,triggerClass:String,triggerStyle:[String,Object],collapsedTriggerClass:String,collapsedTriggerStyle:[String,Object],"onUpdate:collapsed":[Function,Array],onUpdateCollapsed:[Function,Array],onAfterEnter:Function,onAfterLeave:Function,onExpand:[Function,Array],onCollapse:[Function,Array],onScroll:Function},bo=A({name:"LayoutSider",props:Object.assign(Object.assign({},j.props),uo),setup(e){const o=Te(me),r=N(null),b=N(null),d=N(e.defaultCollapsed),s=Ue(se(e,"collapsed"),d),B=R(()=>G(s.value?e.collapsedWidth:e.width)),w=R(()=>e.collapseMode!=="transform"?{}:{minWidth:G(e.width)}),p=R(()=>o?o.siderPlacement:"left");function S(u,l){if(e.nativeScrollbar){const{value:i}=r;i&&(l===void 0?i.scrollTo(u):i.scrollTo(u,l))}else{const{value:i}=b;i&&i.scrollTo(u,l)}}function I(){const{"onUpdate:collapsed":u,onUpdateCollapsed:l,onExpand:i,onCollapse:O}=e,{value:$}=s;l&&U(l,!$),u&&U(u,!$),d.value=!$,$?i&&U(i):O&&U(O)}let L=0,c=0;const E=u=>{var l;const i=u.target;L=i.scrollLeft,c=i.scrollTop,(l=e.onScroll)===null||l===void 0||l.call(e,u)};he(()=>{if(e.nativeScrollbar){const u=r.value;u&&(u.scrollTop=c,u.scrollLeft=L)}}),fe(qe,{collapsedRef:s,collapseModeRef:se(e,"collapseMode")});const{mergedClsPrefixRef:T,inlineThemeDisabled:g}=te(e),y=j("Layout","-layout-sider",so,ae,e,T);function k(u){var l,i;u.propertyName==="max-width"&&(s.value?(l=e.onAfterLeave)===null||l===void 0||l.call(e):(i=e.onAfterEnter)===null||i===void 0||i.call(e))}const q={scrollTo:S},F=R(()=>{const{common:{cubicBezierEaseInOut:u},self:l}=y.value,{siderToggleButtonColor:i,siderToggleButtonBorder:O,siderToggleBarColor:$,siderToggleBarColorHover:J}=l,P={"--n-bezier":u,"--n-toggle-button-color":i,"--n-toggle-button-border":O,"--n-toggle-bar-color":$,"--n-toggle-bar-color-hover":J};return e.inverted?(P["--n-color"]=l.siderColorInverted,P["--n-text-color"]=l.textColorInverted,P["--n-border-color"]=l.siderBorderColorInverted,P["--n-toggle-button-icon-color"]=l.siderToggleButtonIconColorInverted,P.__invertScrollbar=l.__invertScrollbar):(P["--n-color"]=l.siderColor,P["--n-text-color"]=l.textColor,P["--n-border-color"]=l.siderBorderColor,P["--n-toggle-button-icon-color"]=l.siderToggleButtonIconColor),P}),z=g?re("layout-sider",R(()=>e.inverted?"a":"b"),F,e):void 0;return Object.assign({scrollableElRef:r,scrollbarInstRef:b,mergedClsPrefix:T,mergedTheme:y,styleMaxWidth:B,mergedCollapsed:s,scrollContainerStyle:w,siderPlacement:p,handleNativeElScroll:E,handleTransitionend:k,handleTriggerClick:I,inlineThemeDisabled:g,cssVars:F,themeClass:z==null?void 0:z.themeClass,onRender:z==null?void 0:z.onRender},q)},render(){var e;const{mergedClsPrefix:o,mergedCollapsed:r,showTrigger:b}=this;return(e=this.onRender)===null||e===void 0||e.call(this),C("aside",{class:[`${o}-layout-sider`,this.themeClass,`${o}-layout-sider--${this.position}-positioned`,`${o}-layout-sider--${this.siderPlacement}-placement`,this.bordered&&`${o}-layout-sider--bordered`,r&&`${o}-layout-sider--collapsed`,(!r||this.showCollapsedContent)&&`${o}-layout-sider--show-content`],onTransitionend:this.handleTransitionend,style:[this.inlineThemeDisabled?void 0:this.cssVars,{maxWidth:this.styleMaxWidth,width:G(this.width)}]},this.nativeScrollbar?C("div",{class:[`${o}-layout-sider-scroll-container`,this.contentClass],onScroll:this.handleNativeElScroll,style:[this.scrollContainerStyle,{overflow:"auto"},this.contentStyle],ref:"scrollableElRef"},this.$slots):C(ge,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",style:this.scrollContainerStyle,contentStyle:this.contentStyle,contentClass:this.contentClass,theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,builtinThemeOverrides:this.inverted&&this.cssVars.__invertScrollbar==="true"?{colorHover:"rgba(255, 255, 255, .4)",color:"rgba(255, 255, 255, .3)"}:void 0}),this.$slots),b?b==="bar"?C(io,{clsPrefix:o,class:r?this.collapsedTriggerClass:this.triggerClass,style:r?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):C(co,{clsPrefix:o,class:r?this.collapsedTriggerClass:this.triggerClass,style:r?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):null,this.bordered?C("div",{class:`${o}-layout-sider__border`}):null)}}),go={key:0,class:"admin-user-info"},ho=["onClick"],fo=["onClick"],mo={class:"admin-page"},vo=A({__name:"AdminLayout",setup(e){const o=Oe(),r=Ne(),b=Ye(),d=He(),{tabs:s,cachedNames:B,addTab:w,removeTab:p,clearTabs:S}=We(),I=Pe(),L=N(!1),c=Ee({oldPwd:"",newPwd:"",confirmPwd:""});async function E(){if(!c.oldPwd)return d.error("请输入原密码"),!1;if(c.newPwd.length<6)return d.error("新密码至少6位"),!1;if(c.newPwd!==c.confirmPwd)return d.error("两次输入的新密码不一致"),!1;if(c.newPwd===c.oldPwd)return d.error("新密码不能与原密码相同"),!1;await De(c.oldPwd,c.newPwd),d.success("密码已修改，请使用新密码重新登录"),ce(),S(),o.push({name:"Login"})}const T=N(null),g=new Map;function y(t,a){a instanceof HTMLElement&&g.set(t,a)}function k(t){var a;(a=T.value)==null||a.scrollBy({left:t.deltaY,behavior:"smooth"})}function q(t){Fe(()=>{var a;(a=g.get(t))==null||a.scrollIntoView({behavior:"smooth",inline:"nearest",block:"nearest"})})}const F=[{label:"仪表盘",key:"Dashboard"},{label:"库位管理",key:"location",children:[{label:"库位列表",key:"WarehouseLocation"},{label:"巷道灯绑定",key:"LedMapping"},{label:"标签绑定",key:"LabelMapping"},{label:"库存查询",key:"InventoryQuery"}]},{label:"巷道灯管理",key:"led",children:[{label:"设备管理",key:"LedDevice"},{label:"颜色管理",key:"LedColor"},{label:"Netty 管理",key:"ZintisNetty"}]},{label:"EPC 管理",key:"epc",children:[{label:"批次 EPC 绑定",key:"BatchEpc"},{label:"RFID Server",key:"RfidServer"}]},{label:"拣货管理",key:"picking",children:[{label:"拣货单",key:"PickingUpload"},{label:"用户灯色映射",key:"UserLightColor"}]},{label:"系统管理",key:"system",children:[{label:"用户管理",key:"AdminUser"},{label:"App 版本管理",key:"AppVersion"},{label:"App 运行日志",key:"AppDeviceLog"},{label:"系统授权",key:"SystemLicense"},{label:"授权签发",key:"LicenseIssue"},{label:"数据库管理",key:"DatabaseManagement"},{label:"定时任务",key:"JobManagement"},{label:"AIMS 配置",key:"AimsConfig"},{label:"Magic-API",key:"MagicApiConsole"},{label:"实时日志",key:"RealtimeLog"}]}],z=R(()=>je()==="admin"?F:F.map(t=>t.children?{...t,children:t.children.filter(a=>Ae(String(a.key)))}:t).filter(t=>!t.children||t.children.length>0)),u=R(()=>String(r.name)),l=N(null);async function i(){try{const t=await Ke();if(!t.enabled||t.status==="ok"||t.status==="disabled"){l.value=null;return}t.status==="warning"?l.value={type:"warning",text:`⚠ ${t.message}（剩余 ${t.remainDays??"-"} 天）`}:t.status==="grace"||t.status==="expired"||t.status==="abnormal"?l.value={type:"error",text:`⛔ ${t.message}。可在「系统管理 → 系统授权」导入新授权文件恢复`}:l.value={type:"error",text:`⛔ ${t.message}`}}catch{l.value=null}}Be(()=>{i(),setInterval(i,6e4)});function O(t){o.push({name:t})}function $(){var a;const t=r.name;if(t){const Q=((a=r.meta)==null?void 0:a.label)||t;w(t,Q),q(t)}}_e(()=>r.name,$,{immediate:!0});function J(t){t!==r.name&&o.push({name:t})}function P(t){const a=p(t);a&&a!==r.name&&o.push({name:a})}function pe(){b.warning({title:"确认退出",content:"确定要退出登录吗？",positiveText:"确认",negativeText:"取消",onPositiveClick:()=>{Ve(),ce(),S(),o.push({name:"Login"})}})}return(t,a)=>{const Q=ue("router-view"),ye=ue("n-space");return M(),Y(n(be),{class:"admin-shell","has-sider":"","content-style":"height: 100%"},{default:x(()=>[v(n(bo),{class:"admin-sider",bordered:"",width:220,"native-scrollbar":!1,"collapse-mode":"width","collapsed-width":64,"show-trigger":!1},{default:x(()=>[a[5]||(a[5]=H("div",{class:"admin-brand"}," 东信和平管理后台 ",-1)),v(n(Je),{class:"admin-menu",options:z.value,value:u.value,"onUpdate:value":O},null,8,["options","value"])]),_:1}),v(n(be),{class:"admin-main","content-style":"height: 100%; min-height: 0; display: flex; flex-direction: column"},{default:x(()=>[v(n(no),{bordered:"",class:"admin-header"},{default:x(()=>[n(I)?(M(),W("span",go,[D(K(n(I).name||n(I).username)+" ",1),v(n(Xe),{size:"small",type:n(I).role==="admin"?"success":"info",bordered:!1},{default:x(()=>[D(K(n(I).role==="admin"?"管理员":"运营"),1)]),_:1},8,["type"])])):Z("",!0),v(n(ie),{text:"",onClick:a[0]||(a[0]=h=>L.value=!0)},{default:x(()=>[...a[6]||(a[6]=[D("修改密码",-1)])]),_:1}),v(n(ie),{text:"",onClick:pe},{default:x(()=>[...a[7]||(a[7]=[D("退出登录",-1)])]),_:1})]),_:1}),l.value?(M(),Y(n(Ze),{key:0,type:l.value.type,class:"admin-license-banner",bordered:!1},{default:x(()=>[D(K(l.value.text),1)]),_:1},8,["type"])):Z("",!0),H("div",{ref_key:"tabsRef",ref:T,class:"admin-tabs",onWheel:de(k,["prevent"])},[(M(!0),W(Le,null,Re(n(s),h=>(M(),W("div",{ref_for:!0,ref:X=>y(h.name,X),key:h.name,class:Ie(["admin-tab",{"admin-tab--active":h.name===n(r).name}]),onClick:X=>J(h.name)},[H("span",null,K(h.label),1),h.name!=="Dashboard"?(M(),W("span",{key:0,class:"admin-tab__close",onClick:de(X=>P(h.name),["stop"])},"✕",8,fo)):Z("",!0)],10,ho))),128))],544),v(n(ro),{class:"admin-content","content-style":"height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"},{default:x(()=>[H("div",mo,[v(Q,null,{default:x(({Component:h})=>[(M(),Y(ze,{include:n(B)},[(M(),Y($e(h),{class:"admin-page-content"}))],1032,["include"]))]),_:1})])]),_:1})]),_:1}),v(n(Me),{show:L.value,"onUpdate:show":a[4]||(a[4]=h=>L.value=h),title:"修改密码",preset:"dialog","positive-text":"确认修改","negative-text":"取消",onPositiveClick:E},{default:x(()=>[v(ye,{vertical:""},{default:x(()=>[v(n(ee),{label:"原密码",required:""},{default:x(()=>[v(n(oe),{value:c.oldPwd,"onUpdate:value":a[1]||(a[1]=h=>c.oldPwd=h),type:"password","show-password-on":"click",placeholder:"请输入原密码"},null,8,["value"])]),_:1}),v(n(ee),{label:"新密码",required:""},{default:x(()=>[v(n(oe),{value:c.newPwd,"onUpdate:value":a[2]||(a[2]=h=>c.newPwd=h),type:"password","show-password-on":"click",placeholder:"至少6位"},null,8,["value"])]),_:1}),v(n(ee),{label:"确认新密码",required:""},{default:x(()=>[v(n(oe),{value:c.confirmPwd,"onUpdate:value":a[3]||(a[3]=h=>c.confirmPwd=h),type:"password","show-password-on":"click",placeholder:"再次输入新密码"},null,8,["value"])]),_:1})]),_:1})]),_:1},8,["show"])]),_:1})}}}),Fo=Ge(vo,[["__scopeId","data-v-ca29e101"]]);export{Fo as default};

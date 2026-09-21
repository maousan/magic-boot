import{af as se,bM as ie,ak as ce,a2 as Q,J as d,Q as x,ai as O,aF as v,S as oe,c1 as K,ce as M,c8 as te,cf as X,a3 as S,bx as w,ab as de,bt as re,P as g,H as N,d as ue,aJ as be,Y as V,bW as Z,cj as he,a7 as W,cl as R,b_ as k,bq as E,ag as z,a6 as F,B as ge,ae as fe,a9 as H,bz as ve,be as me,bV as pe,co as G,a8 as ye,b as Ce,K as xe,bF as Se,c9 as Te,b3 as ke,bE as Be,ca as _e,bd as we}from"./index-BZ9k8jF4.js";import{n as Re}from"./composables-CkjVPYPE.js";import{p as q,l as ze,N as Ie}from"./Menu-L7NMdmDW.js";import{C as Le}from"./Dropdown-xWIsNXG0.js";import{f as U,u as Pe}from"./get-B4OEzvI8.js";import{_ as $e}from"./_plugin-vue_export-helper-DlAUqK2U.js";import"./light-D4-_KPEm.js";import"./light-D4FWpzNI.js";function Ee(e){const{baseColor:o,textColor2:r,bodyColor:n,cardColor:c,dividerColor:a,actionColor:m,scrollbarColor:u,scrollbarColorHover:b,invertedColor:p}=e;return{textColor:r,textColorInverted:"#FFF",color:n,colorEmbedded:m,headerColor:c,headerColorInverted:p,footerColor:m,footerColorInverted:p,headerBorderColor:a,headerBorderColorInverted:p,footerBorderColor:a,footerBorderColorInverted:p,siderBorderColor:a,siderBorderColorInverted:p,siderColor:c,siderColorInverted:p,siderToggleButtonBorder:`1px solid ${a}`,siderToggleButtonColor:o,siderToggleButtonIconColor:r,siderToggleButtonIconColorInverted:r,siderToggleBarColor:Q(n,u),siderToggleBarColorHover:Q(n,b),__invertScrollbar:"true"}}const J=se({name:"Layout",common:ce,peers:{Scrollbar:ie},self:Ee}),Me=d("layout",`
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
`,[d("layout-scroll-container",`
 overflow-x: hidden;
 box-sizing: border-box;
 height: 100%;
 `),x("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),Oe={embedded:Boolean,position:q,nativeScrollbar:{type:Boolean,default:!0},scrollbarProps:Object,onScroll:Function,contentClass:String,contentStyle:{type:[String,Object],default:""},hasSider:Boolean,siderPlacement:{type:String,default:"left"}},le=de("n-layout");function ne(e){return O({name:e?"LayoutContent":"Layout",props:Object.assign(Object.assign({},M.props),Oe),setup(o){const r=w(null),n=w(null),{mergedClsPrefixRef:c,inlineThemeDisabled:a}=K(o),m=M("Layout","-layout",Me,J,o,c);function u(h,f){if(o.nativeScrollbar){const{value:y}=r;y&&(f===void 0?y.scrollTo(h):y.scrollTo(h,f))}else{const{value:y}=n;y&&y.scrollTo(h,f)}}re(le,o);let b=0,p=0;const j=h=>{var f;const y=h.target;b=y.scrollLeft,p=y.scrollTop,(f=o.onScroll)===null||f===void 0||f.call(o,h)};te(()=>{if(o.nativeScrollbar){const h=r.value;h&&(h.scrollTop=p,h.scrollLeft=b)}});const I={display:"flex",flexWrap:"nowrap",width:"100%",flexDirection:"row"},L={scrollTo:u},P=S(()=>{const{common:{cubicBezierEaseInOut:h},self:f}=m.value;return{"--n-bezier":h,"--n-color":o.embedded?f.colorEmbedded:f.color,"--n-text-color":f.textColor}}),C=a?X("layout",S(()=>o.embedded?"e":""),P,o):void 0;return Object.assign({mergedClsPrefix:c,scrollableElRef:r,scrollbarInstRef:n,hasSiderStyle:I,mergedTheme:m,handleNativeElScroll:j,cssVars:a?void 0:P,themeClass:C==null?void 0:C.themeClass,onRender:C==null?void 0:C.onRender},L)},render(){var o;const{mergedClsPrefix:r,hasSider:n}=this;(o=this.onRender)===null||o===void 0||o.call(this);const c=n?this.hasSiderStyle:void 0,a=[this.themeClass,e&&`${r}-layout-content`,`${r}-layout`,`${r}-layout--${this.position}-positioned`];return v("div",{class:a,style:this.cssVars},this.nativeScrollbar?v("div",{ref:"scrollableElRef",class:[`${r}-layout-scroll-container`,this.contentClass],style:[this.contentStyle,c],onScroll:this.handleNativeElScroll},this.$slots):v(oe,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,contentClass:this.contentClass,contentStyle:[this.contentStyle,c]}),this.$slots))}})}const ee=ne(!1),je=ne(!0),Ne=d("layout-header",`
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 box-sizing: border-box;
 width: 100%;
 background-color: var(--n-color);
 color: var(--n-text-color);
`,[x("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 `),x("bordered",`
 border-bottom: solid 1px var(--n-border-color);
 `)]),Ae={position:q,inverted:Boolean,bordered:{type:Boolean,default:!1}},Ve=O({name:"LayoutHeader",props:Object.assign(Object.assign({},M.props),Ae),setup(e){const{mergedClsPrefixRef:o,inlineThemeDisabled:r}=K(e),n=M("Layout","-layout-header",Ne,J,e,o),c=S(()=>{const{common:{cubicBezierEaseInOut:m},self:u}=n.value,b={"--n-bezier":m};return e.inverted?(b["--n-color"]=u.headerColorInverted,b["--n-text-color"]=u.textColorInverted,b["--n-border-color"]=u.headerBorderColorInverted):(b["--n-color"]=u.headerColor,b["--n-text-color"]=u.textColor,b["--n-border-color"]=u.headerBorderColor),b}),a=r?X("layout-header",S(()=>e.inverted?"a":"b"),c,e):void 0;return{mergedClsPrefix:o,cssVars:r?void 0:c,themeClass:a==null?void 0:a.themeClass,onRender:a==null?void 0:a.onRender}},render(){var e;const{mergedClsPrefix:o}=this;return(e=this.onRender)===null||e===void 0||e.call(this),v("div",{class:[`${o}-layout-header`,this.themeClass,this.position&&`${o}-layout-header--${this.position}-positioned`,this.bordered&&`${o}-layout-header--bordered`],style:this.cssVars},this.$slots)}}),Fe=d("layout-sider",`
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
`,[x("bordered",[g("border",`
 content: "";
 position: absolute;
 top: 0;
 bottom: 0;
 width: 1px;
 background-color: var(--n-border-color);
 transition: background-color .3s var(--n-bezier);
 `)]),g("left-placement",[x("bordered",[g("border",`
 right: 0;
 `)])]),x("right-placement",`
 justify-content: flex-start;
 `,[x("bordered",[g("border",`
 left: 0;
 `)]),x("collapsed",[d("layout-toggle-button",[d("base-icon",`
 transform: rotate(180deg);
 `)]),d("layout-toggle-bar",[N("&:hover",[g("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),g("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])])]),d("layout-toggle-button",`
 left: 0;
 transform: translateX(-50%) translateY(-50%);
 `,[d("base-icon",`
 transform: rotate(0);
 `)]),d("layout-toggle-bar",`
 left: -28px;
 transform: rotate(180deg);
 `,[N("&:hover",[g("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),g("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})])])]),x("collapsed",[d("layout-toggle-bar",[N("&:hover",[g("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),g("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])]),d("layout-toggle-button",[d("base-icon",`
 transform: rotate(0);
 `)])]),d("layout-toggle-button",`
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
 `,[d("base-icon",`
 transition: transform .3s var(--n-bezier);
 transform: rotate(180deg);
 `)]),d("layout-toggle-bar",`
 cursor: pointer;
 height: 72px;
 width: 32px;
 position: absolute;
 top: calc(50% - 36px);
 right: -28px;
 `,[g("top, bottom",`
 position: absolute;
 width: 4px;
 border-radius: 2px;
 height: 38px;
 left: 14px;
 transition: 
 background-color .3s var(--n-bezier),
 transform .3s var(--n-bezier);
 `),g("bottom",`
 position: absolute;
 top: 34px;
 `),N("&:hover",[g("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),g("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})]),g("top, bottom",{backgroundColor:"var(--n-toggle-bar-color)"}),N("&:hover",[g("top, bottom",{backgroundColor:"var(--n-toggle-bar-color-hover)"})])]),g("border",`
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 width: 1px;
 transition: background-color .3s var(--n-bezier);
 `),d("layout-sider-scroll-container",`
 flex-grow: 1;
 flex-shrink: 0;
 box-sizing: border-box;
 height: 100%;
 opacity: 0;
 transition: opacity .3s var(--n-bezier);
 max-width: 100%;
 `),x("show-content",[d("layout-sider-scroll-container",{opacity:1})]),x("absolute-positioned",`
 position: absolute;
 left: 0;
 top: 0;
 bottom: 0;
 `)]),De=O({props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return v("div",{onClick:this.onClick,class:`${e}-layout-toggle-bar`},v("div",{class:`${e}-layout-toggle-bar__top`}),v("div",{class:`${e}-layout-toggle-bar__bottom`}))}}),Ye=O({name:"LayoutToggleButton",props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return v("div",{class:`${e}-layout-toggle-button`,onClick:this.onClick},v(ue,{clsPrefix:e},{default:()=>v(Le,null)}))}}),We={position:q,bordered:Boolean,collapsedWidth:{type:Number,default:48},width:{type:[Number,String],default:272},contentClass:String,contentStyle:{type:[String,Object],default:""},collapseMode:{type:String,default:"transform"},collapsed:{type:Boolean,default:void 0},defaultCollapsed:Boolean,showCollapsedContent:{type:Boolean,default:!0},showTrigger:{type:[Boolean,String],default:!1},nativeScrollbar:{type:Boolean,default:!0},inverted:Boolean,scrollbarProps:Object,triggerClass:String,triggerStyle:[String,Object],collapsedTriggerClass:String,collapsedTriggerStyle:[String,Object],"onUpdate:collapsed":[Function,Array],onUpdateCollapsed:[Function,Array],onAfterEnter:Function,onAfterLeave:Function,onExpand:[Function,Array],onCollapse:[Function,Array],onScroll:Function},He=O({name:"LayoutSider",props:Object.assign(Object.assign({},M.props),We),setup(e){const o=be(le),r=w(null),n=w(null),c=w(e.defaultCollapsed),a=Pe(Z(e,"collapsed"),c),m=S(()=>U(a.value?e.collapsedWidth:e.width)),u=S(()=>e.collapseMode!=="transform"?{}:{minWidth:U(e.width)}),b=S(()=>o?o.siderPlacement:"left");function p(t,l){if(e.nativeScrollbar){const{value:s}=r;s&&(l===void 0?s.scrollTo(t):s.scrollTo(t,l))}else{const{value:s}=n;s&&s.scrollTo(t,l)}}function j(){const{"onUpdate:collapsed":t,onUpdateCollapsed:l,onExpand:s,onCollapse:_}=e,{value:$}=a;l&&V(l,!$),t&&V(t,!$),c.value=!$,$?s&&V(s):_&&V(_)}let I=0,L=0;const P=t=>{var l;const s=t.target;I=s.scrollLeft,L=s.scrollTop,(l=e.onScroll)===null||l===void 0||l.call(e,t)};te(()=>{if(e.nativeScrollbar){const t=r.value;t&&(t.scrollTop=L,t.scrollLeft=I)}}),re(ze,{collapsedRef:a,collapseModeRef:Z(e,"collapseMode")});const{mergedClsPrefixRef:C,inlineThemeDisabled:h}=K(e),f=M("Layout","-layout-sider",Fe,J,e,C);function y(t){var l,s;t.propertyName==="max-width"&&(a.value?(l=e.onAfterLeave)===null||l===void 0||l.call(e):(s=e.onAfterEnter)===null||s===void 0||s.call(e))}const Y={scrollTo:p},A=S(()=>{const{common:{cubicBezierEaseInOut:t},self:l}=f.value,{siderToggleButtonColor:s,siderToggleButtonBorder:_,siderToggleBarColor:$,siderToggleBarColorHover:ae}=l,T={"--n-bezier":t,"--n-toggle-button-color":s,"--n-toggle-button-border":_,"--n-toggle-bar-color":$,"--n-toggle-bar-color-hover":ae};return e.inverted?(T["--n-color"]=l.siderColorInverted,T["--n-text-color"]=l.textColorInverted,T["--n-border-color"]=l.siderBorderColorInverted,T["--n-toggle-button-icon-color"]=l.siderToggleButtonIconColorInverted,T.__invertScrollbar=l.__invertScrollbar):(T["--n-color"]=l.siderColor,T["--n-text-color"]=l.textColor,T["--n-border-color"]=l.siderBorderColor,T["--n-toggle-button-icon-color"]=l.siderToggleButtonIconColor),T}),i=h?X("layout-sider",S(()=>e.inverted?"a":"b"),A,e):void 0;return Object.assign({scrollableElRef:r,scrollbarInstRef:n,mergedClsPrefix:C,mergedTheme:f,styleMaxWidth:m,mergedCollapsed:a,scrollContainerStyle:u,siderPlacement:b,handleNativeElScroll:P,handleTransitionend:y,handleTriggerClick:j,inlineThemeDisabled:h,cssVars:A,themeClass:i==null?void 0:i.themeClass,onRender:i==null?void 0:i.onRender},Y)},render(){var e;const{mergedClsPrefix:o,mergedCollapsed:r,showTrigger:n}=this;return(e=this.onRender)===null||e===void 0||e.call(this),v("aside",{class:[`${o}-layout-sider`,this.themeClass,`${o}-layout-sider--${this.position}-positioned`,`${o}-layout-sider--${this.siderPlacement}-placement`,this.bordered&&`${o}-layout-sider--bordered`,r&&`${o}-layout-sider--collapsed`,(!r||this.showCollapsedContent)&&`${o}-layout-sider--show-content`],onTransitionend:this.handleTransitionend,style:[this.inlineThemeDisabled?void 0:this.cssVars,{maxWidth:this.styleMaxWidth,width:U(this.width)}]},this.nativeScrollbar?v("div",{class:[`${o}-layout-sider-scroll-container`,this.contentClass],onScroll:this.handleNativeElScroll,style:[this.scrollContainerStyle,{overflow:"auto"},this.contentStyle],ref:"scrollableElRef"},this.$slots):v(oe,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",style:this.scrollContainerStyle,contentStyle:this.contentStyle,contentClass:this.contentClass,theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,builtinThemeOverrides:this.inverted&&this.cssVars.__invertScrollbar==="true"?{colorHover:"rgba(255, 255, 255, .4)",color:"rgba(255, 255, 255, .3)"}:void 0}),this.$slots),n?n==="bar"?v(De,{clsPrefix:o,class:r?this.collapsedTriggerClass:this.triggerClass,style:r?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):v(Ye,{clsPrefix:o,class:r?this.collapsedTriggerClass:this.triggerClass,style:r?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):null,this.bordered?v("div",{class:`${o}-layout-sider__border`}):null)}}),B=w([{name:"Dashboard",label:"仪表盘"}]),D=w("Dashboard");function Ue(){const e=S(()=>B.value.map(n=>n.name));function o(n,c){B.value.find(a=>a.name===n)||B.value.push({name:n,label:c}),D.value=n}function r(n){if(n==="Dashboard")return null;const c=B.value.findIndex(m=>m.name===n);if(c===-1)return null;const a=D.value===n;if(B.value.splice(c,1),a&&B.value.length>0){const m=Math.min(c,B.value.length-1),u=B.value[m].name;return D.value=u,u}return null}return{tabs:B,activeTab:D,cachedNames:e,addTab:o,removeTab:r}}const Ke=["onClick"],Xe=["onClick"],qe={class:"admin-page"},Je=O({__name:"AdminLayout",setup(e){const o=_e(),r=Te(),n=Re(),{tabs:c,cachedNames:a,addTab:m,removeTab:u}=Ue(),b=w(null),p=new Map;function j(i,t){t instanceof HTMLElement&&p.set(i,t)}function I(i){var t;(t=b.value)==null||t.scrollBy({left:i.deltaY,behavior:"smooth"})}function L(i){we(()=>{var t;(t=p.get(i))==null||t.scrollIntoView({behavior:"smooth",inline:"nearest",block:"nearest"})})}const P=[{label:"仪表盘",key:"Dashboard"},{label:"库位管理",key:"location",children:[{label:"库位列表",key:"WarehouseLocation"},{label:"巷道灯绑定",key:"LedMapping"},{label:"标签绑定",key:"LabelMapping"},{label:"库存查询",key:"InventoryQuery"}]},{label:"巷道灯管理",key:"led",children:[{label:"设备管理",key:"LedDevice"},{label:"颜色管理",key:"LedColor"},{label:"Netty 管理",key:"ZintisNetty"}]},{label:"EPC 管理",key:"epc",children:[{label:"批次 EPC 绑定",key:"BatchEpc"},{label:"RFID Server",key:"RfidServer"}]},{label:"拣货管理",key:"picking",children:[{label:"拣货单",key:"PickingUpload"},{label:"用户灯色映射",key:"UserLightColor"}]},{label:"系统管理",key:"system",children:[{label:"App 版本管理",key:"AppVersion"},{label:"数据库管理",key:"DatabaseManagement"},{label:"定时任务",key:"JobManagement"},{label:"AIMS 配置",key:"AimsConfig"},{label:"Magic-API",key:"MagicApiConsole"},{label:"实时日志",key:"RealtimeLog"}]}],C=S(()=>String(r.name));function h(i){o.push({name:i})}function f(){var t;const i=r.name;if(i){const l=((t=r.meta)==null?void 0:t.label)||i;m(i,l),L(i)}}he(()=>r.name,f,{immediate:!0});function y(i){i!==r.name&&o.push({name:i})}function Y(i){const t=u(i);t&&t!==r.name&&o.push({name:t})}function A(){n.warning({title:"确认退出",content:"确定要退出登录吗？",positiveText:"确认",negativeText:"取消",onPositiveClick:()=>{ke(),o.push({name:"Login"})}})}return(i,t)=>{const l=Be("router-view");return E(),W(k(ee),{class:"admin-shell","has-sider":"","content-style":"height: 100%"},{default:R(()=>[z(k(He),{class:"admin-sider",bordered:"",width:220,"native-scrollbar":!1,"collapse-mode":"width","collapsed-width":64,"show-trigger":!1},{default:R(()=>[t[0]||(t[0]=F("div",{class:"admin-brand"}," 东信和平管理后台 ",-1)),z(k(Ie),{class:"admin-menu",options:P,value:C.value,"onUpdate:value":h},null,8,["value"])]),_:1}),z(k(ee),{class:"admin-main","content-style":"height: 100%; min-height: 0; display: flex; flex-direction: column"},{default:R(()=>[z(k(Ve),{bordered:"",class:"admin-header"},{default:R(()=>[z(k(ge),{text:"",onClick:A},{default:R(()=>[...t[1]||(t[1]=[fe("退出登录",-1)])]),_:1})]),_:1}),F("div",{ref_key:"tabsRef",ref:b,class:"admin-tabs",onWheel:G(I,["prevent"])},[(E(!0),H(Ce,null,ve(k(c),s=>(E(),H("div",{ref_for:!0,ref:_=>j(s.name,_),key:s.name,class:me(["admin-tab",{"admin-tab--active":s.name===k(r).name}]),onClick:_=>y(s.name)},[F("span",null,pe(s.label),1),s.name!=="Dashboard"?(E(),H("span",{key:0,class:"admin-tab__close",onClick:G(_=>Y(s.name),["stop"])},"✕",8,Xe)):ye("",!0)],10,Ke))),128))],544),z(k(je),{class:"admin-content","content-style":"height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"},{default:R(()=>[F("div",qe,[z(l,null,{default:R(({Component:s})=>[(E(),W(xe,{include:k(a)},[(E(),W(Se(s),{class:"admin-page-content"}))],1032,["include"]))]),_:1})])]),_:1})]),_:1})]),_:1})}}}),no=$e(Je,[["__scopeId","data-v-8a863e6d"]]);export{no as default};

import{ai as xe,bY as we,an as Se,a4 as ie,J as g,Q as R,al as Y,aL as C,S as ge,ch as le,cu as F,co as me,cv as se,a5 as I,bI as $,ae as Te,bE as ve,P as v,H as U,d as Be,aP as Pe,Y as H,c9 as de,aB as ke,bz as _e,cz as ze,aa as W,cB as x,cd as c,ce as Re,bB as j,aj as p,a9 as K,ac as q,ah as A,c8 as X,ab as Z,B as ce,bK as Ie,bo as $e,cE as ue,b as Le,K as Ne,bR as Oe,l as je,cp as Ee,bG as Me,$ as be,bP as he,cq as Ve,bn as Fe}from"./index-BsR5apVf.js";import{d as Ye,c as Ue,a as Ae}from"./auth-DtznBFFP.js";import{f as ee,h as De,u as He,j as We,l as Ke}from"./request-BLUWwSD1.js";import{b as oe}from"./menus-DqdAwNzi.js";import{g as qe}from"./license-RGb25ja0.js";import{p as ne,l as Xe,N as Ge}from"./Menu-DA4VcanW.js";import{C as Je}from"./Dropdown-BUQehJmW.js";import{a as Qe}from"./Tag-dcRWa-Ja.js";import{N as Ze}from"./Alert-g-uzW5Rv.js";import{N as te}from"./FormItem-DLcfhm2M.js";import{N as re}from"./Input-CjNCaY8D.js";import{_ as eo}from"./_plugin-vue_export-helper-DlAUqK2U.js";import"./core-BNwxd6Ld.js";import"./Tooltip-DdOLZ4pO.js";import"./light-Ccz9A-l8.js";import"./create-DE5omGJJ.js";import"./Follower-Bea9S8HJ.js";import"./light-BhA3xn9i.js";import"./light-cLQEZbxF.js";import"./happens-in-CM8LO42l.js";import"./use-keyboard-Lfk8-FGf.js";import"./_common-Dh8oehso.js";import"./light-CJAwsIVw.js";import"./Suffix-CVhn1LlT.js";import"./Eye-D3_0eIiV.js";function oo(e){const{baseColor:o,textColor2:r,bodyColor:h,cardColor:i,dividerColor:n,actionColor:_,scrollbarColor:w,scrollbarColorHover:y,invertedColor:S}=e;return{textColor:r,textColorInverted:"#FFF",color:h,colorEmbedded:_,headerColor:i,headerColorInverted:S,footerColor:_,footerColorInverted:S,headerBorderColor:n,headerBorderColorInverted:S,footerBorderColor:n,footerBorderColorInverted:S,siderBorderColor:n,siderBorderColorInverted:S,siderColor:i,siderColorInverted:S,siderToggleButtonBorder:`1px solid ${n}`,siderToggleButtonColor:o,siderToggleButtonIconColor:r,siderToggleButtonIconColorInverted:r,siderToggleBarColor:ie(h,w),siderToggleBarColorHover:ie(h,y),__invertScrollbar:"true"}}const ae=xe({name:"Layout",common:Se,peers:{Scrollbar:we},self:oo}),to=g("layout",`
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
`,[g("layout-scroll-container",`
 overflow-x: hidden;
 box-sizing: border-box;
 height: 100%;
 `),R("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),ro={embedded:Boolean,position:ne,nativeScrollbar:{type:Boolean,default:!0},scrollbarProps:Object,onScroll:Function,contentClass:String,contentStyle:{type:[String,Object],default:""},hasSider:Boolean,siderPlacement:{type:String,default:"left"}},pe=Te("n-layout");function ye(e){return Y({name:e?"LayoutContent":"Layout",props:Object.assign(Object.assign({},F.props),ro),setup(o){const r=$(null),h=$(null),{mergedClsPrefixRef:i,inlineThemeDisabled:n}=le(o),_=F("Layout","-layout",to,ae,o,i);function w(f,m){if(o.nativeScrollbar){const{value:B}=r;B&&(m===void 0?B.scrollTo(f):B.scrollTo(f,m))}else{const{value:B}=h;B&&B.scrollTo(f,m)}}ve(pe,o);let y=0,S=0;const P=f=>{var m;const B=f.target;y=B.scrollLeft,S=B.scrollTop,(m=o.onScroll)===null||m===void 0||m.call(o,f)};me(()=>{if(o.nativeScrollbar){const f=r.value;f&&(f.scrollTop=S,f.scrollLeft=y)}});const E={display:"flex",flexWrap:"nowrap",width:"100%",flexDirection:"row"},L={scrollTo:w},u=I(()=>{const{common:{cubicBezierEaseInOut:f},self:m}=_.value;return{"--n-bezier":f,"--n-color":o.embedded?m.colorEmbedded:m.color,"--n-text-color":m.textColor}}),z=n?se("layout",I(()=>o.embedded?"e":""),u,o):void 0;return Object.assign({mergedClsPrefix:i,scrollableElRef:r,scrollbarInstRef:h,hasSiderStyle:E,mergedTheme:_,handleNativeElScroll:P,cssVars:n?void 0:u,themeClass:z==null?void 0:z.themeClass,onRender:z==null?void 0:z.onRender},L)},render(){var o;const{mergedClsPrefix:r,hasSider:h}=this;(o=this.onRender)===null||o===void 0||o.call(this);const i=h?this.hasSiderStyle:void 0,n=[this.themeClass,e&&`${r}-layout-content`,`${r}-layout`,`${r}-layout--${this.position}-positioned`];return C("div",{class:n,style:this.cssVars},this.nativeScrollbar?C("div",{ref:"scrollableElRef",class:[`${r}-layout-scroll-container`,this.contentClass],style:[this.contentStyle,i],onScroll:this.handleNativeElScroll},this.$slots):C(ge,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,contentClass:this.contentClass,contentStyle:[this.contentStyle,i]}),this.$slots))}})}const fe=ye(!1),lo=ye(!0),so=g("layout-header",`
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 box-sizing: border-box;
 width: 100%;
 background-color: var(--n-color);
 color: var(--n-text-color);
`,[R("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 `),R("bordered",`
 border-bottom: solid 1px var(--n-border-color);
 `)]),no={position:ne,inverted:Boolean,bordered:{type:Boolean,default:!1}},ao=Y({name:"LayoutHeader",props:Object.assign(Object.assign({},F.props),no),setup(e){const{mergedClsPrefixRef:o,inlineThemeDisabled:r}=le(e),h=F("Layout","-layout-header",so,ae,e,o),i=I(()=>{const{common:{cubicBezierEaseInOut:_},self:w}=h.value,y={"--n-bezier":_};return e.inverted?(y["--n-color"]=w.headerColorInverted,y["--n-text-color"]=w.textColorInverted,y["--n-border-color"]=w.headerBorderColorInverted):(y["--n-color"]=w.headerColor,y["--n-text-color"]=w.textColor,y["--n-border-color"]=w.headerBorderColor),y}),n=r?se("layout-header",I(()=>e.inverted?"a":"b"),i,e):void 0;return{mergedClsPrefix:o,cssVars:r?void 0:i,themeClass:n==null?void 0:n.themeClass,onRender:n==null?void 0:n.onRender}},render(){var e;const{mergedClsPrefix:o}=this;return(e=this.onRender)===null||e===void 0||e.call(this),C("div",{class:[`${o}-layout-header`,this.themeClass,this.position&&`${o}-layout-header--${this.position}-positioned`,this.bordered&&`${o}-layout-header--bordered`],style:this.cssVars},this.$slots)}}),io=g("layout-sider",`
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
`,[R("bordered",[v("border",`
 content: "";
 position: absolute;
 top: 0;
 bottom: 0;
 width: 1px;
 background-color: var(--n-border-color);
 transition: background-color .3s var(--n-bezier);
 `)]),v("left-placement",[R("bordered",[v("border",`
 right: 0;
 `)])]),R("right-placement",`
 justify-content: flex-start;
 `,[R("bordered",[v("border",`
 left: 0;
 `)]),R("collapsed",[g("layout-toggle-button",[g("base-icon",`
 transform: rotate(180deg);
 `)]),g("layout-toggle-bar",[U("&:hover",[v("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),v("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])])]),g("layout-toggle-button",`
 left: 0;
 transform: translateX(-50%) translateY(-50%);
 `,[g("base-icon",`
 transform: rotate(0);
 `)]),g("layout-toggle-bar",`
 left: -28px;
 transform: rotate(180deg);
 `,[U("&:hover",[v("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),v("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})])])]),R("collapsed",[g("layout-toggle-bar",[U("&:hover",[v("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),v("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])]),g("layout-toggle-button",[g("base-icon",`
 transform: rotate(0);
 `)])]),g("layout-toggle-button",`
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
 `,[g("base-icon",`
 transition: transform .3s var(--n-bezier);
 transform: rotate(180deg);
 `)]),g("layout-toggle-bar",`
 cursor: pointer;
 height: 72px;
 width: 32px;
 position: absolute;
 top: calc(50% - 36px);
 right: -28px;
 `,[v("top, bottom",`
 position: absolute;
 width: 4px;
 border-radius: 2px;
 height: 38px;
 left: 14px;
 transition: 
 background-color .3s var(--n-bezier),
 transform .3s var(--n-bezier);
 `),v("bottom",`
 position: absolute;
 top: 34px;
 `),U("&:hover",[v("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),v("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})]),v("top, bottom",{backgroundColor:"var(--n-toggle-bar-color)"}),U("&:hover",[v("top, bottom",{backgroundColor:"var(--n-toggle-bar-color-hover)"})])]),v("border",`
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 width: 1px;
 transition: background-color .3s var(--n-bezier);
 `),g("layout-sider-scroll-container",`
 flex-grow: 1;
 flex-shrink: 0;
 box-sizing: border-box;
 height: 100%;
 opacity: 0;
 transition: opacity .3s var(--n-bezier);
 max-width: 100%;
 `),R("show-content",[g("layout-sider-scroll-container",{opacity:1})]),R("absolute-positioned",`
 position: absolute;
 left: 0;
 top: 0;
 bottom: 0;
 `)]),co=Y({props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return C("div",{onClick:this.onClick,class:`${e}-layout-toggle-bar`},C("div",{class:`${e}-layout-toggle-bar__top`}),C("div",{class:`${e}-layout-toggle-bar__bottom`}))}}),uo=Y({name:"LayoutToggleButton",props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return C("div",{class:`${e}-layout-toggle-button`,onClick:this.onClick},C(Be,{clsPrefix:e},{default:()=>C(Je,null)}))}}),bo={position:ne,bordered:Boolean,collapsedWidth:{type:Number,default:48},width:{type:[Number,String],default:272},contentClass:String,contentStyle:{type:[String,Object],default:""},collapseMode:{type:String,default:"transform"},collapsed:{type:Boolean,default:void 0},defaultCollapsed:Boolean,showCollapsedContent:{type:Boolean,default:!0},showTrigger:{type:[Boolean,String],default:!1},nativeScrollbar:{type:Boolean,default:!0},inverted:Boolean,scrollbarProps:Object,triggerClass:String,triggerStyle:[String,Object],collapsedTriggerClass:String,collapsedTriggerStyle:[String,Object],"onUpdate:collapsed":[Function,Array],onUpdateCollapsed:[Function,Array],onAfterEnter:Function,onAfterLeave:Function,onExpand:[Function,Array],onCollapse:[Function,Array],onScroll:Function},ho=Y({name:"LayoutSider",props:Object.assign(Object.assign({},F.props),bo),setup(e){const o=Pe(pe),r=$(null),h=$(null),i=$(e.defaultCollapsed),n=De(de(e,"collapsed"),i),_=I(()=>ee(n.value?e.collapsedWidth:e.width)),w=I(()=>e.collapseMode!=="transform"?{}:{minWidth:ee(e.width)}),y=I(()=>o?o.siderPlacement:"left");function S(b,l){if(e.nativeScrollbar){const{value:a}=r;a&&(l===void 0?a.scrollTo(b):a.scrollTo(b,l))}else{const{value:a}=h;a&&a.scrollTo(b,l)}}function P(){const{"onUpdate:collapsed":b,onUpdateCollapsed:l,onExpand:a,onCollapse:M}=e,{value:O}=n;l&&H(l,!O),b&&H(b,!O),i.value=!O,O?a&&H(a):M&&H(M)}let E=0,L=0;const u=b=>{var l;const a=b.target;E=a.scrollLeft,L=a.scrollTop,(l=e.onScroll)===null||l===void 0||l.call(e,b)};me(()=>{if(e.nativeScrollbar){const b=r.value;b&&(b.scrollTop=L,b.scrollLeft=E)}}),ve(Xe,{collapsedRef:n,collapseModeRef:de(e,"collapseMode")});const{mergedClsPrefixRef:z,inlineThemeDisabled:f}=le(e),m=F("Layout","-layout-sider",io,ae,e,z);function B(b){var l,a;b.propertyName==="max-width"&&(n.value?(l=e.onAfterLeave)===null||l===void 0||l.call(e):(a=e.onAfterEnter)===null||a===void 0||a.call(e))}const G={scrollTo:S},D=I(()=>{const{common:{cubicBezierEaseInOut:b},self:l}=m.value,{siderToggleButtonColor:a,siderToggleButtonBorder:M,siderToggleBarColor:O,siderToggleBarColorHover:J}=l,k={"--n-bezier":b,"--n-toggle-button-color":a,"--n-toggle-button-border":M,"--n-toggle-bar-color":O,"--n-toggle-bar-color-hover":J};return e.inverted?(k["--n-color"]=l.siderColorInverted,k["--n-text-color"]=l.textColorInverted,k["--n-border-color"]=l.siderBorderColorInverted,k["--n-toggle-button-icon-color"]=l.siderToggleButtonIconColorInverted,k.__invertScrollbar=l.__invertScrollbar):(k["--n-color"]=l.siderColor,k["--n-text-color"]=l.textColor,k["--n-border-color"]=l.siderBorderColor,k["--n-toggle-button-icon-color"]=l.siderToggleButtonIconColor),k}),N=f?se("layout-sider",I(()=>e.inverted?"a":"b"),D,e):void 0;return Object.assign({scrollableElRef:r,scrollbarInstRef:h,mergedClsPrefix:z,mergedTheme:m,styleMaxWidth:_,mergedCollapsed:n,scrollContainerStyle:w,siderPlacement:y,handleNativeElScroll:u,handleTransitionend:B,handleTriggerClick:P,inlineThemeDisabled:f,cssVars:D,themeClass:N==null?void 0:N.themeClass,onRender:N==null?void 0:N.onRender},G)},render(){var e;const{mergedClsPrefix:o,mergedCollapsed:r,showTrigger:h}=this;return(e=this.onRender)===null||e===void 0||e.call(this),C("aside",{class:[`${o}-layout-sider`,this.themeClass,`${o}-layout-sider--${this.position}-positioned`,`${o}-layout-sider--${this.siderPlacement}-placement`,this.bordered&&`${o}-layout-sider--bordered`,r&&`${o}-layout-sider--collapsed`,(!r||this.showCollapsedContent)&&`${o}-layout-sider--show-content`],onTransitionend:this.handleTransitionend,style:[this.inlineThemeDisabled?void 0:this.cssVars,{maxWidth:this.styleMaxWidth,width:ee(this.width)}]},this.nativeScrollbar?C("div",{class:[`${o}-layout-sider-scroll-container`,this.contentClass],onScroll:this.handleNativeElScroll,style:[this.scrollContainerStyle,{overflow:"auto"},this.contentStyle],ref:"scrollableElRef"},this.$slots):C(ge,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",style:this.scrollContainerStyle,contentStyle:this.contentStyle,contentClass:this.contentClass,theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,builtinThemeOverrides:this.inverted&&this.cssVars.__invertScrollbar==="true"?{colorHover:"rgba(255, 255, 255, .4)",color:"rgba(255, 255, 255, .3)"}:void 0}),this.$slots),h?h==="bar"?C(co,{clsPrefix:o,class:r?this.collapsedTriggerClass:this.triggerClass,style:r?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):C(uo,{clsPrefix:o,class:r?this.collapsedTriggerClass:this.triggerClass,style:r?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):null,this.bordered?C("div",{class:`${o}-layout-sider__border`}):null)}}),fo={key:0,class:"admin-user-info"},go=["onClick"],mo=["onClick"],vo={class:"admin-page"},po=Y({__name:"AdminLayout",setup(e){const o=Ve(),r=Ee(),h=He(),i=We(),{tabs:n,cachedNames:_,addTab:w,removeTab:y,clearTabs:S}=Ke(),P=$(ke());async function E(){try{const t=await Ye();Re(t),P.value=t}catch{}}const L=$(!1),u=Me({oldPwd:"",newPwd:"",confirmPwd:""});async function z(){if(!u.oldPwd)return i.error("请输入原密码"),!1;if(u.newPwd.length<6)return i.error("新密码至少6位"),!1;if(u.newPwd!==u.confirmPwd)return i.error("两次输入的新密码不一致"),!1;if(u.newPwd===u.oldPwd)return i.error("新密码不能与原密码相同"),!1;await Ae(u.oldPwd,u.newPwd),i.success("密码已修改，请使用新密码重新登录"),be(),S(),o.push({name:"Login"})}const f=$(null),m=new Map;function B(t,s){s instanceof HTMLElement&&m.set(t,s)}function G(t){var s;(s=f.value)==null||s.scrollBy({left:t.deltaY,behavior:"smooth"})}function D(t){Fe(()=>{var s;(s=m.get(t))==null||s.scrollIntoView({behavior:"smooth",inline:"nearest",block:"nearest"})})}const N=I(()=>{var V;const t=(V=P.value)==null?void 0:V.menus;if(t==null)return oe.filter(T=>T.key==="Dashboard");if(t.includes("*"))return oe;const s=T=>T!=null&&t.includes(String(T));return oe.map(T=>T.children?{...T,children:T.children.filter(d=>s(d.key))}:T).filter(T=>!T.children||T.children.length>0)}),b=I(()=>String(r.name)),l=$(null);async function a(){try{const t=await qe();if(!t.enabled||t.status==="ok"||t.status==="disabled"){l.value=null;return}t.status==="warning"?l.value={type:"warning",text:`⚠ ${t.message}（剩余 ${t.remainDays??"-"} 天）`}:t.status==="grace"||t.status==="expired"||t.status==="abnormal"?l.value={type:"error",text:`⛔ ${t.message}。可在「系统管理 → 系统授权」导入新授权文件恢复`}:l.value={type:"error",text:`⛔ ${t.message}`}}catch{l.value=null}}_e(()=>{E(),a(),setInterval(a,6e4)});function M(t){o.push({name:t})}function O(){var s;const t=r.name;if(t){const V=((s=r.meta)==null?void 0:s.label)||t;w(t,V),D(t)}}ze(()=>r.name,O,{immediate:!0});function J(t){t!==r.name&&o.push({name:t})}function k(t){const s=y(t);s&&s!==r.name&&o.push({name:s})}function Ce(){h.warning({title:"确认退出",content:"确定要退出登录吗？",positiveText:"确认",negativeText:"取消",onPositiveClick:()=>{Ue(),be(),S(),o.push({name:"Login"})}})}return(t,s)=>{const V=he("router-view"),T=he("n-space");return j(),W(c(fe),{class:"admin-shell","has-sider":"","content-style":"height: 100%"},{default:x(()=>[p(c(ho),{class:"admin-sider",bordered:"",width:220,"native-scrollbar":!1,"collapse-mode":"width","collapsed-width":64,"show-trigger":!1},{default:x(()=>[s[5]||(s[5]=K("div",{class:"admin-brand"}," 东信和平管理后台 ",-1)),p(c(Ge),{class:"admin-menu",options:N.value,value:b.value,"onUpdate:value":M},null,8,["options","value"])]),_:1}),p(c(fe),{class:"admin-main","content-style":"height: 100%; min-height: 0; display: flex; flex-direction: column"},{default:x(()=>[p(c(ao),{bordered:"",class:"admin-header"},{default:x(()=>[P.value?(j(),q("span",fo,[A(X(P.value.name||P.value.username)+" ",1),p(c(Qe),{size:"small",type:P.value.role==="admin"?"success":"info",bordered:!1},{default:x(()=>[A(X(P.value.role==="admin"?"管理员":P.value.role),1)]),_:1},8,["type"])])):Z("",!0),p(c(ce),{text:"",onClick:s[0]||(s[0]=d=>L.value=!0)},{default:x(()=>[...s[6]||(s[6]=[A("修改密码",-1)])]),_:1}),p(c(ce),{text:"",onClick:Ce},{default:x(()=>[...s[7]||(s[7]=[A("退出登录",-1)])]),_:1})]),_:1}),l.value?(j(),W(c(Ze),{key:0,type:l.value.type,class:"admin-license-banner",bordered:!1},{default:x(()=>[A(X(l.value.text),1)]),_:1},8,["type"])):Z("",!0),K("div",{ref_key:"tabsRef",ref:f,class:"admin-tabs",onWheel:ue(G,["prevent"])},[(j(!0),q(Le,null,Ie(c(n),d=>(j(),q("div",{ref_for:!0,ref:Q=>B(d.name,Q),key:d.name,class:$e(["admin-tab",{"admin-tab--active":d.name===c(r).name}]),onClick:Q=>J(d.name)},[K("span",null,X(d.label),1),d.name!=="Dashboard"?(j(),q("span",{key:0,class:"admin-tab__close",onClick:ue(Q=>k(d.name),["stop"])},"✕",8,mo)):Z("",!0)],10,go))),128))],544),p(c(lo),{class:"admin-content","content-style":"height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"},{default:x(()=>[K("div",vo,[p(V,null,{default:x(({Component:d})=>[(j(),W(Ne,{include:c(_)},[(j(),W(Oe(d),{class:"admin-page-content"}))],1032,["include"]))]),_:1})])]),_:1})]),_:1}),p(c(je),{show:L.value,"onUpdate:show":s[4]||(s[4]=d=>L.value=d),title:"修改密码",preset:"dialog","positive-text":"确认修改","negative-text":"取消",onPositiveClick:z},{default:x(()=>[p(T,{vertical:""},{default:x(()=>[p(c(te),{label:"原密码",required:""},{default:x(()=>[p(c(re),{value:u.oldPwd,"onUpdate:value":s[1]||(s[1]=d=>u.oldPwd=d),type:"password","show-password-on":"click",placeholder:"请输入原密码"},null,8,["value"])]),_:1}),p(c(te),{label:"新密码",required:""},{default:x(()=>[p(c(re),{value:u.newPwd,"onUpdate:value":s[2]||(s[2]=d=>u.newPwd=d),type:"password","show-password-on":"click",placeholder:"至少6位"},null,8,["value"])]),_:1}),p(c(te),{label:"确认新密码",required:""},{default:x(()=>[p(c(re),{value:u.confirmPwd,"onUpdate:value":s[3]||(s[3]=d=>u.confirmPwd=d),type:"password","show-password-on":"click",placeholder:"再次输入新密码"},null,8,["value"])]),_:1})]),_:1})]),_:1},8,["show"])]),_:1})}}}),Do=eo(po,[["__scopeId","data-v-e7e29506"]]);export{Do as default};

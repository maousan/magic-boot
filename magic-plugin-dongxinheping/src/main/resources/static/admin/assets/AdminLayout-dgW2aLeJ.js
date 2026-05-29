import{ai as L,aF as u,af as eo,bM as oo,ak as to,a2 as Re,ab as Z,J as v,Q as R,S as _e,c1 as ie,ce as D,c8 as He,cf as ae,a3 as C,bx as B,bt as J,P as d,H as w,d as Be,aJ as V,Y as M,bW as le,R as Q,as as ro,by as X,b1 as pe,c5 as he,b as Ee,b0 as se,i as no,V as lo,ck as Te,aa as io,b5 as ao,cj as co,a7 as de,cl as G,b_ as F,bq as Y,ag as W,a6 as te,B as so,ae as uo,a9 as ue,bz as vo,be as mo,bV as ho,co as Pe,a8 as fo,K as go,bF as bo,c9 as po,b3 as xo,bE as Co,ca as yo,bd as zo}from"./index-CIC1_J-A.js";import{a as Io,m as wo,f as ve,n as So}from"./composables-nLmyrczr.js";import{C as Ro,a as To,N as Po}from"./Dropdown-BIE3TuJw.js";import{f as me,u as fe}from"./get-BLwuGfoW.js";import{m as ko}from"./light-BKUarT9W.js";import{_ as No}from"./_plugin-vue_export-helper-DlAUqK2U.js";import"./light-BGOoQkDp.js";const Ao=L({name:"ChevronDownFilled",render(){return u("svg",{viewBox:"0 0 16 16",fill:"none",xmlns:"http://www.w3.org/2000/svg"},u("path",{d:"M3.20041 5.73966C3.48226 5.43613 3.95681 5.41856 4.26034 5.70041L8 9.22652L11.7397 5.70041C12.0432 5.41856 12.5177 5.43613 12.7996 5.73966C13.0815 6.0432 13.0639 6.51775 12.7603 6.7996L8.51034 10.7996C8.22258 11.0668 7.77743 11.0668 7.48967 10.7996L3.23966 6.7996C2.93613 6.51775 2.91856 6.0432 3.20041 5.73966Z",fill:"currentColor"}))}});function _o(e){const{baseColor:t,textColor2:o,bodyColor:i,cardColor:a,dividerColor:l,actionColor:s,scrollbarColor:h,scrollbarColorHover:c,invertedColor:p}=e;return{textColor:o,textColorInverted:"#FFF",color:i,colorEmbedded:s,headerColor:a,headerColorInverted:p,footerColor:s,footerColorInverted:p,headerBorderColor:l,headerBorderColorInverted:p,footerBorderColor:l,footerBorderColorInverted:p,siderBorderColor:l,siderBorderColorInverted:p,siderColor:a,siderColorInverted:p,siderToggleButtonBorder:`1px solid ${l}`,siderToggleButtonColor:t,siderToggleButtonIconColor:o,siderToggleButtonIconColorInverted:o,siderToggleBarColor:Re(i,h),siderToggleBarColorHover:Re(i,c),__invertScrollbar:"true"}}const xe=eo({name:"Layout",common:to,peers:{Scrollbar:oo},self:_o}),Le=Z("n-layout-sider"),Ce={type:String,default:"static"},Ho=v("layout",`
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
`,[v("layout-scroll-container",`
 overflow-x: hidden;
 box-sizing: border-box;
 height: 100%;
 `),R("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),Bo={embedded:Boolean,position:Ce,nativeScrollbar:{type:Boolean,default:!0},scrollbarProps:Object,onScroll:Function,contentClass:String,contentStyle:{type:[String,Object],default:""},hasSider:Boolean,siderPlacement:{type:String,default:"left"}},Oe=Z("n-layout");function Me(e){return L({name:e?"LayoutContent":"Layout",props:Object.assign(Object.assign({},D.props),Bo),setup(t){const o=B(null),i=B(null),{mergedClsPrefixRef:a,inlineThemeDisabled:l}=ie(t),s=D("Layout","-layout",Ho,xe,t,a);function h(S,T){if(t.nativeScrollbar){const{value:N}=o;N&&(T===void 0?N.scrollTo(S):N.scrollTo(S,T))}else{const{value:N}=i;N&&N.scrollTo(S,T)}}J(Oe,t);let c=0,p=0;const H=S=>{var T;const N=S.target;c=N.scrollLeft,p=N.scrollTop,(T=t.onScroll)===null||T===void 0||T.call(t,S)};He(()=>{if(t.nativeScrollbar){const S=o.value;S&&(S.scrollTop=p,S.scrollLeft=c)}});const _={display:"flex",flexWrap:"nowrap",width:"100%",flexDirection:"row"},g={scrollTo:h},A=C(()=>{const{common:{cubicBezierEaseInOut:S},self:T}=s.value;return{"--n-bezier":S,"--n-color":t.embedded?T.colorEmbedded:T.color,"--n-text-color":T.textColor}}),k=l?ae("layout",C(()=>t.embedded?"e":""),A,t):void 0;return Object.assign({mergedClsPrefix:a,scrollableElRef:o,scrollbarInstRef:i,hasSiderStyle:_,mergedTheme:s,handleNativeElScroll:H,cssVars:l?void 0:A,themeClass:k==null?void 0:k.themeClass,onRender:k==null?void 0:k.onRender},g)},render(){var t;const{mergedClsPrefix:o,hasSider:i}=this;(t=this.onRender)===null||t===void 0||t.call(this);const a=i?this.hasSiderStyle:void 0,l=[this.themeClass,e&&`${o}-layout-content`,`${o}-layout`,`${o}-layout--${this.position}-positioned`];return u("div",{class:l,style:this.cssVars},this.nativeScrollbar?u("div",{ref:"scrollableElRef",class:[`${o}-layout-scroll-container`,this.contentClass],style:[this.contentStyle,a],onScroll:this.handleNativeElScroll},this.$slots):u(_e,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,contentClass:this.contentClass,contentStyle:[this.contentStyle,a]}),this.$slots))}})}const ke=Me(!1),Eo=Me(!0),Lo=v("layout-header",`
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
 `)]),Oo={position:Ce,inverted:Boolean,bordered:{type:Boolean,default:!1}},Mo=L({name:"LayoutHeader",props:Object.assign(Object.assign({},D.props),Oo),setup(e){const{mergedClsPrefixRef:t,inlineThemeDisabled:o}=ie(e),i=D("Layout","-layout-header",Lo,xe,e,t),a=C(()=>{const{common:{cubicBezierEaseInOut:s},self:h}=i.value,c={"--n-bezier":s};return e.inverted?(c["--n-color"]=h.headerColorInverted,c["--n-text-color"]=h.textColorInverted,c["--n-border-color"]=h.headerBorderColorInverted):(c["--n-color"]=h.headerColor,c["--n-text-color"]=h.textColor,c["--n-border-color"]=h.headerBorderColor),c}),l=o?ae("layout-header",C(()=>e.inverted?"a":"b"),a,e):void 0;return{mergedClsPrefix:t,cssVars:o?void 0:a,themeClass:l==null?void 0:l.themeClass,onRender:l==null?void 0:l.onRender}},render(){var e;const{mergedClsPrefix:t}=this;return(e=this.onRender)===null||e===void 0||e.call(this),u("div",{class:[`${t}-layout-header`,this.themeClass,this.position&&`${t}-layout-header--${this.position}-positioned`,this.bordered&&`${t}-layout-header--bordered`],style:this.cssVars},this.$slots)}}),$o=v("layout-sider",`
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
`,[R("bordered",[d("border",`
 content: "";
 position: absolute;
 top: 0;
 bottom: 0;
 width: 1px;
 background-color: var(--n-border-color);
 transition: background-color .3s var(--n-bezier);
 `)]),d("left-placement",[R("bordered",[d("border",`
 right: 0;
 `)])]),R("right-placement",`
 justify-content: flex-start;
 `,[R("bordered",[d("border",`
 left: 0;
 `)]),R("collapsed",[v("layout-toggle-button",[v("base-icon",`
 transform: rotate(180deg);
 `)]),v("layout-toggle-bar",[w("&:hover",[d("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])])]),v("layout-toggle-button",`
 left: 0;
 transform: translateX(-50%) translateY(-50%);
 `,[v("base-icon",`
 transform: rotate(0);
 `)]),v("layout-toggle-bar",`
 left: -28px;
 transform: rotate(180deg);
 `,[w("&:hover",[d("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})])])]),R("collapsed",[v("layout-toggle-bar",[w("&:hover",[d("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])]),v("layout-toggle-button",[v("base-icon",`
 transform: rotate(0);
 `)])]),v("layout-toggle-button",`
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
 `,[v("base-icon",`
 transition: transform .3s var(--n-bezier);
 transform: rotate(180deg);
 `)]),v("layout-toggle-bar",`
 cursor: pointer;
 height: 72px;
 width: 32px;
 position: absolute;
 top: calc(50% - 36px);
 right: -28px;
 `,[d("top, bottom",`
 position: absolute;
 width: 4px;
 border-radius: 2px;
 height: 38px;
 left: 14px;
 transition: 
 background-color .3s var(--n-bezier),
 transform .3s var(--n-bezier);
 `),d("bottom",`
 position: absolute;
 top: 34px;
 `),w("&:hover",[d("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})]),d("top, bottom",{backgroundColor:"var(--n-toggle-bar-color)"}),w("&:hover",[d("top, bottom",{backgroundColor:"var(--n-toggle-bar-color-hover)"})])]),d("border",`
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 width: 1px;
 transition: background-color .3s var(--n-bezier);
 `),v("layout-sider-scroll-container",`
 flex-grow: 1;
 flex-shrink: 0;
 box-sizing: border-box;
 height: 100%;
 opacity: 0;
 transition: opacity .3s var(--n-bezier);
 max-width: 100%;
 `),R("show-content",[v("layout-sider-scroll-container",{opacity:1})]),R("absolute-positioned",`
 position: absolute;
 left: 0;
 top: 0;
 bottom: 0;
 `)]),Fo=L({props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return u("div",{onClick:this.onClick,class:`${e}-layout-toggle-bar`},u("div",{class:`${e}-layout-toggle-bar__top`}),u("div",{class:`${e}-layout-toggle-bar__bottom`}))}}),jo=L({name:"LayoutToggleButton",props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return u("div",{class:`${e}-layout-toggle-button`,onClick:this.onClick},u(Be,{clsPrefix:e},{default:()=>u(Ro,null)}))}}),Ko={position:Ce,bordered:Boolean,collapsedWidth:{type:Number,default:48},width:{type:[Number,String],default:272},contentClass:String,contentStyle:{type:[String,Object],default:""},collapseMode:{type:String,default:"transform"},collapsed:{type:Boolean,default:void 0},defaultCollapsed:Boolean,showCollapsedContent:{type:Boolean,default:!0},showTrigger:{type:[Boolean,String],default:!1},nativeScrollbar:{type:Boolean,default:!0},inverted:Boolean,scrollbarProps:Object,triggerClass:String,triggerStyle:[String,Object],collapsedTriggerClass:String,collapsedTriggerStyle:[String,Object],"onUpdate:collapsed":[Function,Array],onUpdateCollapsed:[Function,Array],onAfterEnter:Function,onAfterLeave:Function,onExpand:[Function,Array],onCollapse:[Function,Array],onScroll:Function},Vo=L({name:"LayoutSider",props:Object.assign(Object.assign({},D.props),Ko),setup(e){const t=V(Oe),o=B(null),i=B(null),a=B(e.defaultCollapsed),l=fe(le(e,"collapsed"),a),s=C(()=>me(l.value?e.collapsedWidth:e.width)),h=C(()=>e.collapseMode!=="transform"?{}:{minWidth:me(e.width)}),c=C(()=>t?t.siderPlacement:"left");function p(f,y){if(e.nativeScrollbar){const{value:b}=o;b&&(y===void 0?b.scrollTo(f):b.scrollTo(f,y))}else{const{value:b}=i;b&&b.scrollTo(f,y)}}function H(){const{"onUpdate:collapsed":f,onUpdateCollapsed:y,onExpand:b,onCollapse:O}=e,{value:j}=l;y&&M(y,!j),f&&M(f,!j),a.value=!j,j?b&&M(b):O&&M(O)}let _=0,g=0;const A=f=>{var y;const b=f.target;_=b.scrollLeft,g=b.scrollTop,(y=e.onScroll)===null||y===void 0||y.call(e,f)};He(()=>{if(e.nativeScrollbar){const f=o.value;f&&(f.scrollTop=g,f.scrollLeft=_)}}),J(Le,{collapsedRef:l,collapseModeRef:le(e,"collapseMode")});const{mergedClsPrefixRef:k,inlineThemeDisabled:S}=ie(e),T=D("Layout","-layout-sider",$o,xe,e,k);function N(f){var y,b;f.propertyName==="max-width"&&(l.value?(y=e.onAfterLeave)===null||y===void 0||y.call(e):(b=e.onAfterEnter)===null||b===void 0||b.call(e))}const U={scrollTo:p},$=C(()=>{const{common:{cubicBezierEaseInOut:f},self:y}=T.value,{siderToggleButtonColor:b,siderToggleButtonBorder:O,siderToggleBarColor:j,siderToggleBarColorHover:ce}=y,E={"--n-bezier":f,"--n-toggle-button-color":b,"--n-toggle-button-border":O,"--n-toggle-bar-color":j,"--n-toggle-bar-color-hover":ce};return e.inverted?(E["--n-color"]=y.siderColorInverted,E["--n-text-color"]=y.textColorInverted,E["--n-border-color"]=y.siderBorderColorInverted,E["--n-toggle-button-icon-color"]=y.siderToggleButtonIconColorInverted,E.__invertScrollbar=y.__invertScrollbar):(E["--n-color"]=y.siderColor,E["--n-text-color"]=y.textColor,E["--n-border-color"]=y.siderBorderColor,E["--n-toggle-button-icon-color"]=y.siderToggleButtonIconColor),E}),I=S?ae("layout-sider",C(()=>e.inverted?"a":"b"),$,e):void 0;return Object.assign({scrollableElRef:o,scrollbarInstRef:i,mergedClsPrefix:k,mergedTheme:T,styleMaxWidth:s,mergedCollapsed:l,scrollContainerStyle:h,siderPlacement:c,handleNativeElScroll:A,handleTransitionend:N,handleTriggerClick:H,inlineThemeDisabled:S,cssVars:$,themeClass:I==null?void 0:I.themeClass,onRender:I==null?void 0:I.onRender},U)},render(){var e;const{mergedClsPrefix:t,mergedCollapsed:o,showTrigger:i}=this;return(e=this.onRender)===null||e===void 0||e.call(this),u("aside",{class:[`${t}-layout-sider`,this.themeClass,`${t}-layout-sider--${this.position}-positioned`,`${t}-layout-sider--${this.siderPlacement}-placement`,this.bordered&&`${t}-layout-sider--bordered`,o&&`${t}-layout-sider--collapsed`,(!o||this.showCollapsedContent)&&`${t}-layout-sider--show-content`],onTransitionend:this.handleTransitionend,style:[this.inlineThemeDisabled?void 0:this.cssVars,{maxWidth:this.styleMaxWidth,width:me(this.width)}]},this.nativeScrollbar?u("div",{class:[`${t}-layout-sider-scroll-container`,this.contentClass],onScroll:this.handleNativeElScroll,style:[this.scrollContainerStyle,{overflow:"auto"},this.contentStyle],ref:"scrollableElRef"},this.$slots):u(_e,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",style:this.scrollContainerStyle,contentStyle:this.contentStyle,contentClass:this.contentClass,theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,builtinThemeOverrides:this.inverted&&this.cssVars.__invertScrollbar==="true"?{colorHover:"rgba(255, 255, 255, .4)",color:"rgba(255, 255, 255, .3)"}:void 0}),this.$slots),i?i==="bar"?u(Fo,{clsPrefix:t,class:o?this.collapsedTriggerClass:this.triggerClass,style:o?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):u(jo,{clsPrefix:t,class:o?this.collapsedTriggerClass:this.triggerClass,style:o?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):null,this.bordered?u("div",{class:`${t}-layout-sider__border`}):null)}}),ee=Z("n-menu"),$e=Z("n-submenu"),ye=Z("n-menu-item-group"),Ne=[w("&::before","background-color: var(--n-item-color-hover);"),d("arrow",`
 color: var(--n-arrow-color-hover);
 `),d("icon",`
 color: var(--n-item-icon-color-hover);
 `),v("menu-item-content-header",`
 color: var(--n-item-text-color-hover);
 `,[w("a",`
 color: var(--n-item-text-color-hover);
 `),d("extra",`
 color: var(--n-item-text-color-hover);
 `)])],Ae=[d("icon",`
 color: var(--n-item-icon-color-hover-horizontal);
 `),v("menu-item-content-header",`
 color: var(--n-item-text-color-hover-horizontal);
 `,[w("a",`
 color: var(--n-item-text-color-hover-horizontal);
 `),d("extra",`
 color: var(--n-item-text-color-hover-horizontal);
 `)])],Do=w([v("menu",`
 background-color: var(--n-color);
 color: var(--n-item-text-color);
 overflow: hidden;
 transition: background-color .3s var(--n-bezier);
 box-sizing: border-box;
 font-size: var(--n-font-size);
 padding-bottom: 6px;
 `,[R("horizontal",`
 max-width: 100%;
 width: 100%;
 display: flex;
 overflow: hidden;
 padding-bottom: 0;
 `,[v("submenu","margin: 0;"),v("menu-item","margin: 0;"),v("menu-item-content",`
 padding: 0 20px;
 border-bottom: 2px solid #0000;
 `,[w("&::before","display: none;"),R("selected","border-bottom: 2px solid var(--n-border-color-horizontal)")]),v("menu-item-content",[R("selected",[d("icon","color: var(--n-item-icon-color-active-horizontal);"),v("menu-item-content-header",`
 color: var(--n-item-text-color-active-horizontal);
 `,[w("a","color: var(--n-item-text-color-active-horizontal);"),d("extra","color: var(--n-item-text-color-active-horizontal);")])]),R("child-active",`
 border-bottom: 2px solid var(--n-border-color-horizontal);
 `,[v("menu-item-content-header",`
 color: var(--n-item-text-color-child-active-horizontal);
 `,[w("a",`
 color: var(--n-item-text-color-child-active-horizontal);
 `),d("extra",`
 color: var(--n-item-text-color-child-active-horizontal);
 `)]),d("icon",`
 color: var(--n-item-icon-color-child-active-horizontal);
 `)]),Q("disabled",[Q("selected, child-active",[w("&:focus-within",Ae)]),R("selected",[q(null,[d("icon","color: var(--n-item-icon-color-active-hover-horizontal);"),v("menu-item-content-header",`
 color: var(--n-item-text-color-active-hover-horizontal);
 `,[w("a","color: var(--n-item-text-color-active-hover-horizontal);"),d("extra","color: var(--n-item-text-color-active-hover-horizontal);")])])]),R("child-active",[q(null,[d("icon","color: var(--n-item-icon-color-child-active-hover-horizontal);"),v("menu-item-content-header",`
 color: var(--n-item-text-color-child-active-hover-horizontal);
 `,[w("a","color: var(--n-item-text-color-child-active-hover-horizontal);"),d("extra","color: var(--n-item-text-color-child-active-hover-horizontal);")])])]),q("border-bottom: 2px solid var(--n-border-color-horizontal);",Ae)]),v("menu-item-content-header",[w("a","color: var(--n-item-text-color-horizontal);")])])]),Q("responsive",[v("menu-item-content-header",`
 overflow: hidden;
 text-overflow: ellipsis;
 `)]),R("collapsed",[v("menu-item-content",[R("selected",[w("&::before",`
 background-color: var(--n-item-color-active-collapsed) !important;
 `)]),v("menu-item-content-header","opacity: 0;"),d("arrow","opacity: 0;"),d("icon","color: var(--n-item-icon-color-collapsed);")])]),v("menu-item",`
 height: var(--n-item-height);
 margin-top: 6px;
 position: relative;
 `),v("menu-item-content",`
 box-sizing: border-box;
 line-height: 1.75;
 height: 100%;
 display: grid;
 grid-template-areas: "icon content arrow";
 grid-template-columns: auto 1fr auto;
 align-items: center;
 cursor: pointer;
 position: relative;
 padding-right: 18px;
 transition:
 background-color .3s var(--n-bezier),
 padding-left .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `,[w("> *","z-index: 1;"),w("&::before",`
 z-index: auto;
 content: "";
 background-color: #0000;
 position: absolute;
 left: 8px;
 right: 8px;
 top: 0;
 bottom: 0;
 pointer-events: none;
 border-radius: var(--n-border-radius);
 transition: background-color .3s var(--n-bezier);
 `),R("disabled",`
 opacity: .45;
 cursor: not-allowed;
 `),R("collapsed",[d("arrow","transform: rotate(0);")]),R("selected",[w("&::before","background-color: var(--n-item-color-active);"),d("arrow","color: var(--n-arrow-color-active);"),d("icon","color: var(--n-item-icon-color-active);"),v("menu-item-content-header",`
 color: var(--n-item-text-color-active);
 `,[w("a","color: var(--n-item-text-color-active);"),d("extra","color: var(--n-item-text-color-active);")])]),R("child-active",[v("menu-item-content-header",`
 color: var(--n-item-text-color-child-active);
 `,[w("a",`
 color: var(--n-item-text-color-child-active);
 `),d("extra",`
 color: var(--n-item-text-color-child-active);
 `)]),d("arrow",`
 color: var(--n-arrow-color-child-active);
 `),d("icon",`
 color: var(--n-item-icon-color-child-active);
 `)]),Q("disabled",[Q("selected, child-active",[w("&:focus-within",Ne)]),R("selected",[q(null,[d("arrow","color: var(--n-arrow-color-active-hover);"),d("icon","color: var(--n-item-icon-color-active-hover);"),v("menu-item-content-header",`
 color: var(--n-item-text-color-active-hover);
 `,[w("a","color: var(--n-item-text-color-active-hover);"),d("extra","color: var(--n-item-text-color-active-hover);")])])]),R("child-active",[q(null,[d("arrow","color: var(--n-arrow-color-child-active-hover);"),d("icon","color: var(--n-item-icon-color-child-active-hover);"),v("menu-item-content-header",`
 color: var(--n-item-text-color-child-active-hover);
 `,[w("a","color: var(--n-item-text-color-child-active-hover);"),d("extra","color: var(--n-item-text-color-child-active-hover);")])])]),R("selected",[q(null,[w("&::before","background-color: var(--n-item-color-active-hover);")])]),q(null,Ne)]),d("icon",`
 grid-area: icon;
 color: var(--n-item-icon-color);
 transition:
 color .3s var(--n-bezier),
 font-size .3s var(--n-bezier),
 margin-right .3s var(--n-bezier);
 box-sizing: content-box;
 display: inline-flex;
 align-items: center;
 justify-content: center;
 `),d("arrow",`
 grid-area: arrow;
 font-size: 16px;
 color: var(--n-arrow-color);
 transform: rotate(180deg);
 opacity: 1;
 transition:
 color .3s var(--n-bezier),
 transform 0.2s var(--n-bezier),
 opacity 0.2s var(--n-bezier);
 `),v("menu-item-content-header",`
 grid-area: content;
 transition:
 color .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 opacity: 1;
 white-space: nowrap;
 color: var(--n-item-text-color);
 `,[w("a",`
 outline: none;
 text-decoration: none;
 transition: color .3s var(--n-bezier);
 color: var(--n-item-text-color);
 `,[w("&::before",`
 content: "";
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),d("extra",`
 font-size: .93em;
 color: var(--n-group-text-color);
 transition: color .3s var(--n-bezier);
 `)])]),v("submenu",`
 cursor: pointer;
 position: relative;
 margin-top: 6px;
 `,[v("menu-item-content",`
 height: var(--n-item-height);
 `),v("submenu-children",`
 overflow: hidden;
 padding: 0;
 `,[ro({duration:".2s"})])]),v("menu-item-group",[v("menu-item-group-title",`
 margin-top: 6px;
 color: var(--n-group-text-color);
 cursor: default;
 font-size: .93em;
 height: 36px;
 display: flex;
 align-items: center;
 transition:
 padding-left .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `)])]),v("menu-tooltip",[w("a",`
 color: inherit;
 text-decoration: none;
 `)]),v("menu-divider",`
 transition: background-color .3s var(--n-bezier);
 background-color: var(--n-divider-color);
 height: 1px;
 margin: 6px 18px;
 `)]);function q(e,t){return[R("hover",e,t),w("&:hover",e,t)]}const Fe=L({name:"MenuOptionContent",props:{collapsed:Boolean,disabled:Boolean,title:[String,Function],icon:Function,extra:[String,Function],showArrow:Boolean,childActive:Boolean,hover:Boolean,paddingLeft:Number,selected:Boolean,maxIconSize:{type:Number,required:!0},activeIconSize:{type:Number,required:!0},iconMarginRight:{type:Number,required:!0},clsPrefix:{type:String,required:!0},onClick:Function,tmNode:{type:Object,required:!0},isEllipsisPlaceholder:Boolean},setup(e){const{props:t}=V(ee);return{menuProps:t,style:C(()=>{const{paddingLeft:o}=e;return{paddingLeft:o&&`${o}px`}}),iconStyle:C(()=>{const{maxIconSize:o,activeIconSize:i,iconMarginRight:a}=e;return{width:`${o}px`,height:`${o}px`,fontSize:`${i}px`,marginRight:`${a}px`}})}},render(){const{clsPrefix:e,tmNode:t,menuProps:{renderIcon:o,renderLabel:i,renderExtra:a,expandIcon:l}}=this,s=o?o(t.rawNode):X(this.icon);return u("div",{onClick:h=>{var c;(c=this.onClick)===null||c===void 0||c.call(this,h)},role:"none",class:[`${e}-menu-item-content`,{[`${e}-menu-item-content--selected`]:this.selected,[`${e}-menu-item-content--collapsed`]:this.collapsed,[`${e}-menu-item-content--child-active`]:this.childActive,[`${e}-menu-item-content--disabled`]:this.disabled,[`${e}-menu-item-content--hover`]:this.hover}],style:this.style},s&&u("div",{class:`${e}-menu-item-content__icon`,style:this.iconStyle,role:"none"},[s]),u("div",{class:`${e}-menu-item-content-header`,role:"none"},this.isEllipsisPlaceholder?this.title:i?i(t.rawNode):X(this.title),this.extra||a?u("span",{class:`${e}-menu-item-content-header__extra`}," ",a?a(t.rawNode):X(this.extra)):null),this.showArrow?u(Be,{ariaHidden:!0,class:`${e}-menu-item-content__arrow`,clsPrefix:e},{default:()=>l?l(t.rawNode):u(Ao,null)}):null)}}),re=8;function ze(e){const t=V(ee),{props:o,mergedCollapsedRef:i}=t,a=V($e,null),l=V(ye,null),s=C(()=>o.mode==="horizontal"),h=C(()=>s.value?o.dropdownPlacement:"tmNodes"in e?"right-start":"right"),c=C(()=>{var g;return Math.max((g=o.collapsedIconSize)!==null&&g!==void 0?g:o.iconSize,o.iconSize)}),p=C(()=>{var g;return!s.value&&e.root&&i.value&&(g=o.collapsedIconSize)!==null&&g!==void 0?g:o.iconSize}),H=C(()=>{if(s.value)return;const{collapsedWidth:g,indent:A,rootIndent:k}=o,{root:S,isGroup:T}=e,N=k===void 0?A:k;return S?i.value?g/2-c.value/2:N:l&&typeof l.paddingLeftRef.value=="number"?A/2+l.paddingLeftRef.value:a&&typeof a.paddingLeftRef.value=="number"?(T?A/2:A)+a.paddingLeftRef.value:0}),_=C(()=>{const{collapsedWidth:g,indent:A,rootIndent:k}=o,{value:S}=c,{root:T}=e;return s.value||!T||!i.value?re:(k===void 0?A:k)+S+re-(g+S)/2});return{dropdownPlacement:h,activeIconSize:p,maxIconSize:c,paddingLeft:H,iconMarginRight:_,NMenu:t,NSubmenu:a,NMenuOptionGroup:l}}const Ie={internalKey:{type:[String,Number],required:!0},root:Boolean,isGroup:Boolean,level:{type:Number,required:!0},title:[String,Function],extra:[String,Function]},Uo=L({name:"MenuDivider",setup(){const e=V(ee),{mergedClsPrefixRef:t,isHorizontalRef:o}=e;return()=>o.value?null:u("div",{class:`${t.value}-menu-divider`})}}),je=Object.assign(Object.assign({},Ie),{tmNode:{type:Object,required:!0},disabled:Boolean,icon:Function,onClick:Function}),Go=pe(je),Wo=L({name:"MenuOption",props:je,setup(e){const t=ze(e),{NSubmenu:o,NMenu:i,NMenuOptionGroup:a}=t,{props:l,mergedClsPrefixRef:s,mergedCollapsedRef:h}=i,c=o?o.mergedDisabledRef:a?a.mergedDisabledRef:{value:!1},p=C(()=>c.value||e.disabled);function H(g){const{onClick:A}=e;A&&A(g)}function _(g){p.value||(i.doSelect(e.internalKey,e.tmNode.rawNode),H(g))}return{mergedClsPrefix:s,dropdownPlacement:t.dropdownPlacement,paddingLeft:t.paddingLeft,iconMarginRight:t.iconMarginRight,maxIconSize:t.maxIconSize,activeIconSize:t.activeIconSize,mergedTheme:i.mergedThemeRef,menuProps:l,dropdownEnabled:he(()=>e.root&&h.value&&l.mode!=="horizontal"&&!p.value),selected:he(()=>i.mergedValueRef.value===e.internalKey),mergedDisabled:p,handleClick:_}},render(){const{mergedClsPrefix:e,mergedTheme:t,tmNode:o,menuProps:{renderLabel:i,nodeProps:a}}=this,l=a==null?void 0:a(o.rawNode);return u("div",Object.assign({},l,{role:"menuitem",class:[`${e}-menu-item`,l==null?void 0:l.class]}),u(To,{theme:t.peers.Tooltip,themeOverrides:t.peerOverrides.Tooltip,trigger:"hover",placement:this.dropdownPlacement,disabled:!this.dropdownEnabled||this.title===void 0,internalExtraClass:["menu-tooltip"]},{default:()=>i?i(o.rawNode):X(this.title),trigger:()=>u(Fe,{tmNode:o,clsPrefix:e,paddingLeft:this.paddingLeft,iconMarginRight:this.iconMarginRight,maxIconSize:this.maxIconSize,activeIconSize:this.activeIconSize,selected:this.selected,title:this.title,extra:this.extra,disabled:this.mergedDisabled,icon:this.icon,onClick:this.handleClick})}))}}),Ke=Object.assign(Object.assign({},Ie),{tmNode:{type:Object,required:!0},tmNodes:{type:Array,required:!0}}),qo=pe(Ke),Yo=L({name:"MenuOptionGroup",props:Ke,setup(e){const t=ze(e),{NSubmenu:o}=t,i=C(()=>o!=null&&o.mergedDisabledRef.value?!0:e.tmNode.disabled);J(ye,{paddingLeftRef:t.paddingLeft,mergedDisabledRef:i});const{mergedClsPrefixRef:a,props:l}=V(ee);return function(){const{value:s}=a,h=t.paddingLeft.value,{nodeProps:c}=l,p=c==null?void 0:c(e.tmNode.rawNode);return u("div",{class:`${s}-menu-item-group`,role:"group"},u("div",Object.assign({},p,{class:[`${s}-menu-item-group-title`,p==null?void 0:p.class],style:[(p==null?void 0:p.style)||"",h!==void 0?`padding-left: ${h}px;`:""]}),X(e.title),e.extra?u(Ee,null," ",X(e.extra)):null),u("div",null,e.tmNodes.map(H=>we(H,l))))}}});function ge(e){return e.type==="divider"||e.type==="render"}function Xo(e){return e.type==="divider"}function we(e,t){const{rawNode:o}=e,{show:i}=o;if(i===!1)return null;if(ge(o))return Xo(o)?u(Uo,Object.assign({key:e.key},o.props)):null;const{labelField:a}=t,{key:l,level:s,isGroup:h}=e,c=Object.assign(Object.assign({},o),{title:o.title||o[a],extra:o.titleExtra||o.extra,key:l,internalKey:l,level:s,root:s===0,isGroup:h});return e.children?e.isGroup?u(Yo,se(c,qo,{tmNode:e,tmNodes:e.children,key:l})):u(be,se(c,Jo,{key:l,rawNodes:o[t.childrenField],tmNodes:e.children,tmNode:e})):u(Wo,se(c,Go,{key:l,tmNode:e}))}const Ve=Object.assign(Object.assign({},Ie),{rawNodes:{type:Array,default:()=>[]},tmNodes:{type:Array,default:()=>[]},tmNode:{type:Object,required:!0},disabled:Boolean,icon:Function,onClick:Function,domId:String,virtualChildActive:{type:Boolean,default:void 0},isEllipsisPlaceholder:Boolean}),Jo=pe(Ve),be=L({name:"Submenu",props:Ve,setup(e){const t=ze(e),{NMenu:o,NSubmenu:i}=t,{props:a,mergedCollapsedRef:l,mergedThemeRef:s}=o,h=C(()=>{const{disabled:g}=e;return i!=null&&i.mergedDisabledRef.value||a.disabled?!0:g}),c=B(!1);J($e,{paddingLeftRef:t.paddingLeft,mergedDisabledRef:h}),J(ye,null);function p(){const{onClick:g}=e;g&&g()}function H(){h.value||(l.value||o.toggleExpand(e.internalKey),p())}function _(g){c.value=g}return{menuProps:a,mergedTheme:s,doSelect:o.doSelect,inverted:o.invertedRef,isHorizontal:o.isHorizontalRef,mergedClsPrefix:o.mergedClsPrefixRef,maxIconSize:t.maxIconSize,activeIconSize:t.activeIconSize,iconMarginRight:t.iconMarginRight,dropdownPlacement:t.dropdownPlacement,dropdownShow:c,paddingLeft:t.paddingLeft,mergedDisabled:h,mergedValue:o.mergedValueRef,childActive:he(()=>{var g;return(g=e.virtualChildActive)!==null&&g!==void 0?g:o.activePathRef.value.includes(e.internalKey)}),collapsed:C(()=>a.mode==="horizontal"?!1:l.value?!0:!o.mergedExpandedKeysRef.value.includes(e.internalKey)),dropdownEnabled:C(()=>!h.value&&(a.mode==="horizontal"||l.value)),handlePopoverShowChange:_,handleClick:H}},render(){var e;const{mergedClsPrefix:t,menuProps:{renderIcon:o,renderLabel:i}}=this,a=()=>{const{isHorizontal:s,paddingLeft:h,collapsed:c,mergedDisabled:p,maxIconSize:H,activeIconSize:_,title:g,childActive:A,icon:k,handleClick:S,menuProps:{nodeProps:T},dropdownShow:N,iconMarginRight:U,tmNode:$,mergedClsPrefix:I,isEllipsisPlaceholder:f,extra:y}=this,b=T==null?void 0:T($.rawNode);return u("div",Object.assign({},b,{class:[`${I}-menu-item`,b==null?void 0:b.class],role:"menuitem"}),u(Fe,{tmNode:$,paddingLeft:h,collapsed:c,disabled:p,iconMarginRight:U,maxIconSize:H,activeIconSize:_,title:g,extra:y,showArrow:!s,childActive:A,clsPrefix:I,icon:k,hover:N,onClick:S,isEllipsisPlaceholder:f}))},l=()=>u(no,null,{default:()=>{const{tmNodes:s,collapsed:h}=this;return h?null:u("div",{class:`${t}-submenu-children`,role:"menu"},s.map(c=>we(c,this.menuProps)))}});return this.root?u(Po,Object.assign({size:"large",trigger:"hover"},(e=this.menuProps)===null||e===void 0?void 0:e.dropdownProps,{themeOverrides:this.mergedTheme.peerOverrides.Dropdown,theme:this.mergedTheme.peers.Dropdown,builtinThemeOverrides:{fontSizeLarge:"14px",optionIconSizeLarge:"18px"},value:this.mergedValue,disabled:!this.dropdownEnabled,placement:this.dropdownPlacement,keyField:this.menuProps.keyField,labelField:this.menuProps.labelField,childrenField:this.menuProps.childrenField,onUpdateShow:this.handlePopoverShowChange,options:this.rawNodes,onSelect:this.doSelect,inverted:this.inverted,renderIcon:o,renderLabel:i}),{default:()=>u("div",{class:`${t}-submenu`,role:"menu","aria-expanded":!this.collapsed,id:this.domId},a(),this.isHorizontal?null:l())}):u("div",{class:`${t}-submenu`,role:"menu","aria-expanded":!this.collapsed,id:this.domId},a(),l())}}),Qo=Object.assign(Object.assign({},D.props),{options:{type:Array,default:()=>[]},collapsed:{type:Boolean,default:void 0},collapsedWidth:{type:Number,default:48},iconSize:{type:Number,default:20},collapsedIconSize:{type:Number,default:24},rootIndent:Number,indent:{type:Number,default:32},labelField:{type:String,default:"label"},keyField:{type:String,default:"key"},childrenField:{type:String,default:"children"},disabledField:{type:String,default:"disabled"},defaultExpandAll:Boolean,defaultExpandedKeys:Array,expandedKeys:Array,value:[String,Number],defaultValue:{type:[String,Number],default:null},mode:{type:String,default:"vertical"},watchProps:{type:Array,default:void 0},disabled:Boolean,show:{type:Boolean,default:!0},inverted:Boolean,"onUpdate:expandedKeys":[Function,Array],onUpdateExpandedKeys:[Function,Array],onUpdateValue:[Function,Array],"onUpdate:value":[Function,Array],expandIcon:Function,renderIcon:Function,renderLabel:Function,renderExtra:Function,dropdownProps:Object,accordion:Boolean,nodeProps:Function,dropdownPlacement:{type:String,default:"bottom"},responsive:Boolean,items:Array,onOpenNamesChange:[Function,Array],onSelect:[Function,Array],onExpandedNamesChange:[Function,Array],expandedNames:Array,defaultExpandedNames:Array}),Zo=L({name:"Menu",inheritAttrs:!1,props:Qo,setup(e){const{mergedClsPrefixRef:t,inlineThemeDisabled:o}=ie(e),i=D("Menu","-menu",Do,ko,e,t),a=V(Le,null),l=C(()=>{var m;const{collapsed:z}=e;if(z!==void 0)return z;if(a){const{collapseModeRef:r,collapsedRef:x}=a;if(r.value==="width")return(m=x.value)!==null&&m!==void 0?m:!1}return!1}),s=C(()=>{const{keyField:m,childrenField:z,disabledField:r}=e;return ve(e.items||e.options,{getIgnored(x){return ge(x)},getChildren(x){return x[z]},getDisabled(x){return x[r]},getKey(x){var P;return(P=x[m])!==null&&P!==void 0?P:x.name}})}),h=C(()=>new Set(s.value.treeNodes.map(m=>m.key))),{watchProps:c}=e,p=B(null);c!=null&&c.includes("defaultValue")?Te(()=>{p.value=e.defaultValue}):p.value=e.defaultValue;const H=le(e,"value"),_=fe(H,p),g=B([]),A=()=>{g.value=e.defaultExpandAll?s.value.getNonLeafKeys():e.defaultExpandedNames||e.defaultExpandedKeys||s.value.getPath(_.value,{includeSelf:!1}).keyPath};c!=null&&c.includes("defaultExpandedKeys")?Te(A):A();const k=wo(e,["expandedNames","expandedKeys"]),S=fe(k,g),T=C(()=>s.value.treeNodes),N=C(()=>s.value.getPath(_.value).keyPath);J(ee,{props:e,mergedCollapsedRef:l,mergedThemeRef:i,mergedValueRef:_,mergedExpandedKeysRef:S,activePathRef:N,mergedClsPrefixRef:t,isHorizontalRef:C(()=>e.mode==="horizontal"),invertedRef:le(e,"inverted"),doSelect:U,toggleExpand:I});function U(m,z){const{"onUpdate:value":r,onUpdateValue:x,onSelect:P}=e;x&&M(x,m,z),r&&M(r,m,z),P&&M(P,m,z),p.value=m}function $(m){const{"onUpdate:expandedKeys":z,onUpdateExpandedKeys:r,onExpandedNamesChange:x,onOpenNamesChange:P}=e;z&&M(z,m),r&&M(r,m),x&&M(x,m),P&&M(P,m),g.value=m}function I(m){const z=Array.from(S.value),r=z.findIndex(x=>x===m);if(~r)z.splice(r,1);else{if(e.accordion&&h.value.has(m)){const x=z.findIndex(P=>h.value.has(P));x>-1&&z.splice(x,1)}z.push(m)}$(z)}const f=m=>{const z=s.value.getPath(m??_.value,{includeSelf:!1}).keyPath;if(!z.length)return;const r=Array.from(S.value),x=new Set([...r,...z]);e.accordion&&h.value.forEach(P=>{x.has(P)&&!z.includes(P)&&x.delete(P)}),$(Array.from(x))},y=C(()=>{const{inverted:m}=e,{common:{cubicBezierEaseInOut:z},self:r}=i.value,{borderRadius:x,borderColorHorizontal:P,fontSize:Je,itemHeight:Qe,dividerColor:Ze}=r,n={"--n-divider-color":Ze,"--n-bezier":z,"--n-font-size":Je,"--n-border-color-horizontal":P,"--n-border-radius":x,"--n-item-height":Qe};return m?(n["--n-group-text-color"]=r.groupTextColorInverted,n["--n-color"]=r.colorInverted,n["--n-item-text-color"]=r.itemTextColorInverted,n["--n-item-text-color-hover"]=r.itemTextColorHoverInverted,n["--n-item-text-color-active"]=r.itemTextColorActiveInverted,n["--n-item-text-color-child-active"]=r.itemTextColorChildActiveInverted,n["--n-item-text-color-child-active-hover"]=r.itemTextColorChildActiveInverted,n["--n-item-text-color-active-hover"]=r.itemTextColorActiveHoverInverted,n["--n-item-icon-color"]=r.itemIconColorInverted,n["--n-item-icon-color-hover"]=r.itemIconColorHoverInverted,n["--n-item-icon-color-active"]=r.itemIconColorActiveInverted,n["--n-item-icon-color-active-hover"]=r.itemIconColorActiveHoverInverted,n["--n-item-icon-color-child-active"]=r.itemIconColorChildActiveInverted,n["--n-item-icon-color-child-active-hover"]=r.itemIconColorChildActiveHoverInverted,n["--n-item-icon-color-collapsed"]=r.itemIconColorCollapsedInverted,n["--n-item-text-color-horizontal"]=r.itemTextColorHorizontalInverted,n["--n-item-text-color-hover-horizontal"]=r.itemTextColorHoverHorizontalInverted,n["--n-item-text-color-active-horizontal"]=r.itemTextColorActiveHorizontalInverted,n["--n-item-text-color-child-active-horizontal"]=r.itemTextColorChildActiveHorizontalInverted,n["--n-item-text-color-child-active-hover-horizontal"]=r.itemTextColorChildActiveHoverHorizontalInverted,n["--n-item-text-color-active-hover-horizontal"]=r.itemTextColorActiveHoverHorizontalInverted,n["--n-item-icon-color-horizontal"]=r.itemIconColorHorizontalInverted,n["--n-item-icon-color-hover-horizontal"]=r.itemIconColorHoverHorizontalInverted,n["--n-item-icon-color-active-horizontal"]=r.itemIconColorActiveHorizontalInverted,n["--n-item-icon-color-active-hover-horizontal"]=r.itemIconColorActiveHoverHorizontalInverted,n["--n-item-icon-color-child-active-horizontal"]=r.itemIconColorChildActiveHorizontalInverted,n["--n-item-icon-color-child-active-hover-horizontal"]=r.itemIconColorChildActiveHoverHorizontalInverted,n["--n-arrow-color"]=r.arrowColorInverted,n["--n-arrow-color-hover"]=r.arrowColorHoverInverted,n["--n-arrow-color-active"]=r.arrowColorActiveInverted,n["--n-arrow-color-active-hover"]=r.arrowColorActiveHoverInverted,n["--n-arrow-color-child-active"]=r.arrowColorChildActiveInverted,n["--n-arrow-color-child-active-hover"]=r.arrowColorChildActiveHoverInverted,n["--n-item-color-hover"]=r.itemColorHoverInverted,n["--n-item-color-active"]=r.itemColorActiveInverted,n["--n-item-color-active-hover"]=r.itemColorActiveHoverInverted,n["--n-item-color-active-collapsed"]=r.itemColorActiveCollapsedInverted):(n["--n-group-text-color"]=r.groupTextColor,n["--n-color"]=r.color,n["--n-item-text-color"]=r.itemTextColor,n["--n-item-text-color-hover"]=r.itemTextColorHover,n["--n-item-text-color-active"]=r.itemTextColorActive,n["--n-item-text-color-child-active"]=r.itemTextColorChildActive,n["--n-item-text-color-child-active-hover"]=r.itemTextColorChildActiveHover,n["--n-item-text-color-active-hover"]=r.itemTextColorActiveHover,n["--n-item-icon-color"]=r.itemIconColor,n["--n-item-icon-color-hover"]=r.itemIconColorHover,n["--n-item-icon-color-active"]=r.itemIconColorActive,n["--n-item-icon-color-active-hover"]=r.itemIconColorActiveHover,n["--n-item-icon-color-child-active"]=r.itemIconColorChildActive,n["--n-item-icon-color-child-active-hover"]=r.itemIconColorChildActiveHover,n["--n-item-icon-color-collapsed"]=r.itemIconColorCollapsed,n["--n-item-text-color-horizontal"]=r.itemTextColorHorizontal,n["--n-item-text-color-hover-horizontal"]=r.itemTextColorHoverHorizontal,n["--n-item-text-color-active-horizontal"]=r.itemTextColorActiveHorizontal,n["--n-item-text-color-child-active-horizontal"]=r.itemTextColorChildActiveHorizontal,n["--n-item-text-color-child-active-hover-horizontal"]=r.itemTextColorChildActiveHoverHorizontal,n["--n-item-text-color-active-hover-horizontal"]=r.itemTextColorActiveHoverHorizontal,n["--n-item-icon-color-horizontal"]=r.itemIconColorHorizontal,n["--n-item-icon-color-hover-horizontal"]=r.itemIconColorHoverHorizontal,n["--n-item-icon-color-active-horizontal"]=r.itemIconColorActiveHorizontal,n["--n-item-icon-color-active-hover-horizontal"]=r.itemIconColorActiveHoverHorizontal,n["--n-item-icon-color-child-active-horizontal"]=r.itemIconColorChildActiveHorizontal,n["--n-item-icon-color-child-active-hover-horizontal"]=r.itemIconColorChildActiveHoverHorizontal,n["--n-arrow-color"]=r.arrowColor,n["--n-arrow-color-hover"]=r.arrowColorHover,n["--n-arrow-color-active"]=r.arrowColorActive,n["--n-arrow-color-active-hover"]=r.arrowColorActiveHover,n["--n-arrow-color-child-active"]=r.arrowColorChildActive,n["--n-arrow-color-child-active-hover"]=r.arrowColorChildActiveHover,n["--n-item-color-hover"]=r.itemColorHover,n["--n-item-color-active"]=r.itemColorActive,n["--n-item-color-active-hover"]=r.itemColorActiveHover,n["--n-item-color-active-collapsed"]=r.itemColorActiveCollapsed),n}),b=o?ae("menu",C(()=>e.inverted?"a":"b"),y,e):void 0,O=io(),j=B(null),ce=B(null);let E=!0;const Se=()=>{var m;E?E=!1:(m=j.value)===null||m===void 0||m.sync({showAllItemsBeforeCalculate:!0})};function De(){return document.getElementById(O)}const oe=B(-1);function Ue(m){oe.value=e.options.length-m}function Ge(m){m||(oe.value=-1)}const We=C(()=>{const m=oe.value;return{children:m===-1?[]:e.options.slice(m)}}),qe=C(()=>{const{childrenField:m,disabledField:z,keyField:r}=e;return ve([We.value],{getIgnored(x){return ge(x)},getChildren(x){return x[m]},getDisabled(x){return x[z]},getKey(x){var P;return(P=x[r])!==null&&P!==void 0?P:x.name}})}),Ye=C(()=>ve([{}]).treeNodes[0]);function Xe(){var m;if(oe.value===-1)return u(be,{root:!0,level:0,key:"__ellpisisGroupPlaceholder__",internalKey:"__ellpisisGroupPlaceholder__",title:"···",tmNode:Ye.value,domId:O,isEllipsisPlaceholder:!0});const z=qe.value.treeNodes[0],r=N.value,x=!!(!((m=z.children)===null||m===void 0)&&m.some(P=>r.includes(P.key)));return u(be,{level:0,root:!0,key:"__ellpisisGroup__",internalKey:"__ellpisisGroup__",title:"···",virtualChildActive:x,tmNode:z,domId:O,rawNodes:z.rawNode.children||[],tmNodes:z.children||[],isEllipsisPlaceholder:!0})}return{mergedClsPrefix:t,controlledExpandedKeys:k,uncontrolledExpanededKeys:g,mergedExpandedKeys:S,uncontrolledValue:p,mergedValue:_,activePath:N,tmNodes:T,mergedTheme:i,mergedCollapsed:l,cssVars:o?void 0:y,themeClass:b==null?void 0:b.themeClass,overflowRef:j,counterRef:ce,updateCounter:()=>{},onResize:Se,onUpdateOverflow:Ge,onUpdateCount:Ue,renderCounter:Xe,getCounter:De,onRender:b==null?void 0:b.onRender,showOption:f,deriveResponsiveState:Se}},render(){const{mergedClsPrefix:e,mode:t,themeClass:o,onRender:i}=this;i==null||i();const a=()=>this.tmNodes.map(c=>we(c,this.$props)),s=t==="horizontal"&&this.responsive,h=()=>u("div",ao(this.$attrs,{role:t==="horizontal"?"menubar":"menu",class:[`${e}-menu`,o,`${e}-menu--${t}`,s&&`${e}-menu--responsive`,this.mergedCollapsed&&`${e}-menu--collapsed`],style:this.cssVars}),s?u(Io,{ref:"overflowRef",onUpdateOverflow:this.onUpdateOverflow,getCounter:this.getCounter,onUpdateCount:this.onUpdateCount,updateCounter:this.updateCounter,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:a,counter:this.renderCounter}):a());return s?u(lo,{onResize:this.onResize},{default:h}):h()}}),K=B([{name:"Dashboard",label:"仪表盘"}]),ne=B("Dashboard");function et(){const e=C(()=>K.value.map(i=>i.name));function t(i,a){K.value.find(l=>l.name===i)||K.value.push({name:i,label:a}),ne.value=i}function o(i){if(i==="Dashboard")return null;const a=K.value.findIndex(s=>s.name===i);if(a===-1)return null;const l=ne.value===i;if(K.value.splice(a,1),l&&K.value.length>0){const s=Math.min(a,K.value.length-1),h=K.value[s].name;return ne.value=h,h}return null}return{tabs:K,activeTab:ne,cachedNames:e,addTab:t,removeTab:o}}const ot=["onClick"],tt=["onClick"],rt={class:"admin-page"},nt=L({__name:"AdminLayout",setup(e){const t=yo(),o=po(),i=So(),{tabs:a,cachedNames:l,addTab:s,removeTab:h}=et(),c=B(null),p=new Map;function H(I,f){f instanceof HTMLElement&&p.set(I,f)}function _(I){var f;(f=c.value)==null||f.scrollBy({left:I.deltaY,behavior:"smooth"})}function g(I){zo(()=>{var f;(f=p.get(I))==null||f.scrollIntoView({behavior:"smooth",inline:"nearest",block:"nearest"})})}const A=[{label:"仪表盘",key:"Dashboard"},{label:"库位管理",key:"location",children:[{label:"库位列表",key:"WarehouseLocation"},{label:"巷道灯绑定",key:"LedMapping"},{label:"标签绑定",key:"LabelMapping"},{label:"库存查询",key:"InventoryQuery"}]},{label:"巷道灯管理",key:"led",children:[{label:"设备管理",key:"LedDevice"},{label:"颜色管理",key:"LedColor"},{label:"Netty 管理",key:"ZintisNetty"}]},{label:"EPC 管理",key:"epc",children:[{label:"批次 EPC 绑定",key:"BatchEpc"},{label:"RFID Server",key:"RfidServer"}]},{label:"拣货管理",key:"picking",children:[{label:"拣货单",key:"PickingUpload"},{label:"用户灯色映射",key:"UserLightColor"}]},{label:"系统管理",key:"system",children:[{label:"App 版本管理",key:"AppVersion"},{label:"数据库管理",key:"DatabaseManagement"},{label:"定时任务",key:"JobManagement"},{label:"AIMS 配置",key:"AimsConfig"},{label:"Magic-API",key:"MagicApiConsole"},{label:"实时日志",key:"RealtimeLog"}]}],k=C(()=>String(o.name));function S(I){t.push({name:I})}function T(){var f;const I=o.name;if(I){const y=((f=o.meta)==null?void 0:f.label)||I;s(I,y),g(I)}}co(()=>o.name,T,{immediate:!0});function N(I){I!==o.name&&t.push({name:I})}function U(I){const f=h(I);f&&f!==o.name&&t.push({name:f})}function $(){i.warning({title:"确认退出",content:"确定要退出登录吗？",positiveText:"确认",negativeText:"取消",onPositiveClick:()=>{xo(),t.push({name:"Login"})}})}return(I,f)=>{const y=Co("router-view");return Y(),de(F(ke),{class:"admin-shell","has-sider":"","content-style":"height: 100%"},{default:G(()=>[W(F(Vo),{class:"admin-sider",bordered:"",width:220,"native-scrollbar":!1,"collapse-mode":"width","collapsed-width":64,"show-trigger":!1},{default:G(()=>[f[0]||(f[0]=te("div",{class:"admin-brand"}," 东信和平管理后台 ",-1)),W(F(Zo),{class:"admin-menu",options:A,value:k.value,"onUpdate:value":S},null,8,["value"])]),_:1}),W(F(ke),{class:"admin-main","content-style":"height: 100%; min-height: 0; display: flex; flex-direction: column"},{default:G(()=>[W(F(Mo),{bordered:"",class:"admin-header"},{default:G(()=>[W(F(so),{text:"",onClick:$},{default:G(()=>[...f[1]||(f[1]=[uo("退出登录",-1)])]),_:1})]),_:1}),te("div",{ref_key:"tabsRef",ref:c,class:"admin-tabs",onWheel:Pe(_,["prevent"])},[(Y(!0),ue(Ee,null,vo(F(a),b=>(Y(),ue("div",{ref_for:!0,ref:O=>H(b.name,O),key:b.name,class:mo(["admin-tab",{"admin-tab--active":b.name===F(o).name}]),onClick:O=>N(b.name)},[te("span",null,ho(b.label),1),b.name!=="Dashboard"?(Y(),ue("span",{key:0,class:"admin-tab__close",onClick:Pe(O=>U(b.name),["stop"])},"✕",8,tt)):fo("",!0)],10,ot))),128))],544),W(F(Eo),{class:"admin-content","content-style":"height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"},{default:G(()=>[te("div",rt,[W(y,null,{default:G(({Component:b})=>[(Y(),de(go,{include:F(l)},[(Y(),de(bo(b),{class:"admin-page-content"}))],1032,["include"]))]),_:1})])]),_:1})]),_:1})]),_:1})}}}),vt=No(nt,[["__scopeId","data-v-8a863e6d"]]);export{vt as default};

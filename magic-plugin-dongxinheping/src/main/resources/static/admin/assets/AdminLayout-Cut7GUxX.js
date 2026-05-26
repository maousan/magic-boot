import{ah as B,aC as s,ae as Pe,bH as Je,aj as Te,a2 as ze,Z as ie,ab as Z,J as u,Q as w,S as Ne,bR as te,c0 as D,bW as ke,c1 as re,a3 as b,bu as L,bq as Y,P as d,H as I,d as _e,aG as V,Y as E,bK as oe,R as X,ap as Qe,bv as q,a_ as ge,bT as ue,b as eo,aZ as le,i as oo,V as to,c5 as we,aa as ro,b2 as no,a7 as ae,c6 as j,bO as U,bn as ce,af as K,a6 as Se,B as io,ad as lo,r as ao,K as co,bB as so,b0 as uo,bA as vo,bY as mo,bX as ho}from"./index-CS-AcQCH.js";import{k as go,t as fo,C as po,b as bo,N as Co,c as xo,r as yo,i as de,s as Io}from"./composables-BR42jiX5.js";import{f as se,u as ve}from"./get-Clr8IxGh.js";import{_ as zo}from"./_plugin-vue_export-helper-DlAUqK2U.js";const wo=B({name:"ChevronDownFilled",render(){return s("svg",{viewBox:"0 0 16 16",fill:"none",xmlns:"http://www.w3.org/2000/svg"},s("path",{d:"M3.20041 5.73966C3.48226 5.43613 3.95681 5.41856 4.26034 5.70041L8 9.22652L11.7397 5.70041C12.0432 5.41856 12.5177 5.43613 12.7996 5.73966C13.0815 6.0432 13.0639 6.51775 12.7603 6.7996L8.51034 10.7996C8.22258 11.0668 7.77743 11.0668 7.48967 10.7996L3.23966 6.7996C2.93613 6.51775 2.91856 6.0432 3.20041 5.73966Z",fill:"currentColor"}))}});function So(e){const{baseColor:t,textColor2:o,bodyColor:n,cardColor:a,dividerColor:l,actionColor:v,scrollbarColor:h,scrollbarColorHover:c,invertedColor:g}=e;return{textColor:o,textColorInverted:"#FFF",color:n,colorEmbedded:v,headerColor:a,headerColorInverted:g,footerColor:v,footerColorInverted:g,headerBorderColor:l,headerBorderColorInverted:g,footerBorderColor:l,footerBorderColorInverted:g,siderBorderColor:l,siderBorderColorInverted:g,siderColor:a,siderColorInverted:g,siderToggleButtonBorder:`1px solid ${l}`,siderToggleButtonColor:t,siderToggleButtonIconColor:o,siderToggleButtonIconColorInverted:o,siderToggleBarColor:ze(n,h),siderToggleBarColorHover:ze(n,c),__invertScrollbar:"true"}}const fe=Pe({name:"Layout",common:Te,peers:{Scrollbar:Je},self:So});function Ao(e,t,o,n){return{itemColorHoverInverted:"#0000",itemColorActiveInverted:t,itemColorActiveHoverInverted:t,itemColorActiveCollapsedInverted:t,itemTextColorInverted:e,itemTextColorHoverInverted:o,itemTextColorChildActiveInverted:o,itemTextColorChildActiveHoverInverted:o,itemTextColorActiveInverted:o,itemTextColorActiveHoverInverted:o,itemTextColorHorizontalInverted:e,itemTextColorHoverHorizontalInverted:o,itemTextColorChildActiveHorizontalInverted:o,itemTextColorChildActiveHoverHorizontalInverted:o,itemTextColorActiveHorizontalInverted:o,itemTextColorActiveHoverHorizontalInverted:o,itemIconColorInverted:e,itemIconColorHoverInverted:o,itemIconColorActiveInverted:o,itemIconColorActiveHoverInverted:o,itemIconColorChildActiveInverted:o,itemIconColorChildActiveHoverInverted:o,itemIconColorCollapsedInverted:e,itemIconColorHorizontalInverted:e,itemIconColorHoverHorizontalInverted:o,itemIconColorActiveHorizontalInverted:o,itemIconColorActiveHoverHorizontalInverted:o,itemIconColorChildActiveHorizontalInverted:o,itemIconColorChildActiveHoverHorizontalInverted:o,arrowColorInverted:e,arrowColorHoverInverted:o,arrowColorActiveInverted:o,arrowColorActiveHoverInverted:o,arrowColorChildActiveInverted:o,arrowColorChildActiveHoverInverted:o,groupTextColorInverted:n}}function Ho(e){const{borderRadius:t,textColor3:o,primaryColor:n,textColor2:a,textColor1:l,fontSize:v,dividerColor:h,hoverColor:c,primaryColorHover:g}=e;return Object.assign({borderRadius:t,color:"#0000",groupTextColor:o,itemColorHover:c,itemColorActive:ie(n,{alpha:.1}),itemColorActiveHover:ie(n,{alpha:.1}),itemColorActiveCollapsed:ie(n,{alpha:.1}),itemTextColor:a,itemTextColorHover:a,itemTextColorActive:n,itemTextColorActiveHover:n,itemTextColorChildActive:n,itemTextColorChildActiveHover:n,itemTextColorHorizontal:a,itemTextColorHoverHorizontal:g,itemTextColorActiveHorizontal:n,itemTextColorActiveHoverHorizontal:n,itemTextColorChildActiveHorizontal:n,itemTextColorChildActiveHoverHorizontal:n,itemIconColor:l,itemIconColorHover:l,itemIconColorActive:n,itemIconColorActiveHover:n,itemIconColorChildActive:n,itemIconColorChildActiveHover:n,itemIconColorCollapsed:l,itemIconColorHorizontal:l,itemIconColorHoverHorizontal:g,itemIconColorActiveHorizontal:n,itemIconColorActiveHoverHorizontal:n,itemIconColorChildActiveHorizontal:n,itemIconColorChildActiveHoverHorizontal:n,itemHeight:"42px",arrowColor:a,arrowColorHover:a,arrowColorActive:n,arrowColorActiveHover:n,arrowColorChildActive:n,arrowColorChildActiveHover:n,colorInverted:"#0000",borderColorHorizontal:"#0000",fontSize:v,dividerColor:h},Ao("#BBB",n,"#FFF","#AAA"))}const Ro=Pe({name:"Menu",common:Te,peers:{Tooltip:fo,Dropdown:go},self:Ho}),Be=Z("n-layout-sider"),pe={type:String,default:"static"},Po=u("layout",`
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
`,[u("layout-scroll-container",`
 overflow-x: hidden;
 box-sizing: border-box;
 height: 100%;
 `),w("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 `)]),To={embedded:Boolean,position:pe,nativeScrollbar:{type:Boolean,default:!0},scrollbarProps:Object,onScroll:Function,contentClass:String,contentStyle:{type:[String,Object],default:""},hasSider:Boolean,siderPlacement:{type:String,default:"left"}},Oe=Z("n-layout");function Ee(e){return B({name:e?"LayoutContent":"Layout",props:Object.assign(Object.assign({},D.props),To),setup(t){const o=L(null),n=L(null),{mergedClsPrefixRef:a,inlineThemeDisabled:l}=te(t),v=D("Layout","-layout",Po,fe,t,a);function h(z,S){if(t.nativeScrollbar){const{value:T}=o;T&&(S===void 0?T.scrollTo(z):T.scrollTo(z,S))}else{const{value:T}=n;T&&T.scrollTo(z,S)}}Y(Oe,t);let c=0,g=0;const k=z=>{var S;const T=z.target;c=T.scrollLeft,g=T.scrollTop,(S=t.onScroll)===null||S===void 0||S.call(t,z)};ke(()=>{if(t.nativeScrollbar){const z=o.value;z&&(z.scrollTop=g,z.scrollLeft=c)}});const R={display:"flex",flexWrap:"nowrap",width:"100%",flexDirection:"row"},f={scrollTo:h},N=b(()=>{const{common:{cubicBezierEaseInOut:z},self:S}=v.value;return{"--n-bezier":z,"--n-color":t.embedded?S.colorEmbedded:S.color,"--n-text-color":S.textColor}}),P=l?re("layout",b(()=>t.embedded?"e":""),N,t):void 0;return Object.assign({mergedClsPrefix:a,scrollableElRef:o,scrollbarInstRef:n,hasSiderStyle:R,mergedTheme:v,handleNativeElScroll:k,cssVars:l?void 0:N,themeClass:P==null?void 0:P.themeClass,onRender:P==null?void 0:P.onRender},f)},render(){var t;const{mergedClsPrefix:o,hasSider:n}=this;(t=this.onRender)===null||t===void 0||t.call(this);const a=n?this.hasSiderStyle:void 0,l=[this.themeClass,e&&`${o}-layout-content`,`${o}-layout`,`${o}-layout--${this.position}-positioned`];return s("div",{class:l,style:this.cssVars},this.nativeScrollbar?s("div",{ref:"scrollableElRef",class:[`${o}-layout-scroll-container`,this.contentClass],style:[this.contentStyle,a],onScroll:this.handleNativeElScroll},this.$slots):s(Ne,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,contentClass:this.contentClass,contentStyle:[this.contentStyle,a]}),this.$slots))}})}const Ae=Ee(!1),No=Ee(!0),ko=u("layout-header",`
 transition:
 color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 box-sizing: border-box;
 width: 100%;
 background-color: var(--n-color);
 color: var(--n-text-color);
`,[w("absolute-positioned",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 `),w("bordered",`
 border-bottom: solid 1px var(--n-border-color);
 `)]),_o={position:pe,inverted:Boolean,bordered:{type:Boolean,default:!1}},Bo=B({name:"LayoutHeader",props:Object.assign(Object.assign({},D.props),_o),setup(e){const{mergedClsPrefixRef:t,inlineThemeDisabled:o}=te(e),n=D("Layout","-layout-header",ko,fe,e,t),a=b(()=>{const{common:{cubicBezierEaseInOut:v},self:h}=n.value,c={"--n-bezier":v};return e.inverted?(c["--n-color"]=h.headerColorInverted,c["--n-text-color"]=h.textColorInverted,c["--n-border-color"]=h.headerBorderColorInverted):(c["--n-color"]=h.headerColor,c["--n-text-color"]=h.textColor,c["--n-border-color"]=h.headerBorderColor),c}),l=o?re("layout-header",b(()=>e.inverted?"a":"b"),a,e):void 0;return{mergedClsPrefix:t,cssVars:o?void 0:a,themeClass:l==null?void 0:l.themeClass,onRender:l==null?void 0:l.onRender}},render(){var e;const{mergedClsPrefix:t}=this;return(e=this.onRender)===null||e===void 0||e.call(this),s("div",{class:[`${t}-layout-header`,this.themeClass,this.position&&`${t}-layout-header--${this.position}-positioned`,this.bordered&&`${t}-layout-header--bordered`],style:this.cssVars},this.$slots)}}),Oo=u("layout-sider",`
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
`,[w("bordered",[d("border",`
 content: "";
 position: absolute;
 top: 0;
 bottom: 0;
 width: 1px;
 background-color: var(--n-border-color);
 transition: background-color .3s var(--n-bezier);
 `)]),d("left-placement",[w("bordered",[d("border",`
 right: 0;
 `)])]),w("right-placement",`
 justify-content: flex-start;
 `,[w("bordered",[d("border",`
 left: 0;
 `)]),w("collapsed",[u("layout-toggle-button",[u("base-icon",`
 transform: rotate(180deg);
 `)]),u("layout-toggle-bar",[I("&:hover",[d("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])])]),u("layout-toggle-button",`
 left: 0;
 transform: translateX(-50%) translateY(-50%);
 `,[u("base-icon",`
 transform: rotate(0);
 `)]),u("layout-toggle-bar",`
 left: -28px;
 transform: rotate(180deg);
 `,[I("&:hover",[d("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})])])]),w("collapsed",[u("layout-toggle-bar",[I("&:hover",[d("top",{transform:"rotate(-12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(12deg) scale(1.15) translateY(2px)"})])]),u("layout-toggle-button",[u("base-icon",`
 transform: rotate(0);
 `)])]),u("layout-toggle-button",`
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
 `,[u("base-icon",`
 transition: transform .3s var(--n-bezier);
 transform: rotate(180deg);
 `)]),u("layout-toggle-bar",`
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
 `),I("&:hover",[d("top",{transform:"rotate(12deg) scale(1.15) translateY(-2px)"}),d("bottom",{transform:"rotate(-12deg) scale(1.15) translateY(2px)"})]),d("top, bottom",{backgroundColor:"var(--n-toggle-bar-color)"}),I("&:hover",[d("top, bottom",{backgroundColor:"var(--n-toggle-bar-color-hover)"})])]),d("border",`
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 width: 1px;
 transition: background-color .3s var(--n-bezier);
 `),u("layout-sider-scroll-container",`
 flex-grow: 1;
 flex-shrink: 0;
 box-sizing: border-box;
 height: 100%;
 opacity: 0;
 transition: opacity .3s var(--n-bezier);
 max-width: 100%;
 `),w("show-content",[u("layout-sider-scroll-container",{opacity:1})]),w("absolute-positioned",`
 position: absolute;
 left: 0;
 top: 0;
 bottom: 0;
 `)]),Eo=B({props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return s("div",{onClick:this.onClick,class:`${e}-layout-toggle-bar`},s("div",{class:`${e}-layout-toggle-bar__top`}),s("div",{class:`${e}-layout-toggle-bar__bottom`}))}}),Lo=B({name:"LayoutToggleButton",props:{clsPrefix:{type:String,required:!0},onClick:Function},render(){const{clsPrefix:e}=this;return s("div",{class:`${e}-layout-toggle-button`,onClick:this.onClick},s(_e,{clsPrefix:e},{default:()=>s(po,null)}))}}),Mo={position:pe,bordered:Boolean,collapsedWidth:{type:Number,default:48},width:{type:[Number,String],default:272},contentClass:String,contentStyle:{type:[String,Object],default:""},collapseMode:{type:String,default:"transform"},collapsed:{type:Boolean,default:void 0},defaultCollapsed:Boolean,showCollapsedContent:{type:Boolean,default:!0},showTrigger:{type:[Boolean,String],default:!1},nativeScrollbar:{type:Boolean,default:!0},inverted:Boolean,scrollbarProps:Object,triggerClass:String,triggerStyle:[String,Object],collapsedTriggerClass:String,collapsedTriggerStyle:[String,Object],"onUpdate:collapsed":[Function,Array],onUpdateCollapsed:[Function,Array],onAfterEnter:Function,onAfterLeave:Function,onExpand:[Function,Array],onCollapse:[Function,Array],onScroll:Function},$o=B({name:"LayoutSider",props:Object.assign(Object.assign({},D.props),Mo),setup(e){const t=V(Oe),o=L(null),n=L(null),a=L(e.defaultCollapsed),l=ve(oe(e,"collapsed"),a),v=b(()=>se(l.value?e.collapsedWidth:e.width)),h=b(()=>e.collapseMode!=="transform"?{}:{minWidth:se(e.width)}),c=b(()=>t?t.siderPlacement:"left");function g(H,x){if(e.nativeScrollbar){const{value:y}=o;y&&(x===void 0?y.scrollTo(H):y.scrollTo(H,x))}else{const{value:y}=n;y&&y.scrollTo(H,x)}}function k(){const{"onUpdate:collapsed":H,onUpdateCollapsed:x,onExpand:y,onCollapse:F}=e,{value:M}=l;x&&E(x,!M),H&&E(H,!M),a.value=!M,M?y&&E(y):F&&E(F)}let R=0,f=0;const N=H=>{var x;const y=H.target;R=y.scrollLeft,f=y.scrollTop,(x=e.onScroll)===null||x===void 0||x.call(e,H)};ke(()=>{if(e.nativeScrollbar){const H=o.value;H&&(H.scrollTop=f,H.scrollLeft=R)}}),Y(Be,{collapsedRef:l,collapseModeRef:oe(e,"collapseMode")});const{mergedClsPrefixRef:P,inlineThemeDisabled:z}=te(e),S=D("Layout","-layout-sider",Oo,fe,e,P);function T(H){var x,y;H.propertyName==="max-width"&&(l.value?(x=e.onAfterLeave)===null||x===void 0||x.call(e):(y=e.onAfterEnter)===null||y===void 0||y.call(e))}const W={scrollTo:g},$=b(()=>{const{common:{cubicBezierEaseInOut:H},self:x}=S.value,{siderToggleButtonColor:y,siderToggleButtonBorder:F,siderToggleBarColor:M,siderToggleBarColorHover:ne}=x,_={"--n-bezier":H,"--n-toggle-button-color":y,"--n-toggle-button-border":F,"--n-toggle-bar-color":M,"--n-toggle-bar-color-hover":ne};return e.inverted?(_["--n-color"]=x.siderColorInverted,_["--n-text-color"]=x.textColorInverted,_["--n-border-color"]=x.siderBorderColorInverted,_["--n-toggle-button-icon-color"]=x.siderToggleButtonIconColorInverted,_.__invertScrollbar=x.__invertScrollbar):(_["--n-color"]=x.siderColor,_["--n-text-color"]=x.textColor,_["--n-border-color"]=x.siderBorderColor,_["--n-toggle-button-icon-color"]=x.siderToggleButtonIconColor),_}),O=z?re("layout-sider",b(()=>e.inverted?"a":"b"),$,e):void 0;return Object.assign({scrollableElRef:o,scrollbarInstRef:n,mergedClsPrefix:P,mergedTheme:S,styleMaxWidth:v,mergedCollapsed:l,scrollContainerStyle:h,siderPlacement:c,handleNativeElScroll:N,handleTransitionend:T,handleTriggerClick:k,inlineThemeDisabled:z,cssVars:$,themeClass:O==null?void 0:O.themeClass,onRender:O==null?void 0:O.onRender},W)},render(){var e;const{mergedClsPrefix:t,mergedCollapsed:o,showTrigger:n}=this;return(e=this.onRender)===null||e===void 0||e.call(this),s("aside",{class:[`${t}-layout-sider`,this.themeClass,`${t}-layout-sider--${this.position}-positioned`,`${t}-layout-sider--${this.siderPlacement}-placement`,this.bordered&&`${t}-layout-sider--bordered`,o&&`${t}-layout-sider--collapsed`,(!o||this.showCollapsedContent)&&`${t}-layout-sider--show-content`],onTransitionend:this.handleTransitionend,style:[this.inlineThemeDisabled?void 0:this.cssVars,{maxWidth:this.styleMaxWidth,width:se(this.width)}]},this.nativeScrollbar?s("div",{class:[`${t}-layout-sider-scroll-container`,this.contentClass],onScroll:this.handleNativeElScroll,style:[this.scrollContainerStyle,{overflow:"auto"},this.contentStyle],ref:"scrollableElRef"},this.$slots):s(Ne,Object.assign({},this.scrollbarProps,{onScroll:this.onScroll,ref:"scrollbarInstRef",style:this.scrollContainerStyle,contentStyle:this.contentStyle,contentClass:this.contentClass,theme:this.mergedTheme.peers.Scrollbar,themeOverrides:this.mergedTheme.peerOverrides.Scrollbar,builtinThemeOverrides:this.inverted&&this.cssVars.__invertScrollbar==="true"?{colorHover:"rgba(255, 255, 255, .4)",color:"rgba(255, 255, 255, .3)"}:void 0}),this.$slots),n?n==="bar"?s(Eo,{clsPrefix:t,class:o?this.collapsedTriggerClass:this.triggerClass,style:o?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):s(Lo,{clsPrefix:t,class:o?this.collapsedTriggerClass:this.triggerClass,style:o?this.collapsedTriggerStyle:this.triggerStyle,onClick:this.handleTriggerClick}):null,this.bordered?s("div",{class:`${t}-layout-sider__border`}):null)}}),J=Z("n-menu"),Le=Z("n-submenu"),be=Z("n-menu-item-group"),He=[I("&::before","background-color: var(--n-item-color-hover);"),d("arrow",`
 color: var(--n-arrow-color-hover);
 `),d("icon",`
 color: var(--n-item-icon-color-hover);
 `),u("menu-item-content-header",`
 color: var(--n-item-text-color-hover);
 `,[I("a",`
 color: var(--n-item-text-color-hover);
 `),d("extra",`
 color: var(--n-item-text-color-hover);
 `)])],Re=[d("icon",`
 color: var(--n-item-icon-color-hover-horizontal);
 `),u("menu-item-content-header",`
 color: var(--n-item-text-color-hover-horizontal);
 `,[I("a",`
 color: var(--n-item-text-color-hover-horizontal);
 `),d("extra",`
 color: var(--n-item-text-color-hover-horizontal);
 `)])],Fo=I([u("menu",`
 background-color: var(--n-color);
 color: var(--n-item-text-color);
 overflow: hidden;
 transition: background-color .3s var(--n-bezier);
 box-sizing: border-box;
 font-size: var(--n-font-size);
 padding-bottom: 6px;
 `,[w("horizontal",`
 max-width: 100%;
 width: 100%;
 display: flex;
 overflow: hidden;
 padding-bottom: 0;
 `,[u("submenu","margin: 0;"),u("menu-item","margin: 0;"),u("menu-item-content",`
 padding: 0 20px;
 border-bottom: 2px solid #0000;
 `,[I("&::before","display: none;"),w("selected","border-bottom: 2px solid var(--n-border-color-horizontal)")]),u("menu-item-content",[w("selected",[d("icon","color: var(--n-item-icon-color-active-horizontal);"),u("menu-item-content-header",`
 color: var(--n-item-text-color-active-horizontal);
 `,[I("a","color: var(--n-item-text-color-active-horizontal);"),d("extra","color: var(--n-item-text-color-active-horizontal);")])]),w("child-active",`
 border-bottom: 2px solid var(--n-border-color-horizontal);
 `,[u("menu-item-content-header",`
 color: var(--n-item-text-color-child-active-horizontal);
 `,[I("a",`
 color: var(--n-item-text-color-child-active-horizontal);
 `),d("extra",`
 color: var(--n-item-text-color-child-active-horizontal);
 `)]),d("icon",`
 color: var(--n-item-icon-color-child-active-horizontal);
 `)]),X("disabled",[X("selected, child-active",[I("&:focus-within",Re)]),w("selected",[G(null,[d("icon","color: var(--n-item-icon-color-active-hover-horizontal);"),u("menu-item-content-header",`
 color: var(--n-item-text-color-active-hover-horizontal);
 `,[I("a","color: var(--n-item-text-color-active-hover-horizontal);"),d("extra","color: var(--n-item-text-color-active-hover-horizontal);")])])]),w("child-active",[G(null,[d("icon","color: var(--n-item-icon-color-child-active-hover-horizontal);"),u("menu-item-content-header",`
 color: var(--n-item-text-color-child-active-hover-horizontal);
 `,[I("a","color: var(--n-item-text-color-child-active-hover-horizontal);"),d("extra","color: var(--n-item-text-color-child-active-hover-horizontal);")])])]),G("border-bottom: 2px solid var(--n-border-color-horizontal);",Re)]),u("menu-item-content-header",[I("a","color: var(--n-item-text-color-horizontal);")])])]),X("responsive",[u("menu-item-content-header",`
 overflow: hidden;
 text-overflow: ellipsis;
 `)]),w("collapsed",[u("menu-item-content",[w("selected",[I("&::before",`
 background-color: var(--n-item-color-active-collapsed) !important;
 `)]),u("menu-item-content-header","opacity: 0;"),d("arrow","opacity: 0;"),d("icon","color: var(--n-item-icon-color-collapsed);")])]),u("menu-item",`
 height: var(--n-item-height);
 margin-top: 6px;
 position: relative;
 `),u("menu-item-content",`
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
 `,[I("> *","z-index: 1;"),I("&::before",`
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
 `),w("disabled",`
 opacity: .45;
 cursor: not-allowed;
 `),w("collapsed",[d("arrow","transform: rotate(0);")]),w("selected",[I("&::before","background-color: var(--n-item-color-active);"),d("arrow","color: var(--n-arrow-color-active);"),d("icon","color: var(--n-item-icon-color-active);"),u("menu-item-content-header",`
 color: var(--n-item-text-color-active);
 `,[I("a","color: var(--n-item-text-color-active);"),d("extra","color: var(--n-item-text-color-active);")])]),w("child-active",[u("menu-item-content-header",`
 color: var(--n-item-text-color-child-active);
 `,[I("a",`
 color: var(--n-item-text-color-child-active);
 `),d("extra",`
 color: var(--n-item-text-color-child-active);
 `)]),d("arrow",`
 color: var(--n-arrow-color-child-active);
 `),d("icon",`
 color: var(--n-item-icon-color-child-active);
 `)]),X("disabled",[X("selected, child-active",[I("&:focus-within",He)]),w("selected",[G(null,[d("arrow","color: var(--n-arrow-color-active-hover);"),d("icon","color: var(--n-item-icon-color-active-hover);"),u("menu-item-content-header",`
 color: var(--n-item-text-color-active-hover);
 `,[I("a","color: var(--n-item-text-color-active-hover);"),d("extra","color: var(--n-item-text-color-active-hover);")])])]),w("child-active",[G(null,[d("arrow","color: var(--n-arrow-color-child-active-hover);"),d("icon","color: var(--n-item-icon-color-child-active-hover);"),u("menu-item-content-header",`
 color: var(--n-item-text-color-child-active-hover);
 `,[I("a","color: var(--n-item-text-color-child-active-hover);"),d("extra","color: var(--n-item-text-color-child-active-hover);")])])]),w("selected",[G(null,[I("&::before","background-color: var(--n-item-color-active-hover);")])]),G(null,He)]),d("icon",`
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
 `),u("menu-item-content-header",`
 grid-area: content;
 transition:
 color .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 opacity: 1;
 white-space: nowrap;
 color: var(--n-item-text-color);
 `,[I("a",`
 outline: none;
 text-decoration: none;
 transition: color .3s var(--n-bezier);
 color: var(--n-item-text-color);
 `,[I("&::before",`
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
 `)])]),u("submenu",`
 cursor: pointer;
 position: relative;
 margin-top: 6px;
 `,[u("menu-item-content",`
 height: var(--n-item-height);
 `),u("submenu-children",`
 overflow: hidden;
 padding: 0;
 `,[Qe({duration:".2s"})])]),u("menu-item-group",[u("menu-item-group-title",`
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
 `)])]),u("menu-tooltip",[I("a",`
 color: inherit;
 text-decoration: none;
 `)]),u("menu-divider",`
 transition: background-color .3s var(--n-bezier);
 background-color: var(--n-divider-color);
 height: 1px;
 margin: 6px 18px;
 `)]);function G(e,t){return[w("hover",e,t),I("&:hover",e,t)]}const Me=B({name:"MenuOptionContent",props:{collapsed:Boolean,disabled:Boolean,title:[String,Function],icon:Function,extra:[String,Function],showArrow:Boolean,childActive:Boolean,hover:Boolean,paddingLeft:Number,selected:Boolean,maxIconSize:{type:Number,required:!0},activeIconSize:{type:Number,required:!0},iconMarginRight:{type:Number,required:!0},clsPrefix:{type:String,required:!0},onClick:Function,tmNode:{type:Object,required:!0},isEllipsisPlaceholder:Boolean},setup(e){const{props:t}=V(J);return{menuProps:t,style:b(()=>{const{paddingLeft:o}=e;return{paddingLeft:o&&`${o}px`}}),iconStyle:b(()=>{const{maxIconSize:o,activeIconSize:n,iconMarginRight:a}=e;return{width:`${o}px`,height:`${o}px`,fontSize:`${n}px`,marginRight:`${a}px`}})}},render(){const{clsPrefix:e,tmNode:t,menuProps:{renderIcon:o,renderLabel:n,renderExtra:a,expandIcon:l}}=this,v=o?o(t.rawNode):q(this.icon);return s("div",{onClick:h=>{var c;(c=this.onClick)===null||c===void 0||c.call(this,h)},role:"none",class:[`${e}-menu-item-content`,{[`${e}-menu-item-content--selected`]:this.selected,[`${e}-menu-item-content--collapsed`]:this.collapsed,[`${e}-menu-item-content--child-active`]:this.childActive,[`${e}-menu-item-content--disabled`]:this.disabled,[`${e}-menu-item-content--hover`]:this.hover}],style:this.style},v&&s("div",{class:`${e}-menu-item-content__icon`,style:this.iconStyle,role:"none"},[v]),s("div",{class:`${e}-menu-item-content-header`,role:"none"},this.isEllipsisPlaceholder?this.title:n?n(t.rawNode):q(this.title),this.extra||a?s("span",{class:`${e}-menu-item-content-header__extra`}," ",a?a(t.rawNode):q(this.extra)):null),this.showArrow?s(_e,{ariaHidden:!0,class:`${e}-menu-item-content__arrow`,clsPrefix:e},{default:()=>l?l(t.rawNode):s(wo,null)}):null)}}),ee=8;function Ce(e){const t=V(J),{props:o,mergedCollapsedRef:n}=t,a=V(Le,null),l=V(be,null),v=b(()=>o.mode==="horizontal"),h=b(()=>v.value?o.dropdownPlacement:"tmNodes"in e?"right-start":"right"),c=b(()=>{var f;return Math.max((f=o.collapsedIconSize)!==null&&f!==void 0?f:o.iconSize,o.iconSize)}),g=b(()=>{var f;return!v.value&&e.root&&n.value&&(f=o.collapsedIconSize)!==null&&f!==void 0?f:o.iconSize}),k=b(()=>{if(v.value)return;const{collapsedWidth:f,indent:N,rootIndent:P}=o,{root:z,isGroup:S}=e,T=P===void 0?N:P;return z?n.value?f/2-c.value/2:T:l&&typeof l.paddingLeftRef.value=="number"?N/2+l.paddingLeftRef.value:a&&typeof a.paddingLeftRef.value=="number"?(S?N/2:N)+a.paddingLeftRef.value:0}),R=b(()=>{const{collapsedWidth:f,indent:N,rootIndent:P}=o,{value:z}=c,{root:S}=e;return v.value||!S||!n.value?ee:(P===void 0?N:P)+z+ee-(f+z)/2});return{dropdownPlacement:h,activeIconSize:g,maxIconSize:c,paddingLeft:k,iconMarginRight:R,NMenu:t,NSubmenu:a,NMenuOptionGroup:l}}const xe={internalKey:{type:[String,Number],required:!0},root:Boolean,isGroup:Boolean,level:{type:Number,required:!0},title:[String,Function],extra:[String,Function]},jo=B({name:"MenuDivider",setup(){const e=V(J),{mergedClsPrefixRef:t,isHorizontalRef:o}=e;return()=>o.value?null:s("div",{class:`${t.value}-menu-divider`})}}),$e=Object.assign(Object.assign({},xe),{tmNode:{type:Object,required:!0},disabled:Boolean,icon:Function,onClick:Function}),Ko=ge($e),Vo=B({name:"MenuOption",props:$e,setup(e){const t=Ce(e),{NSubmenu:o,NMenu:n,NMenuOptionGroup:a}=t,{props:l,mergedClsPrefixRef:v,mergedCollapsedRef:h}=n,c=o?o.mergedDisabledRef:a?a.mergedDisabledRef:{value:!1},g=b(()=>c.value||e.disabled);function k(f){const{onClick:N}=e;N&&N(f)}function R(f){g.value||(n.doSelect(e.internalKey,e.tmNode.rawNode),k(f))}return{mergedClsPrefix:v,dropdownPlacement:t.dropdownPlacement,paddingLeft:t.paddingLeft,iconMarginRight:t.iconMarginRight,maxIconSize:t.maxIconSize,activeIconSize:t.activeIconSize,mergedTheme:n.mergedThemeRef,menuProps:l,dropdownEnabled:ue(()=>e.root&&h.value&&l.mode!=="horizontal"&&!g.value),selected:ue(()=>n.mergedValueRef.value===e.internalKey),mergedDisabled:g,handleClick:R}},render(){const{mergedClsPrefix:e,mergedTheme:t,tmNode:o,menuProps:{renderLabel:n,nodeProps:a}}=this,l=a==null?void 0:a(o.rawNode);return s("div",Object.assign({},l,{role:"menuitem",class:[`${e}-menu-item`,l==null?void 0:l.class]}),s(bo,{theme:t.peers.Tooltip,themeOverrides:t.peerOverrides.Tooltip,trigger:"hover",placement:this.dropdownPlacement,disabled:!this.dropdownEnabled||this.title===void 0,internalExtraClass:["menu-tooltip"]},{default:()=>n?n(o.rawNode):q(this.title),trigger:()=>s(Me,{tmNode:o,clsPrefix:e,paddingLeft:this.paddingLeft,iconMarginRight:this.iconMarginRight,maxIconSize:this.maxIconSize,activeIconSize:this.activeIconSize,selected:this.selected,title:this.title,extra:this.extra,disabled:this.mergedDisabled,icon:this.icon,onClick:this.handleClick})}))}}),Fe=Object.assign(Object.assign({},xe),{tmNode:{type:Object,required:!0},tmNodes:{type:Array,required:!0}}),Do=ge(Fe),Uo=B({name:"MenuOptionGroup",props:Fe,setup(e){const t=Ce(e),{NSubmenu:o}=t,n=b(()=>o!=null&&o.mergedDisabledRef.value?!0:e.tmNode.disabled);Y(be,{paddingLeftRef:t.paddingLeft,mergedDisabledRef:n});const{mergedClsPrefixRef:a,props:l}=V(J);return function(){const{value:v}=a,h=t.paddingLeft.value,{nodeProps:c}=l,g=c==null?void 0:c(e.tmNode.rawNode);return s("div",{class:`${v}-menu-item-group`,role:"group"},s("div",Object.assign({},g,{class:[`${v}-menu-item-group-title`,g==null?void 0:g.class],style:[(g==null?void 0:g.style)||"",h!==void 0?`padding-left: ${h}px;`:""]}),q(e.title),e.extra?s(eo,null," ",q(e.extra)):null),s("div",null,e.tmNodes.map(k=>ye(k,l))))}}});function me(e){return e.type==="divider"||e.type==="render"}function Go(e){return e.type==="divider"}function ye(e,t){const{rawNode:o}=e,{show:n}=o;if(n===!1)return null;if(me(o))return Go(o)?s(jo,Object.assign({key:e.key},o.props)):null;const{labelField:a}=t,{key:l,level:v,isGroup:h}=e,c=Object.assign(Object.assign({},o),{title:o.title||o[a],extra:o.titleExtra||o.extra,key:l,internalKey:l,level:v,root:v===0,isGroup:h});return e.children?e.isGroup?s(Uo,le(c,Do,{tmNode:e,tmNodes:e.children,key:l})):s(he,le(c,qo,{key:l,rawNodes:o[t.childrenField],tmNodes:e.children,tmNode:e})):s(Vo,le(c,Ko,{key:l,tmNode:e}))}const je=Object.assign(Object.assign({},xe),{rawNodes:{type:Array,default:()=>[]},tmNodes:{type:Array,default:()=>[]},tmNode:{type:Object,required:!0},disabled:Boolean,icon:Function,onClick:Function,domId:String,virtualChildActive:{type:Boolean,default:void 0},isEllipsisPlaceholder:Boolean}),qo=ge(je),he=B({name:"Submenu",props:je,setup(e){const t=Ce(e),{NMenu:o,NSubmenu:n}=t,{props:a,mergedCollapsedRef:l,mergedThemeRef:v}=o,h=b(()=>{const{disabled:f}=e;return n!=null&&n.mergedDisabledRef.value||a.disabled?!0:f}),c=L(!1);Y(Le,{paddingLeftRef:t.paddingLeft,mergedDisabledRef:h}),Y(be,null);function g(){const{onClick:f}=e;f&&f()}function k(){h.value||(l.value||o.toggleExpand(e.internalKey),g())}function R(f){c.value=f}return{menuProps:a,mergedTheme:v,doSelect:o.doSelect,inverted:o.invertedRef,isHorizontal:o.isHorizontalRef,mergedClsPrefix:o.mergedClsPrefixRef,maxIconSize:t.maxIconSize,activeIconSize:t.activeIconSize,iconMarginRight:t.iconMarginRight,dropdownPlacement:t.dropdownPlacement,dropdownShow:c,paddingLeft:t.paddingLeft,mergedDisabled:h,mergedValue:o.mergedValueRef,childActive:ue(()=>{var f;return(f=e.virtualChildActive)!==null&&f!==void 0?f:o.activePathRef.value.includes(e.internalKey)}),collapsed:b(()=>a.mode==="horizontal"?!1:l.value?!0:!o.mergedExpandedKeysRef.value.includes(e.internalKey)),dropdownEnabled:b(()=>!h.value&&(a.mode==="horizontal"||l.value)),handlePopoverShowChange:R,handleClick:k}},render(){var e;const{mergedClsPrefix:t,menuProps:{renderIcon:o,renderLabel:n}}=this,a=()=>{const{isHorizontal:v,paddingLeft:h,collapsed:c,mergedDisabled:g,maxIconSize:k,activeIconSize:R,title:f,childActive:N,icon:P,handleClick:z,menuProps:{nodeProps:S},dropdownShow:T,iconMarginRight:W,tmNode:$,mergedClsPrefix:O,isEllipsisPlaceholder:H,extra:x}=this,y=S==null?void 0:S($.rawNode);return s("div",Object.assign({},y,{class:[`${O}-menu-item`,y==null?void 0:y.class],role:"menuitem"}),s(Me,{tmNode:$,paddingLeft:h,collapsed:c,disabled:g,iconMarginRight:W,maxIconSize:k,activeIconSize:R,title:f,extra:x,showArrow:!v,childActive:N,clsPrefix:O,icon:P,hover:T,onClick:z,isEllipsisPlaceholder:H}))},l=()=>s(oo,null,{default:()=>{const{tmNodes:v,collapsed:h}=this;return h?null:s("div",{class:`${t}-submenu-children`,role:"menu"},v.map(c=>ye(c,this.menuProps)))}});return this.root?s(Co,Object.assign({size:"large",trigger:"hover"},(e=this.menuProps)===null||e===void 0?void 0:e.dropdownProps,{themeOverrides:this.mergedTheme.peerOverrides.Dropdown,theme:this.mergedTheme.peers.Dropdown,builtinThemeOverrides:{fontSizeLarge:"14px",optionIconSizeLarge:"18px"},value:this.mergedValue,disabled:!this.dropdownEnabled,placement:this.dropdownPlacement,keyField:this.menuProps.keyField,labelField:this.menuProps.labelField,childrenField:this.menuProps.childrenField,onUpdateShow:this.handlePopoverShowChange,options:this.rawNodes,onSelect:this.doSelect,inverted:this.inverted,renderIcon:o,renderLabel:n}),{default:()=>s("div",{class:`${t}-submenu`,role:"menu","aria-expanded":!this.collapsed,id:this.domId},a(),this.isHorizontal?null:l())}):s("div",{class:`${t}-submenu`,role:"menu","aria-expanded":!this.collapsed,id:this.domId},a(),l())}}),Yo=Object.assign(Object.assign({},D.props),{options:{type:Array,default:()=>[]},collapsed:{type:Boolean,default:void 0},collapsedWidth:{type:Number,default:48},iconSize:{type:Number,default:20},collapsedIconSize:{type:Number,default:24},rootIndent:Number,indent:{type:Number,default:32},labelField:{type:String,default:"label"},keyField:{type:String,default:"key"},childrenField:{type:String,default:"children"},disabledField:{type:String,default:"disabled"},defaultExpandAll:Boolean,defaultExpandedKeys:Array,expandedKeys:Array,value:[String,Number],defaultValue:{type:[String,Number],default:null},mode:{type:String,default:"vertical"},watchProps:{type:Array,default:void 0},disabled:Boolean,show:{type:Boolean,default:!0},inverted:Boolean,"onUpdate:expandedKeys":[Function,Array],onUpdateExpandedKeys:[Function,Array],onUpdateValue:[Function,Array],"onUpdate:value":[Function,Array],expandIcon:Function,renderIcon:Function,renderLabel:Function,renderExtra:Function,dropdownProps:Object,accordion:Boolean,nodeProps:Function,dropdownPlacement:{type:String,default:"bottom"},responsive:Boolean,items:Array,onOpenNamesChange:[Function,Array],onSelect:[Function,Array],onExpandedNamesChange:[Function,Array],expandedNames:Array,defaultExpandedNames:Array}),Wo=B({name:"Menu",inheritAttrs:!1,props:Yo,setup(e){const{mergedClsPrefixRef:t,inlineThemeDisabled:o}=te(e),n=D("Menu","-menu",Fo,Ro,e,t),a=V(Be,null),l=b(()=>{var m;const{collapsed:C}=e;if(C!==void 0)return C;if(a){const{collapseModeRef:r,collapsedRef:p}=a;if(r.value==="width")return(m=p.value)!==null&&m!==void 0?m:!1}return!1}),v=b(()=>{const{keyField:m,childrenField:C,disabledField:r}=e;return de(e.items||e.options,{getIgnored(p){return me(p)},getChildren(p){return p[C]},getDisabled(p){return p[r]},getKey(p){var A;return(A=p[m])!==null&&A!==void 0?A:p.name}})}),h=b(()=>new Set(v.value.treeNodes.map(m=>m.key))),{watchProps:c}=e,g=L(null);c!=null&&c.includes("defaultValue")?we(()=>{g.value=e.defaultValue}):g.value=e.defaultValue;const k=oe(e,"value"),R=ve(k,g),f=L([]),N=()=>{f.value=e.defaultExpandAll?v.value.getNonLeafKeys():e.defaultExpandedNames||e.defaultExpandedKeys||v.value.getPath(R.value,{includeSelf:!1}).keyPath};c!=null&&c.includes("defaultExpandedKeys")?we(N):N();const P=yo(e,["expandedNames","expandedKeys"]),z=ve(P,f),S=b(()=>v.value.treeNodes),T=b(()=>v.value.getPath(R.value).keyPath);Y(J,{props:e,mergedCollapsedRef:l,mergedThemeRef:n,mergedValueRef:R,mergedExpandedKeysRef:z,activePathRef:T,mergedClsPrefixRef:t,isHorizontalRef:b(()=>e.mode==="horizontal"),invertedRef:oe(e,"inverted"),doSelect:W,toggleExpand:O});function W(m,C){const{"onUpdate:value":r,onUpdateValue:p,onSelect:A}=e;p&&E(p,m,C),r&&E(r,m,C),A&&E(A,m,C),g.value=m}function $(m){const{"onUpdate:expandedKeys":C,onUpdateExpandedKeys:r,onExpandedNamesChange:p,onOpenNamesChange:A}=e;C&&E(C,m),r&&E(r,m),p&&E(p,m),A&&E(A,m),f.value=m}function O(m){const C=Array.from(z.value),r=C.findIndex(p=>p===m);if(~r)C.splice(r,1);else{if(e.accordion&&h.value.has(m)){const p=C.findIndex(A=>h.value.has(A));p>-1&&C.splice(p,1)}C.push(m)}$(C)}const H=m=>{const C=v.value.getPath(m??R.value,{includeSelf:!1}).keyPath;if(!C.length)return;const r=Array.from(z.value),p=new Set([...r,...C]);e.accordion&&h.value.forEach(A=>{p.has(A)&&!C.includes(A)&&p.delete(A)}),$(Array.from(p))},x=b(()=>{const{inverted:m}=e,{common:{cubicBezierEaseInOut:C},self:r}=n.value,{borderRadius:p,borderColorHorizontal:A,fontSize:We,itemHeight:Xe,dividerColor:Ze}=r,i={"--n-divider-color":Ze,"--n-bezier":C,"--n-font-size":We,"--n-border-color-horizontal":A,"--n-border-radius":p,"--n-item-height":Xe};return m?(i["--n-group-text-color"]=r.groupTextColorInverted,i["--n-color"]=r.colorInverted,i["--n-item-text-color"]=r.itemTextColorInverted,i["--n-item-text-color-hover"]=r.itemTextColorHoverInverted,i["--n-item-text-color-active"]=r.itemTextColorActiveInverted,i["--n-item-text-color-child-active"]=r.itemTextColorChildActiveInverted,i["--n-item-text-color-child-active-hover"]=r.itemTextColorChildActiveInverted,i["--n-item-text-color-active-hover"]=r.itemTextColorActiveHoverInverted,i["--n-item-icon-color"]=r.itemIconColorInverted,i["--n-item-icon-color-hover"]=r.itemIconColorHoverInverted,i["--n-item-icon-color-active"]=r.itemIconColorActiveInverted,i["--n-item-icon-color-active-hover"]=r.itemIconColorActiveHoverInverted,i["--n-item-icon-color-child-active"]=r.itemIconColorChildActiveInverted,i["--n-item-icon-color-child-active-hover"]=r.itemIconColorChildActiveHoverInverted,i["--n-item-icon-color-collapsed"]=r.itemIconColorCollapsedInverted,i["--n-item-text-color-horizontal"]=r.itemTextColorHorizontalInverted,i["--n-item-text-color-hover-horizontal"]=r.itemTextColorHoverHorizontalInverted,i["--n-item-text-color-active-horizontal"]=r.itemTextColorActiveHorizontalInverted,i["--n-item-text-color-child-active-horizontal"]=r.itemTextColorChildActiveHorizontalInverted,i["--n-item-text-color-child-active-hover-horizontal"]=r.itemTextColorChildActiveHoverHorizontalInverted,i["--n-item-text-color-active-hover-horizontal"]=r.itemTextColorActiveHoverHorizontalInverted,i["--n-item-icon-color-horizontal"]=r.itemIconColorHorizontalInverted,i["--n-item-icon-color-hover-horizontal"]=r.itemIconColorHoverHorizontalInverted,i["--n-item-icon-color-active-horizontal"]=r.itemIconColorActiveHorizontalInverted,i["--n-item-icon-color-active-hover-horizontal"]=r.itemIconColorActiveHoverHorizontalInverted,i["--n-item-icon-color-child-active-horizontal"]=r.itemIconColorChildActiveHorizontalInverted,i["--n-item-icon-color-child-active-hover-horizontal"]=r.itemIconColorChildActiveHoverHorizontalInverted,i["--n-arrow-color"]=r.arrowColorInverted,i["--n-arrow-color-hover"]=r.arrowColorHoverInverted,i["--n-arrow-color-active"]=r.arrowColorActiveInverted,i["--n-arrow-color-active-hover"]=r.arrowColorActiveHoverInverted,i["--n-arrow-color-child-active"]=r.arrowColorChildActiveInverted,i["--n-arrow-color-child-active-hover"]=r.arrowColorChildActiveHoverInverted,i["--n-item-color-hover"]=r.itemColorHoverInverted,i["--n-item-color-active"]=r.itemColorActiveInverted,i["--n-item-color-active-hover"]=r.itemColorActiveHoverInverted,i["--n-item-color-active-collapsed"]=r.itemColorActiveCollapsedInverted):(i["--n-group-text-color"]=r.groupTextColor,i["--n-color"]=r.color,i["--n-item-text-color"]=r.itemTextColor,i["--n-item-text-color-hover"]=r.itemTextColorHover,i["--n-item-text-color-active"]=r.itemTextColorActive,i["--n-item-text-color-child-active"]=r.itemTextColorChildActive,i["--n-item-text-color-child-active-hover"]=r.itemTextColorChildActiveHover,i["--n-item-text-color-active-hover"]=r.itemTextColorActiveHover,i["--n-item-icon-color"]=r.itemIconColor,i["--n-item-icon-color-hover"]=r.itemIconColorHover,i["--n-item-icon-color-active"]=r.itemIconColorActive,i["--n-item-icon-color-active-hover"]=r.itemIconColorActiveHover,i["--n-item-icon-color-child-active"]=r.itemIconColorChildActive,i["--n-item-icon-color-child-active-hover"]=r.itemIconColorChildActiveHover,i["--n-item-icon-color-collapsed"]=r.itemIconColorCollapsed,i["--n-item-text-color-horizontal"]=r.itemTextColorHorizontal,i["--n-item-text-color-hover-horizontal"]=r.itemTextColorHoverHorizontal,i["--n-item-text-color-active-horizontal"]=r.itemTextColorActiveHorizontal,i["--n-item-text-color-child-active-horizontal"]=r.itemTextColorChildActiveHorizontal,i["--n-item-text-color-child-active-hover-horizontal"]=r.itemTextColorChildActiveHoverHorizontal,i["--n-item-text-color-active-hover-horizontal"]=r.itemTextColorActiveHoverHorizontal,i["--n-item-icon-color-horizontal"]=r.itemIconColorHorizontal,i["--n-item-icon-color-hover-horizontal"]=r.itemIconColorHoverHorizontal,i["--n-item-icon-color-active-horizontal"]=r.itemIconColorActiveHorizontal,i["--n-item-icon-color-active-hover-horizontal"]=r.itemIconColorActiveHoverHorizontal,i["--n-item-icon-color-child-active-horizontal"]=r.itemIconColorChildActiveHorizontal,i["--n-item-icon-color-child-active-hover-horizontal"]=r.itemIconColorChildActiveHoverHorizontal,i["--n-arrow-color"]=r.arrowColor,i["--n-arrow-color-hover"]=r.arrowColorHover,i["--n-arrow-color-active"]=r.arrowColorActive,i["--n-arrow-color-active-hover"]=r.arrowColorActiveHover,i["--n-arrow-color-child-active"]=r.arrowColorChildActive,i["--n-arrow-color-child-active-hover"]=r.arrowColorChildActiveHover,i["--n-item-color-hover"]=r.itemColorHover,i["--n-item-color-active"]=r.itemColorActive,i["--n-item-color-active-hover"]=r.itemColorActiveHover,i["--n-item-color-active-collapsed"]=r.itemColorActiveCollapsed),i}),y=o?re("menu",b(()=>e.inverted?"a":"b"),x,e):void 0,F=ro(),M=L(null),ne=L(null);let _=!0;const Ie=()=>{var m;_?_=!1:(m=M.value)===null||m===void 0||m.sync({showAllItemsBeforeCalculate:!0})};function Ke(){return document.getElementById(F)}const Q=L(-1);function Ve(m){Q.value=e.options.length-m}function De(m){m||(Q.value=-1)}const Ue=b(()=>{const m=Q.value;return{children:m===-1?[]:e.options.slice(m)}}),Ge=b(()=>{const{childrenField:m,disabledField:C,keyField:r}=e;return de([Ue.value],{getIgnored(p){return me(p)},getChildren(p){return p[m]},getDisabled(p){return p[C]},getKey(p){var A;return(A=p[r])!==null&&A!==void 0?A:p.name}})}),qe=b(()=>de([{}]).treeNodes[0]);function Ye(){var m;if(Q.value===-1)return s(he,{root:!0,level:0,key:"__ellpisisGroupPlaceholder__",internalKey:"__ellpisisGroupPlaceholder__",title:"···",tmNode:qe.value,domId:F,isEllipsisPlaceholder:!0});const C=Ge.value.treeNodes[0],r=T.value,p=!!(!((m=C.children)===null||m===void 0)&&m.some(A=>r.includes(A.key)));return s(he,{level:0,root:!0,key:"__ellpisisGroup__",internalKey:"__ellpisisGroup__",title:"···",virtualChildActive:p,tmNode:C,domId:F,rawNodes:C.rawNode.children||[],tmNodes:C.children||[],isEllipsisPlaceholder:!0})}return{mergedClsPrefix:t,controlledExpandedKeys:P,uncontrolledExpanededKeys:f,mergedExpandedKeys:z,uncontrolledValue:g,mergedValue:R,activePath:T,tmNodes:S,mergedTheme:n,mergedCollapsed:l,cssVars:o?void 0:x,themeClass:y==null?void 0:y.themeClass,overflowRef:M,counterRef:ne,updateCounter:()=>{},onResize:Ie,onUpdateOverflow:De,onUpdateCount:Ve,renderCounter:Ye,getCounter:Ke,onRender:y==null?void 0:y.onRender,showOption:H,deriveResponsiveState:Ie}},render(){const{mergedClsPrefix:e,mode:t,themeClass:o,onRender:n}=this;n==null||n();const a=()=>this.tmNodes.map(c=>ye(c,this.$props)),v=t==="horizontal"&&this.responsive,h=()=>s("div",no(this.$attrs,{role:t==="horizontal"?"menubar":"menu",class:[`${e}-menu`,o,`${e}-menu--${t}`,v&&`${e}-menu--responsive`,this.mergedCollapsed&&`${e}-menu--collapsed`],style:this.cssVars}),v?s(xo,{ref:"overflowRef",onUpdateOverflow:this.onUpdateOverflow,getCounter:this.getCounter,onUpdateCount:this.onUpdateCount,updateCounter:this.updateCounter,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:a,counter:this.renderCounter}):a());return v?s(to,{onResize:this.onResize},{default:h}):h()}}),Xo={class:"admin-page"},Zo=B({__name:"AdminLayout",setup(e){const t=mo(),o=ho(),n=Io(),a=[{label:"仪表盘",key:"Dashboard"},{label:"库位管理",key:"location",children:[{label:"库位列表",key:"WarehouseLocation"},{label:"巷道灯绑定",key:"LedMapping"},{label:"标签绑定",key:"LabelMapping"},{label:"库存查询",key:"InventoryQuery"}]},{label:"巷道灯管理",key:"led",children:[{label:"设备管理",key:"LedDevice"},{label:"颜色管理",key:"LedColor"},{label:"Netty 管理",key:"ZintisNetty"}]},{label:"EPC 管理",key:"epc",children:[{label:"批次 EPC 管理",key:"BatchEpc"}]},{label:"拣货管理",key:"PickingUpload"},{label:"用户灯色映射",key:"UserLightColor"},{label:"系统管理",key:"system",children:[{label:"数据库管理",key:"DatabaseManagement"},{label:"定时任务",key:"JobManagement"},{label:"Magic-API",key:"MagicApiConsole"}]}],l=b(()=>String(o.name));function v(c){t.push({name:c})}function h(){n.warning({title:"确认退出",content:"确定要退出登录吗？",positiveText:"确认",negativeText:"取消",onPositiveClick:()=>{uo(),t.push({name:"Login"})}})}return(c,g)=>{const k=vo("router-view");return ce(),ae(U(Ae),{class:"admin-shell","has-sider":"","content-style":"height: 100%"},{default:j(()=>[K(U($o),{class:"admin-sider",bordered:"",width:220,"native-scrollbar":!1,"collapse-mode":"width","collapsed-width":64,"show-trigger":!1},{default:j(()=>[g[0]||(g[0]=Se("div",{class:"admin-brand"}," 东信和平管理后台 ",-1)),K(U(Wo),{class:"admin-menu",options:a,value:l.value,"onUpdate:value":v},null,8,["value"])]),_:1}),K(U(Ae),{class:"admin-main","content-style":"height: 100%; min-height: 0; display: flex; flex-direction: column"},{default:j(()=>[K(U(Bo),{bordered:"",class:"admin-header"},{default:j(()=>[K(U(io),{text:"",onClick:h},{default:j(()=>[...g[1]||(g[1]=[lo("退出登录",-1)])]),_:1})]),_:1}),K(U(No),{class:"admin-content","content-style":"height: 100%; min-height: 0; padding: 24px; box-sizing: border-box; overflow: hidden"},{default:j(()=>[Se("div",Xo,[K(k,null,{default:j(({Component:R})=>[K(ao,{name:"fade-slide",mode:"out-in"},{default:j(()=>[(ce(),ae(co,null,[(ce(),ae(so(R)))],1024))]),_:2},1024)]),_:1})])]),_:1})]),_:1})]),_:1})}}}),tt=zo(Zo,[["__scopeId","data-v-c3c326bb"]]);export{tt as default};

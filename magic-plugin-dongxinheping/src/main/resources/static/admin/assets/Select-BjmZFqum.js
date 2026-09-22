import{bx as ln,bM as Je,cu as xe,bv as gn,aj as he,aH as r,al as je,J as R,P as _,H as X,d as bn,cc as We,cp as fe,cq as Ke,a3 as M,ad as ae,ag as rn,bV as eo,bH as Fe,aL as mn,cg as Qe,r as xn,Q as Z,R as on,au as wn,bR as sn,e as no,S as oo,bP as to,cm as Cn,c5 as j,bG as $,bl as yn,ak as io,aD as Ie,bC as dn,Z as ze,s as lo,b as ro,cv as ao,cx as so,cs as co,_ as cn,cd as uo,aW as fo,aF as ho,b7 as vo,Y as te}from"./index-BurxrseI.js";import{e as an,i as po,B as go,a as bo,V as mo,u as tn}from"./Follower-Bv_QCgjR.js";import{N as xo}from"./Input-lJj5DAjw.js";import{N as Xe}from"./_common-CxyKW2CF.js";import{c as wo,V as un,a as Co}from"./create-BUqxyy3v.js";import{a as yo,N as So,u as Oo}from"./Popover-ypxKoLGZ.js";import{V as zo,F as Fo}from"./FocusDetector-BRcwhzu4.js";import{u as Sn}from"./Eye-CzfHwEC3.js";import{h as Be}from"./happens-in-CM8LO42l.js";import{d as fn}from"./request-DrQB4ljS.js";function On(e,t){t&&(ln(()=>{const{value:a}=e;a&&Je.registerHandler(a,t)}),xe(e,(a,s)=>{s&&Je.unregisterHandler(s)},{deep:!1}),gn(()=>{const{value:a}=e;a&&Je.unregisterHandler(a)}))}function hn(e){switch(typeof e){case"string":return e||void 0;case"number":return String(e);default:return}}function en(e){const t=e.filter(a=>a!==void 0);if(t.length!==0)return t.length===1?t[0]:a=>{e.forEach(s=>{s&&s(a)})}}const Ro=he({name:"Checkmark",render(){return r("svg",{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 16 16"},r("g",{fill:"none"},r("path",{d:"M14.046 3.486a.75.75 0 0 1-.032 1.06l-7.93 7.474a.85.85 0 0 1-1.188-.022l-2.68-2.72a.75.75 0 1 1 1.068-1.053l2.234 2.267l7.468-7.038a.75.75 0 0 1 1.06.032z",fill:"currentColor"})))}}),To=he({name:"Empty",render(){return r("svg",{viewBox:"0 0 28 28",fill:"none",xmlns:"http://www.w3.org/2000/svg"},r("path",{d:"M26 7.5C26 11.0899 23.0899 14 19.5 14C15.9101 14 13 11.0899 13 7.5C13 3.91015 15.9101 1 19.5 1C23.0899 1 26 3.91015 26 7.5ZM16.8536 4.14645C16.6583 3.95118 16.3417 3.95118 16.1464 4.14645C15.9512 4.34171 15.9512 4.65829 16.1464 4.85355L18.7929 7.5L16.1464 10.1464C15.9512 10.3417 15.9512 10.6583 16.1464 10.8536C16.3417 11.0488 16.6583 11.0488 16.8536 10.8536L19.5 8.20711L22.1464 10.8536C22.3417 11.0488 22.6583 11.0488 22.8536 10.8536C23.0488 10.6583 23.0488 10.3417 22.8536 10.1464L20.2071 7.5L22.8536 4.85355C23.0488 4.65829 23.0488 4.34171 22.8536 4.14645C22.6583 3.95118 22.3417 3.95118 22.1464 4.14645L19.5 6.79289L16.8536 4.14645Z",fill:"currentColor"}),r("path",{d:"M25 22.75V12.5991C24.5572 13.0765 24.053 13.4961 23.5 13.8454V16H17.5L17.3982 16.0068C17.0322 16.0565 16.75 16.3703 16.75 16.75C16.75 18.2688 15.5188 19.5 14 19.5C12.4812 19.5 11.25 18.2688 11.25 16.75L11.2432 16.6482C11.1935 16.2822 10.8797 16 10.5 16H4.5V7.25C4.5 6.2835 5.2835 5.5 6.25 5.5H12.2696C12.4146 4.97463 12.6153 4.47237 12.865 4H6.25C4.45507 4 3 5.45507 3 7.25V22.75C3 24.5449 4.45507 26 6.25 26H21.75C23.5449 26 25 24.5449 25 22.75ZM4.5 22.75V17.5H9.81597L9.85751 17.7041C10.2905 19.5919 11.9808 21 14 21L14.215 20.9947C16.2095 20.8953 17.842 19.4209 18.184 17.5H23.5V22.75C23.5 23.7165 22.7165 24.5 21.75 24.5H6.25C5.2835 24.5 4.5 23.7165 4.5 22.75Z",fill:"currentColor"}))}}),Po={iconSizeTiny:"28px",iconSizeSmall:"34px",iconSizeMedium:"40px",iconSizeLarge:"46px",iconSizeHuge:"52px"};function Mo(e){const{textColorDisabled:t,iconColor:a,textColor2:s,fontSizeTiny:f,fontSizeSmall:v,fontSizeMedium:c,fontSizeLarge:l,fontSizeHuge:y}=e;return Object.assign(Object.assign({},Po),{fontSizeTiny:f,fontSizeSmall:v,fontSizeMedium:c,fontSizeLarge:l,fontSizeHuge:y,textColor:t,iconColor:a,extraTextColor:s})}const zn={name:"Empty",common:je,self:Mo},ko=R("empty",`
 display: flex;
 flex-direction: column;
 align-items: center;
 font-size: var(--n-font-size);
`,[_("icon",`
 width: var(--n-icon-size);
 height: var(--n-icon-size);
 font-size: var(--n-icon-size);
 line-height: var(--n-icon-size);
 color: var(--n-icon-color);
 transition:
 color .3s var(--n-bezier);
 `,[X("+",[_("description",`
 margin-top: 8px;
 `)])]),_("description",`
 transition: color .3s var(--n-bezier);
 color: var(--n-text-color);
 `),_("extra",`
 text-align: center;
 transition: color .3s var(--n-bezier);
 margin-top: 12px;
 color: var(--n-extra-text-color);
 `)]),_o=Object.assign(Object.assign({},fe.props),{description:String,showDescription:{type:Boolean,default:!0},showIcon:{type:Boolean,default:!0},size:{type:String,default:"medium"},renderIcon:Function}),$o=he({name:"Empty",props:_o,slots:Object,setup(e){const{mergedClsPrefixRef:t,inlineThemeDisabled:a,mergedComponentPropsRef:s}=We(e),f=fe("Empty","-empty",ko,zn,e,t),{localeRef:v}=Sn("Empty"),c=M(()=>{var m,b,P;return(m=e.description)!==null&&m!==void 0?m:(P=(b=s==null?void 0:s.value)===null||b===void 0?void 0:b.Empty)===null||P===void 0?void 0:P.description}),l=M(()=>{var m,b;return((b=(m=s==null?void 0:s.value)===null||m===void 0?void 0:m.Empty)===null||b===void 0?void 0:b.renderIcon)||(()=>r(To,null))}),y=M(()=>{const{size:m}=e,{common:{cubicBezierEaseInOut:b},self:{[ae("iconSize",m)]:P,[ae("fontSize",m)]:z,textColor:p,iconColor:k,extraTextColor:D}}=f.value;return{"--n-icon-size":P,"--n-font-size":z,"--n-bezier":b,"--n-text-color":p,"--n-icon-color":k,"--n-extra-text-color":D}}),S=a?Ke("empty",M(()=>{let m="";const{size:b}=e;return m+=b[0],m}),y,e):void 0;return{mergedClsPrefix:t,mergedRenderIcon:l,localizedDescription:M(()=>c.value||v.value.description),cssVars:a?void 0:y,themeClass:S==null?void 0:S.themeClass,onRender:S==null?void 0:S.onRender}},render(){const{$slots:e,mergedClsPrefix:t,onRender:a}=this;return a==null||a(),r("div",{class:[`${t}-empty`,this.themeClass],style:this.cssVars},this.showIcon?r("div",{class:`${t}-empty__icon`},e.icon?e.icon():r(bn,{clsPrefix:t},{default:this.mergedRenderIcon})):null,this.showDescription?r("div",{class:`${t}-empty__description`},e.default?e.default():this.localizedDescription):null,e.extra?r("div",{class:`${t}-empty__extra`},e.extra()):null)}}),Io={height:"calc(var(--n-option-height) * 7.6)",paddingTiny:"4px 0",paddingSmall:"4px 0",paddingMedium:"4px 0",paddingLarge:"4px 0",paddingHuge:"4px 0",optionPaddingTiny:"0 12px",optionPaddingSmall:"0 12px",optionPaddingMedium:"0 12px",optionPaddingLarge:"0 12px",optionPaddingHuge:"0 12px",loadingSize:"18px"};function Bo(e){const{borderRadius:t,popoverColor:a,textColor3:s,dividerColor:f,textColor2:v,primaryColorPressed:c,textColorDisabled:l,primaryColor:y,opacityDisabled:S,hoverColor:m,fontSizeTiny:b,fontSizeSmall:P,fontSizeMedium:z,fontSizeLarge:p,fontSizeHuge:k,heightTiny:D,heightSmall:T,heightMedium:F,heightLarge:I,heightHuge:V}=e;return Object.assign(Object.assign({},Io),{optionFontSizeTiny:b,optionFontSizeSmall:P,optionFontSizeMedium:z,optionFontSizeLarge:p,optionFontSizeHuge:k,optionHeightTiny:D,optionHeightSmall:T,optionHeightMedium:F,optionHeightLarge:I,optionHeightHuge:V,borderRadius:t,color:a,groupHeaderTextColor:s,actionDividerColor:f,optionTextColor:v,optionTextColorPressed:c,optionTextColorDisabled:l,optionTextColorActive:y,optionOpacityDisabled:S,optionCheckColor:y,optionColorPending:m,optionColorActive:"rgba(0, 0, 0, 0)",optionColorActivePending:m,actionTextColor:v,loadingColor:y})}const Fn=rn({name:"InternalSelectMenu",common:je,peers:{Scrollbar:eo,Empty:zn},self:Bo}),vn=he({name:"NBaseSelectGroupHeader",props:{clsPrefix:{type:String,required:!0},tmNode:{type:Object,required:!0}},setup(){const{renderLabelRef:e,renderOptionRef:t,labelFieldRef:a,nodePropsRef:s}=mn(an);return{labelField:a,nodeProps:s,renderLabel:e,renderOption:t}},render(){const{clsPrefix:e,renderLabel:t,renderOption:a,nodeProps:s,tmNode:{rawNode:f}}=this,v=s==null?void 0:s(f),c=t?t(f,!1):Fe(f[this.labelField],f,!1),l=r("div",Object.assign({},v,{class:[`${e}-base-select-group-header`,v==null?void 0:v.class]}),c);return f.render?f.render({node:l,option:f}):a?a({node:l,option:f,selected:!1}):l}});function Eo(e,t){return r(xn,{name:"fade-in-scale-up-transition"},{default:()=>e?r(bn,{clsPrefix:t,class:`${t}-base-select-option__check`},{default:()=>r(Ro)}):null})}const pn=he({name:"NBaseSelectOption",props:{clsPrefix:{type:String,required:!0},tmNode:{type:Object,required:!0}},setup(e){const{valueRef:t,pendingTmNodeRef:a,multipleRef:s,valueSetRef:f,renderLabelRef:v,renderOptionRef:c,labelFieldRef:l,valueFieldRef:y,showCheckmarkRef:S,nodePropsRef:m,handleOptionClick:b,handleOptionMouseEnter:P}=mn(an),z=Qe(()=>{const{value:T}=a;return T?e.tmNode.key===T.key:!1});function p(T){const{tmNode:F}=e;F.disabled||b(T,F)}function k(T){const{tmNode:F}=e;F.disabled||P(T,F)}function D(T){const{tmNode:F}=e,{value:I}=z;F.disabled||I||P(T,F)}return{multiple:s,isGrouped:Qe(()=>{const{tmNode:T}=e,{parent:F}=T;return F&&F.rawNode.type==="group"}),showCheckmark:S,nodeProps:m,isPending:z,isSelected:Qe(()=>{const{value:T}=t,{value:F}=s;if(T===null)return!1;const I=e.tmNode.rawNode[y.value];if(F){const{value:V}=f;return V.has(I)}else return T===I}),labelField:l,renderLabel:v,renderOption:c,handleMouseMove:D,handleMouseEnter:k,handleClick:p}},render(){const{clsPrefix:e,tmNode:{rawNode:t},isSelected:a,isPending:s,isGrouped:f,showCheckmark:v,nodeProps:c,renderOption:l,renderLabel:y,handleClick:S,handleMouseEnter:m,handleMouseMove:b}=this,P=Eo(a,e),z=y?[y(t,a),v&&P]:[Fe(t[this.labelField],t,a),v&&P],p=c==null?void 0:c(t),k=r("div",Object.assign({},p,{class:[`${e}-base-select-option`,t.class,p==null?void 0:p.class,{[`${e}-base-select-option--disabled`]:t.disabled,[`${e}-base-select-option--selected`]:a,[`${e}-base-select-option--grouped`]:f,[`${e}-base-select-option--pending`]:s,[`${e}-base-select-option--show-checkmark`]:v}],style:[(p==null?void 0:p.style)||"",t.style||""],onClick:en([S,p==null?void 0:p.onClick]),onMouseenter:en([m,p==null?void 0:p.onMouseenter]),onMousemove:en([b,p==null?void 0:p.onMousemove])}),r("div",{class:`${e}-base-select-option__content`},z));return t.render?t.render({node:k,option:t,selected:a}):l?l({node:k,option:t,selected:a}):k}}),Ao=R("base-select-menu",`
 line-height: 1.5;
 outline: none;
 z-index: 0;
 position: relative;
 border-radius: var(--n-border-radius);
 transition:
 background-color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier);
 background-color: var(--n-color);
`,[R("scrollbar",`
 max-height: var(--n-height);
 `),R("virtual-list",`
 max-height: var(--n-height);
 `),R("base-select-option",`
 min-height: var(--n-option-height);
 font-size: var(--n-option-font-size);
 display: flex;
 align-items: center;
 `,[_("content",`
 z-index: 1;
 white-space: nowrap;
 text-overflow: ellipsis;
 overflow: hidden;
 `)]),R("base-select-group-header",`
 min-height: var(--n-option-height);
 font-size: .93em;
 display: flex;
 align-items: center;
 `),R("base-select-menu-option-wrapper",`
 position: relative;
 width: 100%;
 `),_("loading, empty",`
 display: flex;
 padding: 12px 32px;
 flex: 1;
 justify-content: center;
 `),_("loading",`
 color: var(--n-loading-color);
 font-size: var(--n-loading-size);
 `),_("header",`
 padding: 8px var(--n-option-padding-left);
 font-size: var(--n-option-font-size);
 transition: 
 color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 border-bottom: 1px solid var(--n-action-divider-color);
 color: var(--n-action-text-color);
 `),_("action",`
 padding: 8px var(--n-option-padding-left);
 font-size: var(--n-option-font-size);
 transition: 
 color .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 border-top: 1px solid var(--n-action-divider-color);
 color: var(--n-action-text-color);
 `),R("base-select-group-header",`
 position: relative;
 cursor: default;
 padding: var(--n-option-padding);
 color: var(--n-group-header-text-color);
 `),R("base-select-option",`
 cursor: pointer;
 position: relative;
 padding: var(--n-option-padding);
 transition:
 color .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 box-sizing: border-box;
 color: var(--n-option-text-color);
 opacity: 1;
 `,[Z("show-checkmark",`
 padding-right: calc(var(--n-option-padding-right) + 20px);
 `),X("&::before",`
 content: "";
 position: absolute;
 left: 4px;
 right: 4px;
 top: 0;
 bottom: 0;
 border-radius: var(--n-border-radius);
 transition: background-color .3s var(--n-bezier);
 `),X("&:active",`
 color: var(--n-option-text-color-pressed);
 `),Z("grouped",`
 padding-left: calc(var(--n-option-padding-left) * 1.5);
 `),Z("pending",[X("&::before",`
 background-color: var(--n-option-color-pending);
 `)]),Z("selected",`
 color: var(--n-option-text-color-active);
 `,[X("&::before",`
 background-color: var(--n-option-color-active);
 `),Z("pending",[X("&::before",`
 background-color: var(--n-option-color-active-pending);
 `)])]),Z("disabled",`
 cursor: not-allowed;
 `,[on("selected",`
 color: var(--n-option-text-color-disabled);
 `),Z("selected",`
 opacity: var(--n-option-opacity-disabled);
 `)]),_("check",`
 font-size: 16px;
 position: absolute;
 right: calc(var(--n-option-padding-right) - 4px);
 top: calc(50% - 7px);
 color: var(--n-option-check-color);
 transition: color .3s var(--n-bezier);
 `,[wn({enterScale:"0.5"})])])]),Do=he({name:"InternalSelectMenu",props:Object.assign(Object.assign({},fe.props),{clsPrefix:{type:String,required:!0},scrollable:{type:Boolean,default:!0},treeMate:{type:Object,required:!0},multiple:Boolean,size:{type:String,default:"medium"},value:{type:[String,Number,Array],default:null},autoPending:Boolean,virtualScroll:{type:Boolean,default:!0},show:{type:Boolean,default:!0},labelField:{type:String,default:"label"},valueField:{type:String,default:"value"},loading:Boolean,focusable:Boolean,renderLabel:Function,renderOption:Function,nodeProps:Function,showCheckmark:{type:Boolean,default:!0},onMousedown:Function,onScroll:Function,onFocus:Function,onBlur:Function,onKeyup:Function,onKeydown:Function,onTabOut:Function,onMouseenter:Function,onMouseleave:Function,onResize:Function,resetMenuOnOptionsChange:{type:Boolean,default:!0},inlineThemeDisabled:Boolean,scrollbarProps:Object,onToggle:Function}),setup(e){const{mergedClsPrefixRef:t,mergedRtlRef:a,mergedComponentPropsRef:s}=We(e),f=Cn("InternalSelectMenu",a,t),v=fe("InternalSelectMenu","-internal-select-menu",Ao,Fn,e,j(e,"clsPrefix")),c=$(null),l=$(null),y=$(null),S=M(()=>e.treeMate.getFlattenedNodes()),m=M(()=>wo(S.value)),b=$(null);function P(){const{treeMate:i}=e;let h=null;const{value:A}=e;A===null?h=i.getFirstAvailableNode():(e.multiple?h=i.getNode((A||[])[(A||[]).length-1]):h=i.getNode(A),(!h||h.disabled)&&(h=i.getFirstAvailableNode())),ee(h||null)}function z(){const{value:i}=b;i&&!e.treeMate.getNode(i.key)&&(b.value=null)}let p;xe(()=>e.show,i=>{i?p=xe(()=>e.treeMate,()=>{e.resetMenuOnOptionsChange?(e.autoPending?P():z(),yn(le)):z()},{immediate:!0}):p==null||p()},{immediate:!0}),gn(()=>{p==null||p()});const k=M(()=>io(v.value.self[ae("optionHeight",e.size)])),D=M(()=>Ie(v.value.self[ae("padding",e.size)])),T=M(()=>e.multiple&&Array.isArray(e.value)?new Set(e.value):new Set),F=M(()=>{const i=S.value;return i&&i.length===0}),I=M(()=>{var i,h;return(h=(i=s==null?void 0:s.value)===null||i===void 0?void 0:i.Select)===null||h===void 0?void 0:h.renderEmpty});function V(i){const{onToggle:h}=e;h&&h(i)}function E(i){const{onScroll:h}=e;h&&h(i)}function B(i){var h;(h=y.value)===null||h===void 0||h.sync(),E(i)}function Y(){var i;(i=y.value)===null||i===void 0||i.sync()}function K(){const{value:i}=b;return i||null}function ie(i,h){h.disabled||ee(h,!1)}function se(i,h){h.disabled||V(h)}function W(i){var h;Be(i,"action")||(h=e.onKeyup)===null||h===void 0||h.call(e,i)}function U(i){var h;Be(i,"action")||(h=e.onKeydown)===null||h===void 0||h.call(e,i)}function L(i){var h;(h=e.onMousedown)===null||h===void 0||h.call(e,i),!e.focusable&&i.preventDefault()}function ve(){const{value:i}=b;i&&ee(i.getNext({loop:!0}),!0)}function we(){const{value:i}=b;i&&ee(i.getPrev({loop:!0}),!0)}function ee(i,h=!1){b.value=i,h&&le()}function le(){var i,h;const A=b.value;if(!A)return;const ne=m.value(A.key);ne!==null&&(e.virtualScroll?(i=l.value)===null||i===void 0||i.scrollTo({index:ne}):(h=y.value)===null||h===void 0||h.scrollTo({index:ne,elSize:k.value}))}function Re(i){var h,A;!((h=c.value)===null||h===void 0)&&h.contains(i.target)&&((A=e.onFocus)===null||A===void 0||A.call(e,i))}function de(i){var h,A;!((h=c.value)===null||h===void 0)&&h.contains(i.relatedTarget)||(A=e.onBlur)===null||A===void 0||A.call(e,i)}dn(an,{handleOptionMouseEnter:ie,handleOptionClick:se,valueSetRef:T,pendingTmNodeRef:b,nodePropsRef:j(e,"nodeProps"),showCheckmarkRef:j(e,"showCheckmark"),multipleRef:j(e,"multiple"),valueRef:j(e,"value"),renderLabelRef:j(e,"renderLabel"),renderOptionRef:j(e,"renderOption"),labelFieldRef:j(e,"labelField"),valueFieldRef:j(e,"valueField")}),dn(po,c),ln(()=>{const{value:i}=y;i&&i.sync()});const pe=M(()=>{const{size:i}=e,{common:{cubicBezierEaseInOut:h},self:{height:A,borderRadius:ne,color:Ce,groupHeaderTextColor:re,actionDividerColor:G,optionTextColorPressed:ye,optionTextColor:ce,optionTextColorDisabled:Te,optionTextColorActive:Pe,optionOpacityDisabled:Me,optionCheckColor:be,actionTextColor:me,optionColorPending:ke,optionColorActive:_e,loadingColor:$e,loadingSize:Se,optionColorActivePending:Oe,[ae("optionFontSize",i)]:Q,[ae("optionHeight",i)]:o,[ae("optionPadding",i)]:u}}=v.value;return{"--n-height":A,"--n-action-divider-color":G,"--n-action-text-color":me,"--n-bezier":h,"--n-border-radius":ne,"--n-color":Ce,"--n-option-font-size":Q,"--n-group-header-text-color":re,"--n-option-check-color":be,"--n-option-color-pending":ke,"--n-option-color-active":_e,"--n-option-color-active-pending":Oe,"--n-option-height":o,"--n-option-opacity-disabled":Me,"--n-option-text-color":ce,"--n-option-text-color-active":Pe,"--n-option-text-color-disabled":Te,"--n-option-text-color-pressed":ye,"--n-option-padding":u,"--n-option-padding-left":Ie(u,"left"),"--n-option-padding-right":Ie(u,"right"),"--n-loading-color":$e,"--n-loading-size":Se}}),{inlineThemeDisabled:q}=e,J=q?Ke("internal-select-menu",M(()=>e.size[0]),pe,e):void 0,ge={selfRef:c,next:ve,prev:we,getPendingTmNode:K};return On(c,e.onResize),Object.assign({mergedTheme:v,mergedClsPrefix:t,rtlEnabled:f,virtualListRef:l,scrollbarRef:y,itemSize:k,padding:D,flattenedNodes:S,empty:F,mergedRenderEmpty:I,virtualListContainer(){const{value:i}=l;return i==null?void 0:i.listElRef},virtualListContent(){const{value:i}=l;return i==null?void 0:i.itemsElRef},doScroll:E,handleFocusin:Re,handleFocusout:de,handleKeyUp:W,handleKeyDown:U,handleMouseDown:L,handleVirtualListResize:Y,handleVirtualListScroll:B,cssVars:q?void 0:pe,themeClass:J==null?void 0:J.themeClass,onRender:J==null?void 0:J.onRender},ge)},render(){const{$slots:e,virtualScroll:t,clsPrefix:a,mergedTheme:s,themeClass:f,onRender:v}=this;return v==null||v(),r("div",{ref:"selfRef",tabindex:this.focusable?0:-1,class:[`${a}-base-select-menu`,`${a}-base-select-menu--${this.size}-size`,this.rtlEnabled&&`${a}-base-select-menu--rtl`,f,this.multiple&&`${a}-base-select-menu--multiple`],style:this.cssVars,onFocusin:this.handleFocusin,onFocusout:this.handleFocusout,onKeyup:this.handleKeyUp,onKeydown:this.handleKeyDown,onMousedown:this.handleMouseDown,onMouseenter:this.onMouseenter,onMouseleave:this.onMouseleave},sn(e.header,c=>c&&r("div",{class:`${a}-base-select-menu__header`,"data-header":!0,key:"header"},c)),this.loading?r("div",{class:`${a}-base-select-menu__loading`},r(no,{clsPrefix:a,strokeWidth:20})):this.empty?r("div",{class:`${a}-base-select-menu__empty`,"data-empty":!0},to(e.empty,()=>{var c;return[((c=this.mergedRenderEmpty)===null||c===void 0?void 0:c.call(this))||r($o,{theme:s.peers.Empty,themeOverrides:s.peerOverrides.Empty,size:this.size})]})):r(oo,Object.assign({ref:"scrollbarRef",theme:s.peers.Scrollbar,themeOverrides:s.peerOverrides.Scrollbar,scrollable:this.scrollable,container:t?this.virtualListContainer:void 0,content:t?this.virtualListContent:void 0,onScroll:t?void 0:this.doScroll},this.scrollbarProps),{default:()=>t?r(zo,{ref:"virtualListRef",class:`${a}-virtual-list`,items:this.flattenedNodes,itemSize:this.itemSize,showScrollbar:!1,paddingTop:this.padding.top,paddingBottom:this.padding.bottom,onResize:this.handleVirtualListResize,onScroll:this.handleVirtualListScroll,itemResizable:!0},{default:({item:c})=>c.isGroup?r(vn,{key:c.key,clsPrefix:a,tmNode:c}):c.ignored?null:r(pn,{clsPrefix:a,key:c.key,tmNode:c})}):r("div",{class:`${a}-base-select-menu-option-wrapper`,style:{paddingTop:this.padding.top,paddingBottom:this.padding.bottom}},this.flattenedNodes.map(c=>c.isGroup?r(vn,{key:c.key,clsPrefix:a,tmNode:c}):r(pn,{clsPrefix:a,key:c.key,tmNode:c})))}),sn(e.action,c=>c&&[r("div",{class:`${a}-base-select-menu__action`,"data-action":!0,key:"action"},c),r(Fo,{onFocus:this.onTabOut,key:"focus-detector"})]))}}),Lo={paddingSingle:"0 26px 0 12px",paddingMultiple:"3px 26px 0 12px",clearSize:"16px",arrowSize:"16px"};function No(e){const{borderRadius:t,textColor2:a,textColorDisabled:s,inputColor:f,inputColorDisabled:v,primaryColor:c,primaryColorHover:l,warningColor:y,warningColorHover:S,errorColor:m,errorColorHover:b,borderColor:P,iconColor:z,iconColorDisabled:p,clearColor:k,clearColorHover:D,clearColorPressed:T,placeholderColor:F,placeholderColorDisabled:I,fontSizeTiny:V,fontSizeSmall:E,fontSizeMedium:B,fontSizeLarge:Y,heightTiny:K,heightSmall:ie,heightMedium:se,heightLarge:W,fontWeight:U}=e;return Object.assign(Object.assign({},Lo),{fontSizeTiny:V,fontSizeSmall:E,fontSizeMedium:B,fontSizeLarge:Y,heightTiny:K,heightSmall:ie,heightMedium:se,heightLarge:W,borderRadius:t,fontWeight:U,textColor:a,textColorDisabled:s,placeholderColor:F,placeholderColorDisabled:I,color:f,colorDisabled:v,colorActive:f,border:`1px solid ${P}`,borderHover:`1px solid ${l}`,borderActive:`1px solid ${c}`,borderFocus:`1px solid ${l}`,boxShadowHover:"none",boxShadowActive:`0 0 0 2px ${ze(c,{alpha:.2})}`,boxShadowFocus:`0 0 0 2px ${ze(c,{alpha:.2})}`,caretColor:c,arrowColor:z,arrowColorDisabled:p,loadingColor:c,borderWarning:`1px solid ${y}`,borderHoverWarning:`1px solid ${S}`,borderActiveWarning:`1px solid ${y}`,borderFocusWarning:`1px solid ${S}`,boxShadowHoverWarning:"none",boxShadowActiveWarning:`0 0 0 2px ${ze(y,{alpha:.2})}`,boxShadowFocusWarning:`0 0 0 2px ${ze(y,{alpha:.2})}`,colorActiveWarning:f,caretColorWarning:y,borderError:`1px solid ${m}`,borderHoverError:`1px solid ${b}`,borderActiveError:`1px solid ${m}`,borderFocusError:`1px solid ${b}`,boxShadowHoverError:"none",boxShadowActiveError:`0 0 0 2px ${ze(m,{alpha:.2})}`,boxShadowFocusError:`0 0 0 2px ${ze(m,{alpha:.2})}`,colorActiveError:f,caretColorError:m,clearColor:k,clearColorHover:D,clearColorPressed:T})}const Rn=rn({name:"InternalSelection",common:je,peers:{Popover:yo},self:No}),Ho=X([R("base-selection",`
 --n-padding-single: var(--n-padding-single-top) var(--n-padding-single-right) var(--n-padding-single-bottom) var(--n-padding-single-left);
 --n-padding-multiple: var(--n-padding-multiple-top) var(--n-padding-multiple-right) var(--n-padding-multiple-bottom) var(--n-padding-multiple-left);
 position: relative;
 z-index: auto;
 box-shadow: none;
 width: 100%;
 max-width: 100%;
 display: inline-block;
 vertical-align: bottom;
 border-radius: var(--n-border-radius);
 min-height: var(--n-height);
 line-height: 1.5;
 font-size: var(--n-font-size);
 `,[R("base-loading",`
 color: var(--n-loading-color);
 `),R("base-selection-tags","min-height: var(--n-height);"),_("border, state-border",`
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 pointer-events: none;
 border: var(--n-border);
 border-radius: inherit;
 transition:
 box-shadow .3s var(--n-bezier),
 border-color .3s var(--n-bezier);
 `),_("state-border",`
 z-index: 1;
 border-color: #0000;
 `),R("base-suffix",`
 cursor: pointer;
 position: absolute;
 top: 50%;
 transform: translateY(-50%);
 right: 10px;
 `,[_("arrow",`
 font-size: var(--n-arrow-size);
 color: var(--n-arrow-color);
 transition: color .3s var(--n-bezier);
 `)]),R("base-selection-overlay",`
 display: flex;
 align-items: center;
 white-space: nowrap;
 pointer-events: none;
 position: absolute;
 top: 0;
 right: 0;
 bottom: 0;
 left: 0;
 padding: var(--n-padding-single);
 transition: color .3s var(--n-bezier);
 `,[_("wrapper",`
 flex-basis: 0;
 flex-grow: 1;
 overflow: hidden;
 text-overflow: ellipsis;
 `)]),R("base-selection-placeholder",`
 color: var(--n-placeholder-color);
 `,[_("inner",`
 max-width: 100%;
 overflow: hidden;
 `)]),R("base-selection-tags",`
 cursor: pointer;
 outline: none;
 box-sizing: border-box;
 position: relative;
 z-index: auto;
 display: flex;
 padding: var(--n-padding-multiple);
 flex-wrap: wrap;
 align-items: center;
 width: 100%;
 vertical-align: bottom;
 background-color: var(--n-color);
 border-radius: inherit;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 `),R("base-selection-label",`
 height: var(--n-height);
 display: inline-flex;
 width: 100%;
 vertical-align: bottom;
 cursor: pointer;
 outline: none;
 z-index: auto;
 box-sizing: border-box;
 position: relative;
 transition:
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 border-radius: inherit;
 background-color: var(--n-color);
 align-items: center;
 `,[R("base-selection-input",`
 font-size: inherit;
 line-height: inherit;
 outline: none;
 cursor: pointer;
 box-sizing: border-box;
 border:none;
 width: 100%;
 padding: var(--n-padding-single);
 background-color: #0000;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 caret-color: var(--n-caret-color);
 `,[_("content",`
 text-overflow: ellipsis;
 overflow: hidden;
 white-space: nowrap; 
 `)]),_("render-label",`
 color: var(--n-text-color);
 `)]),on("disabled",[X("&:hover",[_("state-border",`
 box-shadow: var(--n-box-shadow-hover);
 border: var(--n-border-hover);
 `)]),Z("focus",[_("state-border",`
 box-shadow: var(--n-box-shadow-focus);
 border: var(--n-border-focus);
 `)]),Z("active",[_("state-border",`
 box-shadow: var(--n-box-shadow-active);
 border: var(--n-border-active);
 `),R("base-selection-label","background-color: var(--n-color-active);"),R("base-selection-tags","background-color: var(--n-color-active);")])]),Z("disabled","cursor: not-allowed;",[_("arrow",`
 color: var(--n-arrow-color-disabled);
 `),R("base-selection-label",`
 cursor: not-allowed;
 background-color: var(--n-color-disabled);
 `,[R("base-selection-input",`
 cursor: not-allowed;
 color: var(--n-text-color-disabled);
 `),_("render-label",`
 color: var(--n-text-color-disabled);
 `)]),R("base-selection-tags",`
 cursor: not-allowed;
 background-color: var(--n-color-disabled);
 `),R("base-selection-placeholder",`
 cursor: not-allowed;
 color: var(--n-placeholder-color-disabled);
 `)]),R("base-selection-input-tag",`
 height: calc(var(--n-height) - 6px);
 line-height: calc(var(--n-height) - 6px);
 outline: none;
 display: none;
 position: relative;
 margin-bottom: 3px;
 max-width: 100%;
 vertical-align: bottom;
 `,[_("input",`
 font-size: inherit;
 font-family: inherit;
 min-width: 1px;
 padding: 0;
 background-color: #0000;
 outline: none;
 border: none;
 max-width: 100%;
 overflow: hidden;
 width: 1em;
 line-height: inherit;
 cursor: pointer;
 color: var(--n-text-color);
 caret-color: var(--n-caret-color);
 `),_("mirror",`
 position: absolute;
 left: 0;
 top: 0;
 white-space: pre;
 visibility: hidden;
 user-select: none;
 -webkit-user-select: none;
 opacity: 0;
 `)]),["warning","error"].map(e=>Z(`${e}-status`,[_("state-border",`border: var(--n-border-${e});`),on("disabled",[X("&:hover",[_("state-border",`
 box-shadow: var(--n-box-shadow-hover-${e});
 border: var(--n-border-hover-${e});
 `)]),Z("active",[_("state-border",`
 box-shadow: var(--n-box-shadow-active-${e});
 border: var(--n-border-active-${e});
 `),R("base-selection-label",`background-color: var(--n-color-active-${e});`),R("base-selection-tags",`background-color: var(--n-color-active-${e});`)]),Z("focus",[_("state-border",`
 box-shadow: var(--n-box-shadow-focus-${e});
 border: var(--n-border-focus-${e});
 `)])])]))]),R("base-selection-popover",`
 margin-bottom: -3px;
 display: flex;
 flex-wrap: wrap;
 margin-right: -8px;
 `),R("base-selection-tag-wrapper",`
 max-width: 100%;
 display: inline-flex;
 padding: 0 7px 3px 0;
 `,[X("&:last-child","padding-right: 0;"),R("tag",`
 font-size: 14px;
 max-width: 100%;
 `,[_("content",`
 line-height: 1.25;
 text-overflow: ellipsis;
 overflow: hidden;
 `)])])]),Vo=he({name:"InternalSelection",props:Object.assign(Object.assign({},fe.props),{clsPrefix:{type:String,required:!0},bordered:{type:Boolean,default:void 0},active:Boolean,pattern:{type:String,default:""},placeholder:String,selectedOption:{type:Object,default:null},selectedOptions:{type:Array,default:null},labelField:{type:String,default:"label"},valueField:{type:String,default:"value"},multiple:Boolean,filterable:Boolean,clearable:Boolean,disabled:Boolean,size:{type:String,default:"medium"},loading:Boolean,autofocus:Boolean,showArrow:{type:Boolean,default:!0},inputProps:Object,focused:Boolean,renderTag:Function,onKeydown:Function,onClick:Function,onBlur:Function,onFocus:Function,onDeleteOption:Function,maxTagCount:[String,Number],ellipsisTagPopoverProps:Object,onClear:Function,onPatternInput:Function,onPatternFocus:Function,onPatternBlur:Function,renderLabel:Function,status:String,inlineThemeDisabled:Boolean,ignoreComposition:{type:Boolean,default:!0},onResize:Function}),setup(e){const{mergedClsPrefixRef:t,mergedRtlRef:a}=We(e),s=Cn("InternalSelection",a,t),f=$(null),v=$(null),c=$(null),l=$(null),y=$(null),S=$(null),m=$(null),b=$(null),P=$(null),z=$(null),p=$(!1),k=$(!1),D=$(!1),T=fe("InternalSelection","-internal-selection",Ho,Rn,e,j(e,"clsPrefix")),F=M(()=>e.clearable&&!e.disabled&&(D.value||e.active)),I=M(()=>e.selectedOption?e.renderTag?e.renderTag({option:e.selectedOption,handleClose:()=>{}}):e.renderLabel?e.renderLabel(e.selectedOption,!0):Fe(e.selectedOption[e.labelField],e.selectedOption,!0):e.placeholder),V=M(()=>{const o=e.selectedOption;if(o)return o[e.labelField]}),E=M(()=>e.multiple?!!(Array.isArray(e.selectedOptions)&&e.selectedOptions.length):e.selectedOption!==null);function B(){var o;const{value:u}=f;if(u){const{value:N}=v;N&&(N.style.width=`${u.offsetWidth}px`,e.maxTagCount!=="responsive"&&((o=P.value)===null||o===void 0||o.sync({showAllItemsBeforeCalculate:!1})))}}function Y(){const{value:o}=z;o&&(o.style.display="none")}function K(){const{value:o}=z;o&&(o.style.display="inline-block")}xe(j(e,"active"),o=>{o||Y()}),xe(j(e,"pattern"),()=>{e.multiple&&yn(B)});function ie(o){const{onFocus:u}=e;u&&u(o)}function se(o){const{onBlur:u}=e;u&&u(o)}function W(o){const{onDeleteOption:u}=e;u&&u(o)}function U(o){const{onClear:u}=e;u&&u(o)}function L(o){const{onPatternInput:u}=e;u&&u(o)}function ve(o){var u;(!o.relatedTarget||!(!((u=c.value)===null||u===void 0)&&u.contains(o.relatedTarget)))&&ie(o)}function we(o){var u;!((u=c.value)===null||u===void 0)&&u.contains(o.relatedTarget)||se(o)}function ee(o){U(o)}function le(){D.value=!0}function Re(){D.value=!1}function de(o){!e.active||!e.filterable||o.target!==v.value&&o.preventDefault()}function pe(o){W(o)}const q=$(!1);function J(o){if(o.key==="Backspace"&&!q.value&&!e.pattern.length){const{selectedOptions:u}=e;u!=null&&u.length&&pe(u[u.length-1])}}let ge=null;function i(o){const{value:u}=f;if(u){const N=o.target.value;u.textContent=N,B()}e.ignoreComposition&&q.value?ge=o:L(o)}function h(){q.value=!0}function A(){q.value=!1,e.ignoreComposition&&L(ge),ge=null}function ne(o){var u;k.value=!0,(u=e.onPatternFocus)===null||u===void 0||u.call(e,o)}function Ce(o){var u;k.value=!1,(u=e.onPatternBlur)===null||u===void 0||u.call(e,o)}function re(){var o,u;if(e.filterable)k.value=!1,(o=S.value)===null||o===void 0||o.blur(),(u=v.value)===null||u===void 0||u.blur();else if(e.multiple){const{value:N}=l;N==null||N.blur()}else{const{value:N}=y;N==null||N.blur()}}function G(){var o,u,N;e.filterable?(k.value=!1,(o=S.value)===null||o===void 0||o.focus()):e.multiple?(u=l.value)===null||u===void 0||u.focus():(N=y.value)===null||N===void 0||N.focus()}function ye(){const{value:o}=v;o&&(K(),o.focus())}function ce(){const{value:o}=v;o&&o.blur()}function Te(o){const{value:u}=m;u&&u.setTextContent(`+${o}`)}function Pe(){const{value:o}=b;return o}function Me(){return v.value}let be=null;function me(){be!==null&&window.clearTimeout(be)}function ke(){e.active||(me(),be=window.setTimeout(()=>{E.value&&(p.value=!0)},100))}function _e(){me()}function $e(o){o||(me(),p.value=!1)}xe(E,o=>{o||(p.value=!1)}),ln(()=>{ao(()=>{const o=S.value;o&&(e.disabled?o.removeAttribute("tabindex"):o.tabIndex=k.value?-1:0)})}),On(c,e.onResize);const{inlineThemeDisabled:Se}=e,Oe=M(()=>{const{size:o}=e,{common:{cubicBezierEaseInOut:u},self:{fontWeight:N,borderRadius:Ue,color:qe,placeholderColor:Ge,textColor:Ee,paddingSingle:Ae,paddingMultiple:De,caretColor:Ze,colorDisabled:Ye,textColorDisabled:Le,placeholderColorDisabled:ue,colorActive:n,boxShadowFocus:d,boxShadowActive:g,boxShadowHover:C,border:x,borderFocus:w,borderHover:O,borderActive:H,arrowColor:oe,arrowColorDisabled:Pn,loadingColor:Mn,colorActiveWarning:kn,boxShadowFocusWarning:_n,boxShadowActiveWarning:$n,boxShadowHoverWarning:In,borderWarning:Bn,borderFocusWarning:En,borderHoverWarning:An,borderActiveWarning:Dn,colorActiveError:Ln,boxShadowFocusError:Nn,boxShadowActiveError:Hn,boxShadowHoverError:Vn,borderError:jn,borderFocusError:Wn,borderHoverError:Kn,borderActiveError:Un,clearColor:qn,clearColorHover:Gn,clearColorPressed:Zn,clearSize:Yn,arrowSize:Jn,[ae("height",o)]:Qn,[ae("fontSize",o)]:Xn}}=T.value,Ne=Ie(Ae),He=Ie(De);return{"--n-bezier":u,"--n-border":x,"--n-border-active":H,"--n-border-focus":w,"--n-border-hover":O,"--n-border-radius":Ue,"--n-box-shadow-active":g,"--n-box-shadow-focus":d,"--n-box-shadow-hover":C,"--n-caret-color":Ze,"--n-color":qe,"--n-color-active":n,"--n-color-disabled":Ye,"--n-font-size":Xn,"--n-height":Qn,"--n-padding-single-top":Ne.top,"--n-padding-multiple-top":He.top,"--n-padding-single-right":Ne.right,"--n-padding-multiple-right":He.right,"--n-padding-single-left":Ne.left,"--n-padding-multiple-left":He.left,"--n-padding-single-bottom":Ne.bottom,"--n-padding-multiple-bottom":He.bottom,"--n-placeholder-color":Ge,"--n-placeholder-color-disabled":ue,"--n-text-color":Ee,"--n-text-color-disabled":Le,"--n-arrow-color":oe,"--n-arrow-color-disabled":Pn,"--n-loading-color":Mn,"--n-color-active-warning":kn,"--n-box-shadow-focus-warning":_n,"--n-box-shadow-active-warning":$n,"--n-box-shadow-hover-warning":In,"--n-border-warning":Bn,"--n-border-focus-warning":En,"--n-border-hover-warning":An,"--n-border-active-warning":Dn,"--n-color-active-error":Ln,"--n-box-shadow-focus-error":Nn,"--n-box-shadow-active-error":Hn,"--n-box-shadow-hover-error":Vn,"--n-border-error":jn,"--n-border-focus-error":Wn,"--n-border-hover-error":Kn,"--n-border-active-error":Un,"--n-clear-size":Yn,"--n-clear-color":qn,"--n-clear-color-hover":Gn,"--n-clear-color-pressed":Zn,"--n-arrow-size":Jn,"--n-font-weight":N}}),Q=Se?Ke("internal-selection",M(()=>e.size[0]),Oe,e):void 0;return{mergedTheme:T,mergedClearable:F,mergedClsPrefix:t,rtlEnabled:s,patternInputFocused:k,filterablePlaceholder:I,label:V,selected:E,showTagsPanel:p,isComposing:q,counterRef:m,counterWrapperRef:b,patternInputMirrorRef:f,patternInputRef:v,selfRef:c,multipleElRef:l,singleElRef:y,patternInputWrapperRef:S,overflowRef:P,inputTagElRef:z,handleMouseDown:de,handleFocusin:ve,handleClear:ee,handleMouseEnter:le,handleMouseLeave:Re,handleDeleteOption:pe,handlePatternKeyDown:J,handlePatternInputInput:i,handlePatternInputBlur:Ce,handlePatternInputFocus:ne,handleMouseEnterCounter:ke,handleMouseLeaveCounter:_e,handleFocusout:we,handleCompositionEnd:A,handleCompositionStart:h,onPopoverUpdateShow:$e,focus:G,focusInput:ye,blur:re,blurInput:ce,updateCounter:Te,getCounter:Pe,getTail:Me,renderLabel:e.renderLabel,cssVars:Se?void 0:Oe,themeClass:Q==null?void 0:Q.themeClass,onRender:Q==null?void 0:Q.onRender}},render(){const{status:e,multiple:t,size:a,disabled:s,filterable:f,maxTagCount:v,bordered:c,clsPrefix:l,ellipsisTagPopoverProps:y,onRender:S,renderTag:m,renderLabel:b}=this;S==null||S();const P=v==="responsive",z=typeof v=="number",p=P||z,k=r(lo,null,{default:()=>r(xo,{clsPrefix:l,loading:this.loading,showArrow:this.showArrow,showClear:this.mergedClearable&&this.selected,onClear:this.handleClear},{default:()=>{var T,F;return(F=(T=this.$slots).arrow)===null||F===void 0?void 0:F.call(T)}})});let D;if(t){const{labelField:T}=this,F=L=>r("div",{class:`${l}-base-selection-tag-wrapper`,key:L.value},m?m({option:L,handleClose:()=>{this.handleDeleteOption(L)}}):r(Xe,{size:a,closable:!L.disabled,disabled:s,onClose:()=>{this.handleDeleteOption(L)},internalCloseIsButtonTag:!1,internalCloseFocusable:!1},{default:()=>b?b(L,!0):Fe(L[T],L,!0)})),I=()=>(z?this.selectedOptions.slice(0,v):this.selectedOptions).map(F),V=f?r("div",{class:`${l}-base-selection-input-tag`,ref:"inputTagElRef",key:"__input-tag__"},r("input",Object.assign({},this.inputProps,{ref:"patternInputRef",tabindex:-1,disabled:s,value:this.pattern,autofocus:this.autofocus,class:`${l}-base-selection-input-tag__input`,onBlur:this.handlePatternInputBlur,onFocus:this.handlePatternInputFocus,onKeydown:this.handlePatternKeyDown,onInput:this.handlePatternInputInput,onCompositionstart:this.handleCompositionStart,onCompositionend:this.handleCompositionEnd})),r("span",{ref:"patternInputMirrorRef",class:`${l}-base-selection-input-tag__mirror`},this.pattern)):null,E=P?()=>r("div",{class:`${l}-base-selection-tag-wrapper`,ref:"counterWrapperRef"},r(Xe,{size:a,ref:"counterRef",onMouseenter:this.handleMouseEnterCounter,onMouseleave:this.handleMouseLeaveCounter,disabled:s})):void 0;let B;if(z){const L=this.selectedOptions.length-v;L>0&&(B=r("div",{class:`${l}-base-selection-tag-wrapper`,key:"__counter__"},r(Xe,{size:a,ref:"counterRef",onMouseenter:this.handleMouseEnterCounter,disabled:s},{default:()=>`+${L}`})))}const Y=P?f?r(un,{ref:"overflowRef",updateCounter:this.updateCounter,getCounter:this.getCounter,getTail:this.getTail,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:I,counter:E,tail:()=>V}):r(un,{ref:"overflowRef",updateCounter:this.updateCounter,getCounter:this.getCounter,style:{width:"100%",display:"flex",overflow:"hidden"}},{default:I,counter:E}):z&&B?I().concat(B):I(),K=p?()=>r("div",{class:`${l}-base-selection-popover`},P?I():this.selectedOptions.map(F)):void 0,ie=p?Object.assign({show:this.showTagsPanel,trigger:"hover",overlap:!0,placement:"top",width:"trigger",onUpdateShow:this.onPopoverUpdateShow,theme:this.mergedTheme.peers.Popover,themeOverrides:this.mergedTheme.peerOverrides.Popover},y):null,W=(this.selected?!1:this.active?!this.pattern&&!this.isComposing:!0)?r("div",{class:`${l}-base-selection-placeholder ${l}-base-selection-overlay`},r("div",{class:`${l}-base-selection-placeholder__inner`},this.placeholder)):null,U=f?r("div",{ref:"patternInputWrapperRef",class:`${l}-base-selection-tags`},Y,P?null:V,k):r("div",{ref:"multipleElRef",class:`${l}-base-selection-tags`,tabindex:s?void 0:0},Y,k);D=r(ro,null,p?r(So,Object.assign({},ie,{scrollable:!0,style:"max-height: calc(var(--v-target-height) * 6.6);"}),{trigger:()=>U,default:K}):U,W)}else if(f){const T=this.pattern||this.isComposing,F=this.active?!T:!this.selected,I=this.active?!1:this.selected;D=r("div",{ref:"patternInputWrapperRef",class:`${l}-base-selection-label`,title:this.patternInputFocused?void 0:hn(this.label)},r("input",Object.assign({},this.inputProps,{ref:"patternInputRef",class:`${l}-base-selection-input`,value:this.active?this.pattern:"",placeholder:"",readonly:s,disabled:s,tabindex:-1,autofocus:this.autofocus,onFocus:this.handlePatternInputFocus,onBlur:this.handlePatternInputBlur,onInput:this.handlePatternInputInput,onCompositionstart:this.handleCompositionStart,onCompositionend:this.handleCompositionEnd})),I?r("div",{class:`${l}-base-selection-label__render-label ${l}-base-selection-overlay`,key:"input"},r("div",{class:`${l}-base-selection-overlay__wrapper`},m?m({option:this.selectedOption,handleClose:()=>{}}):b?b(this.selectedOption,!0):Fe(this.label,this.selectedOption,!0))):null,F?r("div",{class:`${l}-base-selection-placeholder ${l}-base-selection-overlay`,key:"placeholder"},r("div",{class:`${l}-base-selection-overlay__wrapper`},this.filterablePlaceholder)):null,k)}else D=r("div",{ref:"singleElRef",class:`${l}-base-selection-label`,tabindex:this.disabled?void 0:0},this.label!==void 0?r("div",{class:`${l}-base-selection-input`,title:hn(this.label),key:"input"},r("div",{class:`${l}-base-selection-input__content`},m?m({option:this.selectedOption,handleClose:()=>{}}):b?b(this.selectedOption,!0):Fe(this.label,this.selectedOption,!0))):r("div",{class:`${l}-base-selection-placeholder ${l}-base-selection-overlay`,key:"placeholder"},r("div",{class:`${l}-base-selection-placeholder__inner`},this.placeholder)),k);return r("div",{ref:"selfRef",class:[`${l}-base-selection`,this.rtlEnabled&&`${l}-base-selection--rtl`,this.themeClass,e&&`${l}-base-selection--${e}-status`,{[`${l}-base-selection--active`]:this.active,[`${l}-base-selection--selected`]:this.selected||this.active&&this.pattern,[`${l}-base-selection--disabled`]:this.disabled,[`${l}-base-selection--multiple`]:this.multiple,[`${l}-base-selection--focus`]:this.focused}],style:this.cssVars,onClick:this.onClick,onMouseenter:this.handleMouseEnter,onMouseleave:this.handleMouseLeave,onKeydown:this.onKeydown,onFocusin:this.handleFocusin,onFocusout:this.handleFocusout,onMousedown:this.handleMouseDown},D,c?r("div",{class:`${l}-base-selection__border`}):null,c?r("div",{class:`${l}-base-selection__state-border`}):null)}});function Ve(e){return e.type==="group"}function Tn(e){return e.type==="ignored"}function nn(e,t){try{return!!(1+t.toString().toLowerCase().indexOf(e.trim().toLowerCase()))}catch{return!1}}function jo(e,t){return{getIsGroup:Ve,getIgnored:Tn,getKey(s){return Ve(s)?s.name||s.key||"key-required":s[e]},getChildren(s){return s[t]}}}function Wo(e,t,a,s){if(!t)return e;function f(v){if(!Array.isArray(v))return[];const c=[];for(const l of v)if(Ve(l)){const y=f(l[s]);y.length&&c.push(Object.assign({},l,{[s]:y}))}else{if(Tn(l))continue;t(a,l)&&c.push(l)}return c}return f(e)}function Ko(e,t,a){const s=new Map;return e.forEach(f=>{Ve(f)?f[a].forEach(v=>{s.set(v[t],v)}):s.set(f[t],f)}),s}function Uo(e){const{boxShadow2:t}=e;return{menuBoxShadow:t}}const qo=rn({name:"Select",common:je,peers:{InternalSelection:Rn,InternalSelectMenu:Fn},self:Uo}),Go=X([R("select",`
 z-index: auto;
 outline: none;
 width: 100%;
 position: relative;
 font-weight: var(--n-font-weight);
 `),R("select-menu",`
 margin: 4px 0;
 box-shadow: var(--n-menu-box-shadow);
 `,[wn({originalTransition:"background-color .3s var(--n-bezier), box-shadow .3s var(--n-bezier)"})])]),Zo=Object.assign(Object.assign({},fe.props),{to:tn.propTo,bordered:{type:Boolean,default:void 0},clearable:Boolean,clearCreatedOptionsOnClear:{type:Boolean,default:!0},clearFilterAfterSelect:{type:Boolean,default:!0},options:{type:Array,default:()=>[]},defaultValue:{type:[String,Number,Array],default:null},keyboard:{type:Boolean,default:!0},value:[String,Number,Array],placeholder:String,menuProps:Object,multiple:Boolean,size:String,menuSize:{type:String},filterable:Boolean,disabled:{type:Boolean,default:void 0},remote:Boolean,loading:Boolean,filter:Function,placement:{type:String,default:"bottom-start"},widthMode:{type:String,default:"trigger"},tag:Boolean,onCreate:Function,fallbackOption:{type:[Function,Boolean],default:void 0},show:{type:Boolean,default:void 0},showArrow:{type:Boolean,default:!0},maxTagCount:[Number,String],ellipsisTagPopoverProps:Object,consistentMenuWidth:{type:Boolean,default:!0},virtualScroll:{type:Boolean,default:!0},labelField:{type:String,default:"label"},valueField:{type:String,default:"value"},childrenField:{type:String,default:"children"},renderLabel:Function,renderOption:Function,renderTag:Function,"onUpdate:value":[Function,Array],inputProps:Object,nodeProps:Function,ignoreComposition:{type:Boolean,default:!0},showOnFocus:Boolean,onUpdateValue:[Function,Array],onBlur:[Function,Array],onClear:[Function,Array],onFocus:[Function,Array],onScroll:[Function,Array],onSearch:[Function,Array],onUpdateShow:[Function,Array],"onUpdate:show":[Function,Array],displayDirective:{type:String,default:"show"},resetMenuOnOptionsChange:{type:Boolean,default:!0},status:String,showCheckmark:{type:Boolean,default:!0},scrollbarProps:Object,onChange:[Function,Array],items:Array}),rt=he({name:"Select",props:Zo,slots:Object,setup(e){const{mergedClsPrefixRef:t,mergedBorderedRef:a,namespaceRef:s,inlineThemeDisabled:f,mergedComponentPropsRef:v}=We(e),c=fe("Select","-select",Go,qo,e,t),l=$(e.defaultValue),y=j(e,"value"),S=fn(y,l),m=$(!1),b=$(""),P=Oo(e,["items","options"]),z=$([]),p=$([]),k=M(()=>p.value.concat(z.value).concat(P.value)),D=M(()=>{const{filter:n}=e;if(n)return n;const{labelField:d,valueField:g}=e;return(C,x)=>{if(!x)return!1;const w=x[d];if(typeof w=="string")return nn(C,w);const O=x[g];return typeof O=="string"?nn(C,O):typeof O=="number"?nn(C,String(O)):!1}}),T=M(()=>{if(e.remote)return P.value;{const{value:n}=k,{value:d}=b;return!d.length||!e.filterable?n:Wo(n,D.value,d,e.childrenField)}}),F=M(()=>{const{valueField:n,childrenField:d}=e,g=jo(n,d);return Co(T.value,g)}),I=M(()=>Ko(k.value,e.valueField,e.childrenField)),V=$(!1),E=fn(j(e,"show"),V),B=$(null),Y=$(null),K=$(null),{localeRef:ie}=Sn("Select"),se=M(()=>{var n;return(n=e.placeholder)!==null&&n!==void 0?n:ie.value.placeholder}),W=[],U=$(new Map),L=M(()=>{const{fallbackOption:n}=e;if(n===void 0){const{labelField:d,valueField:g}=e;return C=>({[d]:String(C),[g]:C})}return n===!1?!1:d=>Object.assign(n(d),{value:d})});function ve(n){const d=e.remote,{value:g}=U,{value:C}=I,{value:x}=L,w=[];return n.forEach(O=>{if(C.has(O))w.push(C.get(O));else if(d&&g.has(O))w.push(g.get(O));else if(x){const H=x(O);H&&w.push(H)}}),w}const we=M(()=>{if(e.multiple){const{value:n}=S;return Array.isArray(n)?ve(n):[]}return null}),ee=M(()=>{const{value:n}=S;return!e.multiple&&!Array.isArray(n)?n===null?null:ve([n])[0]||null:null}),le=uo(e,{mergedSize:n=>{var d,g;const{size:C}=e;if(C)return C;const{mergedSize:x}=n||{};if(x!=null&&x.value)return x.value;const w=(g=(d=v==null?void 0:v.value)===null||d===void 0?void 0:d.Select)===null||g===void 0?void 0:g.size;return w||"medium"}}),{mergedSizeRef:Re,mergedDisabledRef:de,mergedStatusRef:pe}=le;function q(n,d){const{onChange:g,"onUpdate:value":C,onUpdateValue:x}=e,{nTriggerFormChange:w,nTriggerFormInput:O}=le;g&&te(g,n,d),x&&te(x,n,d),C&&te(C,n,d),l.value=n,w(),O()}function J(n){const{onBlur:d}=e,{nTriggerFormBlur:g}=le;d&&te(d,n),g()}function ge(){const{onClear:n}=e;n&&te(n)}function i(n){const{onFocus:d,showOnFocus:g}=e,{nTriggerFormFocus:C}=le;d&&te(d,n),C(),g&&re()}function h(n){const{onSearch:d}=e;d&&te(d,n)}function A(n){const{onScroll:d}=e;d&&te(d,n)}function ne(){var n;const{remote:d,multiple:g}=e;if(d){const{value:C}=U;if(g){const{valueField:x}=e;(n=we.value)===null||n===void 0||n.forEach(w=>{C.set(w[x],w)})}else{const x=ee.value;x&&C.set(x[e.valueField],x)}}}function Ce(n){const{onUpdateShow:d,"onUpdate:show":g}=e;d&&te(d,n),g&&te(g,n),V.value=n}function re(){de.value||(Ce(!0),V.value=!0,e.filterable&&De())}function G(){Ce(!1)}function ye(){b.value="",p.value=W}const ce=$(!1);function Te(){e.filterable&&(ce.value=!0)}function Pe(){e.filterable&&(ce.value=!1,E.value||ye())}function Me(){de.value||(E.value?e.filterable?De():G():re())}function be(n){var d,g;!((g=(d=K.value)===null||d===void 0?void 0:d.selfRef)===null||g===void 0)&&g.contains(n.relatedTarget)||(m.value=!1,J(n),G())}function me(n){i(n),m.value=!0}function ke(){m.value=!0}function _e(n){var d;!((d=B.value)===null||d===void 0)&&d.$el.contains(n.relatedTarget)||(m.value=!1,J(n),G())}function $e(){var n;(n=B.value)===null||n===void 0||n.focus(),G()}function Se(n){var d;E.value&&(!((d=B.value)===null||d===void 0)&&d.$el.contains(ho(n))||G())}function Oe(n){if(!Array.isArray(n))return[];if(L.value)return Array.from(n);{const{remote:d}=e,{value:g}=I;if(d){const{value:C}=U;return n.filter(x=>g.has(x)||C.has(x))}else return n.filter(C=>g.has(C))}}function Q(n){o(n.rawNode)}function o(n){if(de.value)return;const{tag:d,remote:g,clearFilterAfterSelect:C,valueField:x}=e;if(d&&!g){const{value:w}=p,O=w[0]||null;if(O){const H=z.value;H.length?H.push(O):z.value=[O],p.value=W}}if(g&&U.value.set(n[x],n),e.multiple){const w=Oe(S.value),O=w.findIndex(H=>H===n[x]);if(~O){if(w.splice(O,1),d&&!g){const H=u(n[x]);~H&&(z.value.splice(H,1),C&&(b.value=""))}}else w.push(n[x]),C&&(b.value="");q(w,ve(w))}else{if(d&&!g){const w=u(n[x]);~w?z.value=[z.value[w]]:z.value=W}Ae(),G(),q(n[x],n)}}function u(n){return z.value.findIndex(g=>g[e.valueField]===n)}function N(n){E.value||re();const{value:d}=n.target;b.value=d;const{tag:g,remote:C}=e;if(h(d),g&&!C){if(!d){p.value=W;return}const{onCreate:x}=e,w=x?x(d):{[e.labelField]:d,[e.valueField]:d},{valueField:O,labelField:H}=e;P.value.some(oe=>oe[O]===w[O]||oe[H]===w[H])||z.value.some(oe=>oe[O]===w[O]||oe[H]===w[H])?p.value=W:p.value=[w]}}function Ue(n){n.stopPropagation();const{multiple:d,tag:g,remote:C,clearCreatedOptionsOnClear:x}=e;!d&&e.filterable&&G(),g&&!C&&x&&(z.value=W),ge(),d?q([],[]):q(null,null)}function qe(n){!Be(n,"action")&&!Be(n,"empty")&&!Be(n,"header")&&n.preventDefault()}function Ge(n){A(n)}function Ee(n){var d,g,C,x,w;if(!e.keyboard){n.preventDefault();return}switch(n.key){case" ":if(e.filterable)break;n.preventDefault();case"Enter":if(!(!((d=B.value)===null||d===void 0)&&d.isComposing)){if(E.value){const O=(g=K.value)===null||g===void 0?void 0:g.getPendingTmNode();O?Q(O):e.filterable||(G(),Ae())}else if(re(),e.tag&&ce.value){const O=p.value[0];if(O){const H=O[e.valueField],{value:oe}=S;e.multiple&&Array.isArray(oe)&&oe.includes(H)||o(O)}}}n.preventDefault();break;case"ArrowUp":if(n.preventDefault(),e.loading)return;E.value&&((C=K.value)===null||C===void 0||C.prev());break;case"ArrowDown":if(n.preventDefault(),e.loading)return;E.value?(x=K.value)===null||x===void 0||x.next():re();break;case"Escape":E.value&&(vo(n),G()),(w=B.value)===null||w===void 0||w.focus();break}}function Ae(){var n;(n=B.value)===null||n===void 0||n.focus()}function De(){var n;(n=B.value)===null||n===void 0||n.focusInput()}function Ze(){var n;E.value&&((n=Y.value)===null||n===void 0||n.syncPosition())}ne(),xe(j(e,"options"),ne);const Ye={focus:()=>{var n;(n=B.value)===null||n===void 0||n.focus()},focusInput:()=>{var n;(n=B.value)===null||n===void 0||n.focusInput()},blur:()=>{var n;(n=B.value)===null||n===void 0||n.blur()},blurInput:()=>{var n;(n=B.value)===null||n===void 0||n.blurInput()}},Le=M(()=>{const{self:{menuBoxShadow:n}}=c.value;return{"--n-menu-box-shadow":n}}),ue=f?Ke("select",void 0,Le,e):void 0;return Object.assign(Object.assign({},Ye),{mergedStatus:pe,mergedClsPrefix:t,mergedBordered:a,namespace:s,treeMate:F,isMounted:fo(),triggerRef:B,menuRef:K,pattern:b,uncontrolledShow:V,mergedShow:E,adjustedTo:tn(e),uncontrolledValue:l,mergedValue:S,followerRef:Y,localizedPlaceholder:se,selectedOption:ee,selectedOptions:we,mergedSize:Re,mergedDisabled:de,focused:m,activeWithoutMenuOpen:ce,inlineThemeDisabled:f,onTriggerInputFocus:Te,onTriggerInputBlur:Pe,handleTriggerOrMenuResize:Ze,handleMenuFocus:ke,handleMenuBlur:_e,handleMenuTabOut:$e,handleTriggerClick:Me,handleToggle:Q,handleDeleteOption:o,handlePatternInput:N,handleClear:Ue,handleTriggerBlur:be,handleTriggerFocus:me,handleKeydown:Ee,handleMenuAfterLeave:ye,handleMenuClickOutside:Se,handleMenuScroll:Ge,handleMenuKeydown:Ee,handleMenuMousedown:qe,mergedTheme:c,cssVars:f?void 0:Le,themeClass:ue==null?void 0:ue.themeClass,onRender:ue==null?void 0:ue.onRender})},render(){return r("div",{class:`${this.mergedClsPrefix}-select`},r(go,null,{default:()=>[r(bo,null,{default:()=>r(Vo,{ref:"triggerRef",inlineThemeDisabled:this.inlineThemeDisabled,status:this.mergedStatus,inputProps:this.inputProps,clsPrefix:this.mergedClsPrefix,showArrow:this.showArrow,maxTagCount:this.maxTagCount,ellipsisTagPopoverProps:this.ellipsisTagPopoverProps,bordered:this.mergedBordered,active:this.activeWithoutMenuOpen||this.mergedShow,pattern:this.pattern,placeholder:this.localizedPlaceholder,selectedOption:this.selectedOption,selectedOptions:this.selectedOptions,multiple:this.multiple,renderTag:this.renderTag,renderLabel:this.renderLabel,filterable:this.filterable,clearable:this.clearable,disabled:this.mergedDisabled,size:this.mergedSize,theme:this.mergedTheme.peers.InternalSelection,labelField:this.labelField,valueField:this.valueField,themeOverrides:this.mergedTheme.peerOverrides.InternalSelection,loading:this.loading,focused:this.focused,onClick:this.handleTriggerClick,onDeleteOption:this.handleDeleteOption,onPatternInput:this.handlePatternInput,onClear:this.handleClear,onBlur:this.handleTriggerBlur,onFocus:this.handleTriggerFocus,onKeydown:this.handleKeydown,onPatternBlur:this.onTriggerInputBlur,onPatternFocus:this.onTriggerInputFocus,onResize:this.handleTriggerOrMenuResize,ignoreComposition:this.ignoreComposition},{arrow:()=>{var e,t;return[(t=(e=this.$slots).arrow)===null||t===void 0?void 0:t.call(e)]}})}),r(mo,{ref:"followerRef",show:this.mergedShow,to:this.adjustedTo,teleportDisabled:this.adjustedTo===tn.tdkey,containerClass:this.namespace,width:this.consistentMenuWidth?"target":void 0,minWidth:"target",placement:this.placement},{default:()=>r(xn,{name:"fade-in-scale-up-transition",appear:this.isMounted,onAfterLeave:this.handleMenuAfterLeave},{default:()=>{var e,t,a;return this.mergedShow||this.displayDirective==="show"?((e=this.onRender)===null||e===void 0||e.call(this),so(r(Do,Object.assign({},this.menuProps,{ref:"menuRef",onResize:this.handleTriggerOrMenuResize,inlineThemeDisabled:this.inlineThemeDisabled,virtualScroll:this.consistentMenuWidth&&this.virtualScroll,class:[`${this.mergedClsPrefix}-select-menu`,this.themeClass,(t=this.menuProps)===null||t===void 0?void 0:t.class],clsPrefix:this.mergedClsPrefix,focusable:!0,labelField:this.labelField,valueField:this.valueField,autoPending:!0,nodeProps:this.nodeProps,theme:this.mergedTheme.peers.InternalSelectMenu,themeOverrides:this.mergedTheme.peerOverrides.InternalSelectMenu,treeMate:this.treeMate,multiple:this.multiple,size:this.menuSize,renderOption:this.renderOption,renderLabel:this.renderLabel,value:this.mergedValue,style:[(a=this.menuProps)===null||a===void 0?void 0:a.style,this.cssVars],onToggle:this.handleToggle,onScroll:this.handleMenuScroll,onFocus:this.handleMenuFocus,onBlur:this.handleMenuBlur,onKeydown:this.handleMenuKeydown,onTabOut:this.handleMenuTabOut,onMousedown:this.handleMenuMousedown,show:this.mergedShow,showCheckmark:this.showCheckmark,resetMenuOnOptionsChange:this.resetMenuOnOptionsChange,scrollbarProps:this.scrollbarProps}),{empty:()=>{var s,f;return[(f=(s=this.$slots).empty)===null||f===void 0?void 0:f.call(s)]},header:()=>{var s,f;return[(f=(s=this.$slots).header)===null||f===void 0?void 0:f.call(s)]},action:()=>{var s,f;return[(f=(s=this.$slots).action)===null||f===void 0?void 0:f.call(s)]}}),this.displayDirective==="show"?[[co,this.mergedShow],[cn,this.handleMenuClickOutside,void 0,{capture:!0}]]:[[cn,this.handleMenuClickOutside,void 0,{capture:!0}]])):null}})})]}))}});export{$o as N,Do as a,rt as b,Lo as c,jo as d,zn as e,Mo as f,Bo as g,Uo as h,Fn as i,en as m,qo as s};

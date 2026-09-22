import{al as so,Z as e,J as to,Q as u,P as k,R as y,H as I,aj as io,bR as L,aH as x,N as ho,cc as go,cp as U,cm as bo,cq as Co,bG as vo,Y as uo,a3 as w,ad as d,aD as po,a0 as D,bC as fo,c5 as mo,ac as ko}from"./index-DYclsc6M.js";const xo={closeIconSizeTiny:"12px",closeIconSizeSmall:"12px",closeIconSizeMedium:"14px",closeIconSizeLarge:"14px",closeSizeTiny:"16px",closeSizeSmall:"16px",closeSizeMedium:"18px",closeSizeLarge:"18px",padding:"0 7px",closeMargin:"0 0 0 4px"};function zo(c){const{textColor2:g,primaryColorHover:r,primaryColorPressed:p,primaryColor:a,infoColor:i,successColor:n,warningColor:s,errorColor:t,baseColor:f,borderColor:m,opacityDisabled:b,tagColor:P,closeIconColor:z,closeIconColorHover:v,closeIconColorPressed:o,borderRadiusSmall:l,fontSizeMini:C,fontSizeTiny:h,fontSizeSmall:B,fontSizeMedium:H,heightMini:$,heightTiny:R,heightSmall:M,heightMedium:T,closeColorHover:_,closeColorPressed:E,buttonColor2Hover:j,buttonColor2Pressed:O,fontWeightStrong:W}=c;return Object.assign(Object.assign({},xo),{closeBorderRadius:l,heightTiny:$,heightSmall:R,heightMedium:M,heightLarge:T,borderRadius:l,opacityDisabled:b,fontSizeTiny:C,fontSizeSmall:h,fontSizeMedium:B,fontSizeLarge:H,fontWeightStrong:W,textColorCheckable:g,textColorHoverCheckable:g,textColorPressedCheckable:g,textColorChecked:f,colorCheckable:"#0000",colorHoverCheckable:j,colorPressedCheckable:O,colorChecked:a,colorCheckedHover:r,colorCheckedPressed:p,border:`1px solid ${m}`,textColor:g,color:P,colorBordered:"rgb(250, 250, 252)",closeIconColor:z,closeIconColorHover:v,closeIconColorPressed:o,closeColorHover:_,closeColorPressed:E,borderPrimary:`1px solid ${e(a,{alpha:.3})}`,textColorPrimary:a,colorPrimary:e(a,{alpha:.12}),colorBorderedPrimary:e(a,{alpha:.1}),closeIconColorPrimary:a,closeIconColorHoverPrimary:a,closeIconColorPressedPrimary:a,closeColorHoverPrimary:e(a,{alpha:.12}),closeColorPressedPrimary:e(a,{alpha:.18}),borderInfo:`1px solid ${e(i,{alpha:.3})}`,textColorInfo:i,colorInfo:e(i,{alpha:.12}),colorBorderedInfo:e(i,{alpha:.1}),closeIconColorInfo:i,closeIconColorHoverInfo:i,closeIconColorPressedInfo:i,closeColorHoverInfo:e(i,{alpha:.12}),closeColorPressedInfo:e(i,{alpha:.18}),borderSuccess:`1px solid ${e(n,{alpha:.3})}`,textColorSuccess:n,colorSuccess:e(n,{alpha:.12}),colorBorderedSuccess:e(n,{alpha:.1}),closeIconColorSuccess:n,closeIconColorHoverSuccess:n,closeIconColorPressedSuccess:n,closeColorHoverSuccess:e(n,{alpha:.12}),closeColorPressedSuccess:e(n,{alpha:.18}),borderWarning:`1px solid ${e(s,{alpha:.35})}`,textColorWarning:s,colorWarning:e(s,{alpha:.15}),colorBorderedWarning:e(s,{alpha:.12}),closeIconColorWarning:s,closeIconColorHoverWarning:s,closeIconColorPressedWarning:s,closeColorHoverWarning:e(s,{alpha:.12}),closeColorPressedWarning:e(s,{alpha:.18}),borderError:`1px solid ${e(t,{alpha:.23})}`,textColorError:t,colorError:e(t,{alpha:.1}),colorBorderedError:e(t,{alpha:.08}),closeIconColorError:t,closeIconColorHoverError:t,closeIconColorPressedError:t,closeColorHoverError:e(t,{alpha:.12}),closeColorPressedError:e(t,{alpha:.18})})}const So={common:so,self:zo},yo={color:Object,type:{type:String,default:"default"},round:Boolean,size:String,closable:Boolean,disabled:{type:Boolean,default:void 0}},Io=to("tag",`
 --n-close-margin: var(--n-close-margin-top) var(--n-close-margin-right) var(--n-close-margin-bottom) var(--n-close-margin-left);
 white-space: nowrap;
 position: relative;
 box-sizing: border-box;
 cursor: default;
 display: inline-flex;
 align-items: center;
 flex-wrap: nowrap;
 padding: var(--n-padding);
 border-radius: var(--n-border-radius);
 color: var(--n-text-color);
 background-color: var(--n-color);
 transition: 
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 box-shadow .3s var(--n-bezier),
 opacity .3s var(--n-bezier);
 line-height: 1;
 height: var(--n-height);
 font-size: var(--n-font-size);
`,[u("strong",`
 font-weight: var(--n-font-weight-strong);
 `),k("border",`
 pointer-events: none;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 border-radius: inherit;
 border: var(--n-border);
 transition: border-color .3s var(--n-bezier);
 `),k("icon",`
 display: flex;
 margin: 0 4px 0 0;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 font-size: var(--n-avatar-size-override);
 `),k("avatar",`
 display: flex;
 margin: 0 6px 0 0;
 `),k("close",`
 margin: var(--n-close-margin);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `),u("round",`
 padding: 0 calc(var(--n-height) / 3);
 border-radius: calc(var(--n-height) / 2);
 `,[k("icon",`
 margin: 0 4px 0 calc((var(--n-height) - 8px) / -2);
 `),k("avatar",`
 margin: 0 6px 0 calc((var(--n-height) - 8px) / -2);
 `),u("closable",`
 padding: 0 calc(var(--n-height) / 4) 0 calc(var(--n-height) / 3);
 `)]),u("icon, avatar",[u("round",`
 padding: 0 calc(var(--n-height) / 3) 0 calc(var(--n-height) / 2);
 `)]),u("disabled",`
 cursor: not-allowed !important;
 opacity: var(--n-opacity-disabled);
 `),u("checkable",`
 cursor: pointer;
 box-shadow: none;
 color: var(--n-text-color-checkable);
 background-color: var(--n-color-checkable);
 `,[y("disabled",[I("&:hover","background-color: var(--n-color-hover-checkable);",[y("checked","color: var(--n-text-color-hover-checkable);")]),I("&:active","background-color: var(--n-color-pressed-checkable);",[y("checked","color: var(--n-text-color-pressed-checkable);")])]),u("checked",`
 color: var(--n-text-color-checked);
 background-color: var(--n-color-checked);
 `,[y("disabled",[I("&:hover","background-color: var(--n-color-checked-hover);"),I("&:active","background-color: var(--n-color-checked-pressed);")])])])]),Po=Object.assign(Object.assign(Object.assign({},U.props),yo),{bordered:{type:Boolean,default:void 0},checked:Boolean,checkable:Boolean,strong:Boolean,triggerClickOnClose:Boolean,onClose:[Array,Function],onMouseenter:Function,onMouseleave:Function,"onUpdate:checked":Function,onUpdateChecked:Function,internalCloseFocusable:{type:Boolean,default:!0},internalCloseIsButtonTag:{type:Boolean,default:!0},onCheckedChange:Function}),Bo=ko("n-tag"),$o=io({name:"Tag",props:Po,slots:Object,setup(c){const g=vo(null),{mergedBorderedRef:r,mergedClsPrefixRef:p,inlineThemeDisabled:a,mergedRtlRef:i,mergedComponentPropsRef:n}=go(c),s=w(()=>{var o,l;return c.size||((l=(o=n==null?void 0:n.value)===null||o===void 0?void 0:o.Tag)===null||l===void 0?void 0:l.size)||"medium"}),t=U("Tag","-tag",Io,So,c,p);fo(Bo,{roundRef:mo(c,"round")});function f(){if(!c.disabled&&c.checkable){const{checked:o,onCheckedChange:l,onUpdateChecked:C,"onUpdate:checked":h}=c;C&&C(!o),h&&h(!o),l&&l(!o)}}function m(o){if(c.triggerClickOnClose||o.stopPropagation(),!c.disabled){const{onClose:l}=c;l&&uo(l,o)}}const b={setTextContent(o){const{value:l}=g;l&&(l.textContent=o)}},P=bo("Tag",i,p),z=w(()=>{const{type:o,color:{color:l,textColor:C}={}}=c,h=s.value,{common:{cubicBezierEaseInOut:B},self:{padding:H,closeMargin:$,borderRadius:R,opacityDisabled:M,textColorCheckable:T,textColorHoverCheckable:_,textColorPressedCheckable:E,textColorChecked:j,colorCheckable:O,colorHoverCheckable:W,colorPressedCheckable:V,colorChecked:K,colorCheckedHover:q,colorCheckedPressed:A,closeBorderRadius:G,fontWeightStrong:J,[d("colorBordered",o)]:Q,[d("closeSize",h)]:Y,[d("closeIconSize",h)]:Z,[d("fontSize",h)]:X,[d("height",h)]:F,[d("color",o)]:oo,[d("textColor",o)]:eo,[d("border",o)]:ro,[d("closeIconColor",o)]:N,[d("closeIconColorHover",o)]:lo,[d("closeIconColorPressed",o)]:co,[d("closeColorHover",o)]:ao,[d("closeColorPressed",o)]:no}}=t.value,S=po($);return{"--n-font-weight-strong":J,"--n-avatar-size-override":`calc(${F} - 8px)`,"--n-bezier":B,"--n-border-radius":R,"--n-border":ro,"--n-close-icon-size":Z,"--n-close-color-pressed":no,"--n-close-color-hover":ao,"--n-close-border-radius":G,"--n-close-icon-color":N,"--n-close-icon-color-hover":lo,"--n-close-icon-color-pressed":co,"--n-close-icon-color-disabled":N,"--n-close-margin-top":S.top,"--n-close-margin-right":S.right,"--n-close-margin-bottom":S.bottom,"--n-close-margin-left":S.left,"--n-close-size":Y,"--n-color":l||(r.value?Q:oo),"--n-color-checkable":O,"--n-color-checked":K,"--n-color-checked-hover":q,"--n-color-checked-pressed":A,"--n-color-hover-checkable":W,"--n-color-pressed-checkable":V,"--n-font-size":X,"--n-height":F,"--n-opacity-disabled":M,"--n-padding":H,"--n-text-color":C||eo,"--n-text-color-checkable":T,"--n-text-color-checked":j,"--n-text-color-hover-checkable":_,"--n-text-color-pressed-checkable":E}}),v=a?Co("tag",w(()=>{let o="";const{type:l,color:{color:C,textColor:h}={}}=c;return o+=l[0],o+=s.value[0],C&&(o+=`a${D(C)}`),h&&(o+=`b${D(h)}`),r.value&&(o+="c"),o}),z,c):void 0;return Object.assign(Object.assign({},b),{rtlEnabled:P,mergedClsPrefix:p,contentRef:g,mergedBordered:r,handleClick:f,handleCloseClick:m,cssVars:a?void 0:z,themeClass:v==null?void 0:v.themeClass,onRender:v==null?void 0:v.onRender})},render(){var c,g;const{mergedClsPrefix:r,rtlEnabled:p,closable:a,color:{borderColor:i}={},round:n,onRender:s,$slots:t}=this;s==null||s();const f=L(t.avatar,b=>b&&x("div",{class:`${r}-tag__avatar`},b)),m=L(t.icon,b=>b&&x("div",{class:`${r}-tag__icon`},b));return x("div",{class:[`${r}-tag`,this.themeClass,{[`${r}-tag--rtl`]:p,[`${r}-tag--strong`]:this.strong,[`${r}-tag--disabled`]:this.disabled,[`${r}-tag--checkable`]:this.checkable,[`${r}-tag--checked`]:this.checkable&&this.checked,[`${r}-tag--round`]:n,[`${r}-tag--avatar`]:f,[`${r}-tag--icon`]:m,[`${r}-tag--closable`]:a}],style:this.cssVars,onClick:this.handleClick,onMouseenter:this.onMouseenter,onMouseleave:this.onMouseleave},m||f,x("span",{class:`${r}-tag__content`,ref:"contentRef"},(g=(c=this.$slots).default)===null||g===void 0?void 0:g.call(c)),!this.checkable&&a?x(ho,{clsPrefix:r,class:`${r}-tag__close`,disabled:this.disabled,onClick:this.handleCloseClick,focusable:this.internalCloseFocusable,round:n,isButtonTag:this.internalCloseIsButtonTag,absolute:!0}):null,!this.checkable&&this.mergedBordered?x("div",{class:`${r}-tag__border`,style:{borderColor:i}}):null)}}),Ro={gapSmall:"4px 8px",gapMedium:"8px 12px",gapLarge:"12px 16px"};export{$o as N,Ro as a,xo as c};

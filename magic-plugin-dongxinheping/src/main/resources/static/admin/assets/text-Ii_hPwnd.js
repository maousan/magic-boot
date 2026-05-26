import{aj as y,J as B,Q as C,ah as S,aC as g,bR as z,c0 as b,c1 as T,a3 as f,ac as W}from"./index-CmaQkH7q.js";import{r as P}from"./composables-B2vItlUa.js";const F={headerFontSize1:"30px",headerFontSize2:"22px",headerFontSize3:"18px",headerFontSize4:"16px",headerFontSize5:"16px",headerFontSize6:"16px",headerMargin1:"28px 0 20px 0",headerMargin2:"28px 0 20px 0",headerMargin3:"28px 0 20px 0",headerMargin4:"28px 0 18px 0",headerMargin5:"28px 0 18px 0",headerMargin6:"28px 0 18px 0",headerPrefixWidth1:"16px",headerPrefixWidth2:"16px",headerPrefixWidth3:"12px",headerPrefixWidth4:"12px",headerPrefixWidth5:"12px",headerPrefixWidth6:"12px",headerBarWidth1:"4px",headerBarWidth2:"4px",headerBarWidth3:"3px",headerBarWidth4:"3px",headerBarWidth5:"3px",headerBarWidth6:"3px",pMargin:"16px 0 16px 0",liMargin:".25em 0 0 0",olPadding:"0 0 0 2em",ulPadding:"0 0 0 2em"};function M(r){const{primaryColor:t,textColor2:o,borderColor:i,lineHeight:a,fontSize:e,borderRadiusSmall:l,dividerColor:d,fontWeightStrong:u,textColor1:n,textColor3:s,infoColor:c,warningColor:h,errorColor:x,successColor:p,codeColor:m}=r;return Object.assign(Object.assign({},F),{aTextColor:t,blockquoteTextColor:o,blockquotePrefixColor:i,blockquoteLineHeight:a,blockquoteFontSize:e,codeBorderRadius:l,liTextColor:o,liLineHeight:a,liFontSize:e,hrColor:d,headerFontWeight:u,headerTextColor:n,pTextColor:o,pTextColor1Depth:n,pTextColor2Depth:o,pTextColor3Depth:s,pLineHeight:a,pFontSize:e,headerBarColor:t,headerBarColorPrimary:t,headerBarColorInfo:c,headerBarColorError:x,headerBarColorWarning:h,headerBarColorSuccess:p,textColor:o,textColor1Depth:n,textColor2Depth:o,textColor3Depth:s,textColorPrimary:t,textColorInfo:c,textColorSuccess:p,textColorWarning:h,textColorError:x,codeTextColor:o,codeColor:m,codeBorder:"1px solid #0000"})}const R={common:y,self:M},$=B("text",`
 transition: color .3s var(--n-bezier);
 color: var(--n-text-color);
`,[C("strong",`
 font-weight: var(--n-font-weight-strong);
 `),C("italic",{fontStyle:"italic"}),C("underline",{textDecoration:"underline"}),C("code",`
 line-height: 1.4;
 display: inline-block;
 font-family: var(--n-font-famliy-mono);
 transition: 
 color .3s var(--n-bezier),
 border-color .3s var(--n-bezier),
 background-color .3s var(--n-bezier);
 box-sizing: border-box;
 padding: .05em .35em 0 .35em;
 border-radius: var(--n-code-border-radius);
 font-size: .9em;
 color: var(--n-code-text-color);
 background-color: var(--n-code-color);
 border: var(--n-code-border);
 `)]),D=Object.assign(Object.assign({},b.props),{code:Boolean,type:{type:String,default:"default"},delete:Boolean,strong:Boolean,italic:Boolean,underline:Boolean,depth:[String,Number],tag:String,as:{type:String,validator:()=>!0,default:void 0}}),j=S({name:"Text",props:D,setup(r){const{mergedClsPrefixRef:t,inlineThemeDisabled:o}=z(r),i=b("Typography","-text",$,R,r,t),a=f(()=>{const{depth:l,type:d}=r,u=d==="default"?l===void 0?"textColor":`textColor${l}Depth`:W("textColor",d),{common:{fontWeightStrong:n,fontFamilyMono:s,cubicBezierEaseInOut:c},self:{codeTextColor:h,codeBorderRadius:x,codeColor:p,codeBorder:m,[u]:v}}=i.value;return{"--n-bezier":c,"--n-text-color":v,"--n-font-weight-strong":n,"--n-font-famliy-mono":s,"--n-code-border-radius":x,"--n-code-text-color":h,"--n-code-color":p,"--n-code-border":m}}),e=o?T("text",f(()=>`${r.type[0]}${r.depth||""}`),a,r):void 0;return{mergedClsPrefix:t,compitableTag:P(r,["as","tag"]),cssVars:o?void 0:a,themeClass:e==null?void 0:e.themeClass,onRender:e==null?void 0:e.onRender}},render(){var r,t,o;const{mergedClsPrefix:i}=this;(r=this.onRender)===null||r===void 0||r.call(this);const a=[`${i}-text`,this.themeClass,{[`${i}-text--code`]:this.code,[`${i}-text--delete`]:this.delete,[`${i}-text--strong`]:this.strong,[`${i}-text--italic`]:this.italic,[`${i}-text--underline`]:this.underline}],e=(o=(t=this.$slots).default)===null||o===void 0?void 0:o.call(t);return this.code?g("code",{class:a,style:this.cssVars},this.delete?g("del",null,e):e):this.delete?g("del",{class:a,style:this.cssVars},e):g(this.compitableTag||"span",{class:a,style:this.cssVars},e)}});export{j as N};

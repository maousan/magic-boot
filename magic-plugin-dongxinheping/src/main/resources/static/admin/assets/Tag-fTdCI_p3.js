import{a5 as K,bs as ge,bu as pe,aH as fe,bW as he,bC as mr,b1 as wr,aU as Oe,u as Cr,aT as Q,x as Ze,cb as Z,M as ye,c as yr,p as Me,ar as Sr,U as ke,aX as ze,n as ce,b6 as xr,b0 as Ee,a$ as Pr,aZ as $r,aY as Or,aS as _r,aO as Ir,w as Tr,v as Rr,ai as Ar,bY as Br,an as er,H as k,J as D,R as X,P as j,Q as M,O as Mr,al as _e,aL as x,r as kr,ch as rr,cu as le,cr as or,bI as U,cA as nr,cv as tr,bx as zr,cz as Er,aP as Hr,b4 as He,F as Fr,ba as Dr,cC as ar,bU as ne,b as Lr,t as Wr,a0 as Fe,cx as Nr,c9 as te,aI as De,bE as ae,bD as jr,ap as Ur,bi as Kr,aD as Gr,a1 as Yr,q as Vr,cF as Xr,b7 as Jr,a_ as qr,cl as Le,Y as oe,_ as S,N as Qr,af as A,aG as Zr,a2 as We,ae as eo}from"./index-B_kJwC9K.js";import{c as ro,t as Ie,i as lr,g as oo,b as no,f as be,h as to}from"./request-DRc-RfIL.js";import{V as ao,u as de,B as lo,a as so}from"./Follower-CjrEVEsI.js";function io(e,r){return K(()=>{for(const o of r)if(e[o]!==void 0)return e[o];return e[r[r.length-1]]})}const q="@@mmoContext",co={mounted(e,{value:r}){e[q]={handler:void 0},typeof r=="function"&&(e[q].handler=r,pe("mousemoveoutside",e,r))},updated(e,{value:r}){const o=e[q];typeof r=="function"?o.handler?o.handler!==r&&(ge("mousemoveoutside",e,o.handler),o.handler=r,pe("mousemoveoutside",e,r)):(e[q].handler=r,pe("mousemoveoutside",e,r)):o.handler&&(ge("mousemoveoutside",e,o.handler),o.handler=void 0)},unmounted(e){const{handler:r}=e[q];r&&ge("mousemoveoutside",e,r),e[q].handler=void 0}};let me;function uo(){return me===void 0&&(me=navigator.userAgent.includes("Node.js")||navigator.userAgent.includes("jsdom")),me}var Se=fe(he,"WeakMap"),fo=mr(Object.keys,Object),ho=Object.prototype,vo=ho.hasOwnProperty;function go(e){if(!wr(e))return fo(e);var r=[];for(var o in Object(e))vo.call(e,o)&&o!="constructor"&&r.push(o);return r}function Te(e){return Oe(e)?Cr(e):go(e)}function po(e,r){for(var o=-1,n=r.length,a=e.length;++o<n;)e[a+o]=r[o];return e}function bo(e,r){for(var o=-1,n=e==null?0:e.length,a=0,t=[];++o<n;){var l=e[o];r(l,o,e)&&(t[a++]=l)}return t}function mo(){return[]}var wo=Object.prototype,Co=wo.propertyIsEnumerable,Ne=Object.getOwnPropertySymbols,yo=Ne?function(e){return e==null?[]:(e=Object(e),bo(Ne(e),function(r){return Co.call(e,r)}))}:mo;function So(e,r,o){var n=r(e);return Q(e)?n:po(n,o(e))}function je(e){return So(e,Te,yo)}var xe=fe(he,"DataView"),Pe=fe(he,"Promise"),$e=fe(he,"Set"),Ue="[object Map]",xo="[object Object]",Ke="[object Promise]",Ge="[object Set]",Ye="[object WeakMap]",Ve="[object DataView]",Po=Z(xe),$o=Z(ye),Oo=Z(Pe),_o=Z($e),Io=Z(Se),Y=Ze;(xe&&Y(new xe(new ArrayBuffer(1)))!=Ve||ye&&Y(new ye)!=Ue||Pe&&Y(Pe.resolve())!=Ke||$e&&Y(new $e)!=Ge||Se&&Y(new Se)!=Ye)&&(Y=function(e){var r=Ze(e),o=r==xo?e.constructor:void 0,n=o?Z(o):"";if(n)switch(n){case Po:return Ve;case $o:return Ue;case Oo:return Ke;case _o:return Ge;case Io:return Ye}return r});var To="__lodash_hash_undefined__";function Ro(e){return this.__data__.set(e,To),this}function Ao(e){return this.__data__.has(e)}function ue(e){var r=-1,o=e==null?0:e.length;for(this.__data__=new yr;++r<o;)this.add(e[r])}ue.prototype.add=ue.prototype.push=Ro;ue.prototype.has=Ao;function Bo(e,r){for(var o=-1,n=e==null?0:e.length;++o<n;)if(r(e[o],o,e))return!0;return!1}function Mo(e,r){return e.has(r)}var ko=1,zo=2;function sr(e,r,o,n,a,t){var l=o&ko,s=e.length,c=r.length;if(s!=c&&!(l&&c>s))return!1;var f=t.get(e),u=t.get(r);if(f&&u)return f==r&&u==e;var v=-1,m=!0,y=o&zo?new ue:void 0;for(t.set(e,r),t.set(r,e);++v<s;){var C=e[v],i=r[v];if(n)var g=l?n(i,C,v,r,e,t):n(C,i,v,e,r,t);if(g!==void 0){if(g)continue;m=!1;break}if(y){if(!Bo(r,function(p,w){if(!Mo(y,w)&&(C===p||a(C,p,o,n,t)))return y.push(w)})){m=!1;break}}else if(!(C===i||a(C,i,o,n,t))){m=!1;break}}return t.delete(e),t.delete(r),m}function Eo(e){var r=-1,o=Array(e.size);return e.forEach(function(n,a){o[++r]=[a,n]}),o}function Ho(e){var r=-1,o=Array(e.size);return e.forEach(function(n){o[++r]=n}),o}var Fo=1,Do=2,Lo="[object Boolean]",Wo="[object Date]",No="[object Error]",jo="[object Map]",Uo="[object Number]",Ko="[object RegExp]",Go="[object Set]",Yo="[object String]",Vo="[object Symbol]",Xo="[object ArrayBuffer]",Jo="[object DataView]",Xe=Me?Me.prototype:void 0,we=Xe?Xe.valueOf:void 0;function qo(e,r,o,n,a,t,l){switch(o){case Jo:if(e.byteLength!=r.byteLength||e.byteOffset!=r.byteOffset)return!1;e=e.buffer,r=r.buffer;case Xo:return!(e.byteLength!=r.byteLength||!t(new ke(e),new ke(r)));case Lo:case Wo:case Uo:return Sr(+e,+r);case No:return e.name==r.name&&e.message==r.message;case Ko:case Yo:return e==r+"";case jo:var s=Eo;case Go:var c=n&Fo;if(s||(s=Ho),e.size!=r.size&&!c)return!1;var f=l.get(e);if(f)return f==r;n|=Do,l.set(e,r);var u=sr(s(e),s(r),n,a,t,l);return l.delete(e),u;case Vo:if(we)return we.call(e)==we.call(r)}return!1}var Qo=1,Zo=Object.prototype,en=Zo.hasOwnProperty;function rn(e,r,o,n,a,t){var l=o&Qo,s=je(e),c=s.length,f=je(r),u=f.length;if(c!=u&&!l)return!1;for(var v=c;v--;){var m=s[v];if(!(l?m in r:en.call(r,m)))return!1}var y=t.get(e),C=t.get(r);if(y&&C)return y==r&&C==e;var i=!0;t.set(e,r),t.set(r,e);for(var g=l;++v<c;){m=s[v];var p=e[m],w=r[m];if(n)var z=l?n(w,p,m,r,e,t):n(p,w,m,e,r,t);if(!(z===void 0?p===w||a(p,w,o,n,t):z)){i=!1;break}g||(g=m=="constructor")}if(i&&!g){var T=e.constructor,R=r.constructor;T!=R&&"constructor"in e&&"constructor"in r&&!(typeof T=="function"&&T instanceof T&&typeof R=="function"&&R instanceof R)&&(i=!1)}return t.delete(e),t.delete(r),i}var on=1,Je="[object Arguments]",qe="[object Array]",ie="[object Object]",nn=Object.prototype,Qe=nn.hasOwnProperty;function tn(e,r,o,n,a,t){var l=Q(e),s=Q(r),c=l?qe:Y(e),f=s?qe:Y(r);c=c==Je?ie:c,f=f==Je?ie:f;var u=c==ie,v=f==ie,m=c==f;if(m&&ze(e)){if(!ze(r))return!1;l=!0,u=!1}if(m&&!u)return t||(t=new ce),l||xr(e)?sr(e,r,o,n,a,t):qo(e,r,c,o,n,a,t);if(!(o&on)){var y=u&&Qe.call(e,"__wrapped__"),C=v&&Qe.call(r,"__wrapped__");if(y||C){var i=y?e.value():e,g=C?r.value():r;return t||(t=new ce),a(i,g,o,n,t)}}return m?(t||(t=new ce),rn(e,r,o,n,a,t)):!1}function Re(e,r,o,n,a){return e===r?!0:e==null||r==null||!Ee(e)&&!Ee(r)?e!==e&&r!==r:tn(e,r,o,n,Re,a)}var an=1,ln=2;function sn(e,r,o,n){var a=o.length,t=a;if(e==null)return!t;for(e=Object(e);a--;){var l=o[a];if(l[2]?l[1]!==e[l[0]]:!(l[0]in e))return!1}for(;++a<t;){l=o[a];var s=l[0],c=e[s],f=l[1];if(l[2]){if(c===void 0&&!(s in e))return!1}else{var u=new ce,v;if(!(v===void 0?Re(f,c,an|ln,n,u):v))return!1}}return!0}function ir(e){return e===e&&!Pr(e)}function cn(e){for(var r=Te(e),o=r.length;o--;){var n=r[o],a=e[n];r[o]=[n,a,ir(a)]}return r}function cr(e,r){return function(o){return o==null?!1:o[e]===r&&(r!==void 0||e in Object(o))}}function dn(e){var r=cn(e);return r.length==1&&r[0][2]?cr(r[0][0],r[0][1]):function(o){return o===e||sn(o,e,r)}}function un(e,r){return e!=null&&r in Object(e)}function fn(e,r,o){r=ro(r,e);for(var n=-1,a=r.length,t=!1;++n<a;){var l=Ie(r[n]);if(!(t=e!=null&&o(e,l)))break;e=e[l]}return t||++n!=a?t:(a=e==null?0:e.length,!!a&&$r(a)&&Or(l,a)&&(Q(e)||_r(e)))}function hn(e,r){return e!=null&&fn(e,r,un)}var vn=1,gn=2;function pn(e,r){return lr(e)&&ir(r)?cr(Ie(e),r):function(o){var n=oo(o,e);return n===void 0&&n===r?hn(o,e):Re(r,n,vn|gn)}}function bn(e){return function(r){return r==null?void 0:r[e]}}function mn(e){return function(r){return no(r,e)}}function wn(e){return lr(e)?bn(Ie(e)):mn(e)}function Cn(e){return typeof e=="function"?e:e==null?Ir:typeof e=="object"?Q(e)?pn(e[0],e[1]):dn(e):wn(e)}function yn(e,r){return e&&Tr(e,r,Te)}function Sn(e,r){return function(o,n){if(o==null)return o;if(!Oe(o))return e(o,n);for(var a=o.length,t=-1,l=Object(o);++t<a&&n(l[t],t,l)!==!1;);return o}}var xn=Sn(yn);function Pn(e,r){var o=-1,n=Oe(e)?Array(e.length):[];return xn(e,function(a,t,l){n[++o]=r(a,t,l)}),n}function $n(e,r){var o=Q(e)?Rr:Pn;return o(e,Cn(r))}const On={space:"6px",spaceArrow:"10px",arrowOffset:"10px",arrowOffsetVertical:"10px",arrowHeight:"6px",padding:"8px 14px"};function _n(e){const{boxShadow2:r,popoverColor:o,textColor2:n,borderRadius:a,fontSize:t,dividerColor:l}=e;return Object.assign(Object.assign({},On),{fontSize:t,borderRadius:a,color:o,dividerColor:l,textColor:n,boxShadow:r})}const In=Ar({name:"Popover",common:er,peers:{Scrollbar:Br},self:_n}),Ce={top:"bottom",bottom:"top",left:"right",right:"left"},_="var(--n-arrow-height) * 1.414",Tn=k([D("popover",`
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 position: relative;
 font-size: var(--n-font-size);
 color: var(--n-text-color);
 box-shadow: var(--n-box-shadow);
 word-break: break-word;
 `,[k(">",[D("scrollbar",`
 height: inherit;
 max-height: inherit;
 `)]),X("raw",`
 background-color: var(--n-color);
 border-radius: var(--n-border-radius);
 `,[X("scrollable",[X("show-header-or-footer","padding: var(--n-padding);")])]),j("header",`
 padding: var(--n-padding);
 border-bottom: 1px solid var(--n-divider-color);
 transition: border-color .3s var(--n-bezier);
 `),j("footer",`
 padding: var(--n-padding);
 border-top: 1px solid var(--n-divider-color);
 transition: border-color .3s var(--n-bezier);
 `),M("scrollable, show-header-or-footer",[j("content",`
 padding: var(--n-padding);
 `)])]),D("popover-shared",`
 transform-origin: inherit;
 `,[D("popover-arrow-wrapper",`
 position: absolute;
 overflow: hidden;
 pointer-events: none;
 `,[D("popover-arrow",`
 transition: background-color .3s var(--n-bezier);
 position: absolute;
 display: block;
 width: calc(${_});
 height: calc(${_});
 box-shadow: 0 0 8px 0 rgba(0, 0, 0, .12);
 transform: rotate(45deg);
 background-color: var(--n-color);
 pointer-events: all;
 `)]),k("&.popover-transition-enter-from, &.popover-transition-leave-to",`
 opacity: 0;
 transform: scale(.85);
 `),k("&.popover-transition-enter-to, &.popover-transition-leave-from",`
 transform: scale(1);
 opacity: 1;
 `),k("&.popover-transition-enter-active",`
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 opacity .15s var(--n-bezier-ease-out),
 transform .15s var(--n-bezier-ease-out);
 `),k("&.popover-transition-leave-active",`
 transition:
 box-shadow .3s var(--n-bezier),
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier),
 opacity .15s var(--n-bezier-ease-in),
 transform .15s var(--n-bezier-ease-in);
 `)]),B("top-start",`
 top: calc(${_} / -2);
 left: calc(${N("top-start")} - var(--v-offset-left));
 `),B("top",`
 top: calc(${_} / -2);
 transform: translateX(calc(${_} / -2)) rotate(45deg);
 left: 50%;
 `),B("top-end",`
 top: calc(${_} / -2);
 right: calc(${N("top-end")} + var(--v-offset-left));
 `),B("bottom-start",`
 bottom: calc(${_} / -2);
 left: calc(${N("bottom-start")} - var(--v-offset-left));
 `),B("bottom",`
 bottom: calc(${_} / -2);
 transform: translateX(calc(${_} / -2)) rotate(45deg);
 left: 50%;
 `),B("bottom-end",`
 bottom: calc(${_} / -2);
 right: calc(${N("bottom-end")} + var(--v-offset-left));
 `),B("left-start",`
 left: calc(${_} / -2);
 top: calc(${N("left-start")} - var(--v-offset-top));
 `),B("left",`
 left: calc(${_} / -2);
 transform: translateY(calc(${_} / -2)) rotate(45deg);
 top: 50%;
 `),B("left-end",`
 left: calc(${_} / -2);
 bottom: calc(${N("left-end")} + var(--v-offset-top));
 `),B("right-start",`
 right: calc(${_} / -2);
 top: calc(${N("right-start")} - var(--v-offset-top));
 `),B("right",`
 right: calc(${_} / -2);
 transform: translateY(calc(${_} / -2)) rotate(45deg);
 top: 50%;
 `),B("right-end",`
 right: calc(${_} / -2);
 bottom: calc(${N("right-end")} + var(--v-offset-top));
 `),...$n({top:["right-start","left-start"],right:["top-end","bottom-end"],bottom:["right-end","left-end"],left:["top-start","bottom-start"]},(e,r)=>{const o=["right","left"].includes(r),n=o?"width":"height";return e.map(a=>{const t=a.split("-")[1]==="end",s=`calc((${`var(--v-target-${n}, 0px)`} - ${_}) / 2)`,c=N(a);return k(`[v-placement="${a}"] >`,[D("popover-shared",[M("center-arrow",[D("popover-arrow",`${r}: calc(max(${s}, ${c}) ${t?"+":"-"} var(--v-offset-${o?"left":"top"}));`)])])])})})]);function N(e){return["top","bottom"].includes(e.split("-")[0])?"var(--n-arrow-offset)":"var(--n-arrow-offset-vertical)"}function B(e,r){const o=e.split("-")[0],n=["top","bottom"].includes(o)?"height: var(--n-space-arrow);":"width: var(--n-space-arrow);";return k(`[v-placement="${e}"] >`,[D("popover-shared",`
 margin-${Ce[o]}: var(--n-space);
 `,[M("show-arrow",`
 margin-${Ce[o]}: var(--n-space-arrow);
 `),M("overlap",`
 margin: 0;
 `),Mr("popover-arrow-wrapper",`
 right: 0;
 left: 0;
 top: 0;
 bottom: 0;
 ${o}: 100%;
 ${Ce[o]}: auto;
 ${n}
 `,[D("popover-arrow",r)])])])}const dr=Object.assign(Object.assign({},le.props),{to:de.propTo,show:Boolean,trigger:String,showArrow:Boolean,delay:Number,duration:Number,raw:Boolean,arrowPointToCenter:Boolean,arrowClass:String,arrowStyle:[String,Object],arrowWrapperClass:String,arrowWrapperStyle:[String,Object],displayDirective:String,x:Number,y:Number,flip:Boolean,overlap:Boolean,placement:String,width:[Number,String],keepAliveOnHover:Boolean,scrollable:Boolean,contentClass:String,contentStyle:[Object,String],headerClass:String,headerStyle:[Object,String],footerClass:String,footerStyle:[Object,String],internalDeactivateImmediately:Boolean,animated:Boolean,onClickoutside:Function,internalTrapFocus:Boolean,internalOnAfterLeave:Function,minWidth:Number,maxWidth:Number});function Rn({arrowClass:e,arrowStyle:r,arrowWrapperClass:o,arrowWrapperStyle:n,clsPrefix:a}){return x("div",{key:"__popover-arrow__",style:n,class:[`${a}-popover-arrow-wrapper`,o]},x("div",{class:[`${a}-popover-arrow`,e],style:r}))}const An=_e({name:"PopoverBody",inheritAttrs:!1,props:dr,setup(e,{slots:r,attrs:o}){const{namespaceRef:n,mergedClsPrefixRef:a,inlineThemeDisabled:t,mergedRtlRef:l}=rr(e),s=le("Popover","-popover",Tn,In,e,a),c=or("Popover",l,a),f=U(null),u=Hr("NPopover"),v=U(null),m=U(e.show),y=U(!1);nr(()=>{const{show:b}=e;b&&!uo()&&!e.internalDeactivateImmediately&&(y.value=!0)});const C=K(()=>{const{trigger:b,onClickoutside:P}=e,$=[],{positionManuallyRef:{value:h}}=u;return h||(b==="click"&&!P&&$.push([Fe,H,void 0,{capture:!0}]),b==="hover"&&$.push([co,R])),P&&$.push([Fe,H,void 0,{capture:!0}]),(e.displayDirective==="show"||e.animated&&y.value)&&$.push([Nr,e.show]),$}),i=K(()=>{const{common:{cubicBezierEaseInOut:b,cubicBezierEaseIn:P,cubicBezierEaseOut:$},self:{space:h,spaceArrow:L,padding:J,fontSize:d,textColor:O,dividerColor:I,color:F,boxShadow:W,borderRadius:ee,arrowHeight:re,arrowOffset:E,arrowOffsetVertical:ve}}=s.value;return{"--n-box-shadow":W,"--n-bezier":b,"--n-bezier-ease-in":P,"--n-bezier-ease-out":$,"--n-font-size":d,"--n-text-color":O,"--n-color":F,"--n-divider-color":I,"--n-border-radius":ee,"--n-arrow-height":re,"--n-arrow-offset":E,"--n-arrow-offset-vertical":ve,"--n-padding":J,"--n-space":h,"--n-space-arrow":L}}),g=K(()=>{const b=e.width==="trigger"?void 0:be(e.width),P=[];b&&P.push({width:b});const{maxWidth:$,minWidth:h}=e;return $&&P.push({maxWidth:be($)}),h&&P.push({maxWidth:be(h)}),t||P.push(i.value),P}),p=t?tr("popover",void 0,i,e):void 0;u.setBodyInstance({syncPosition:w}),zr(()=>{u.setBodyInstance(null)}),Er(te(e,"show"),b=>{e.animated||(b?m.value=!0:m.value=!1)});function w(){var b;(b=f.value)===null||b===void 0||b.syncPosition()}function z(b){e.trigger==="hover"&&e.keepAliveOnHover&&e.show&&u.handleMouseEnter(b)}function T(b){e.trigger==="hover"&&e.keepAliveOnHover&&u.handleMouseLeave(b)}function R(b){e.trigger==="hover"&&!G().contains(De(b))&&u.handleMouseMoveOutside(b)}function H(b){(e.trigger==="click"&&!G().contains(De(b))||e.onClickoutside)&&u.handleClickOutside(b)}function G(){return u.getTriggerElement()}ae(jr,v),ae(Ur,null),ae(Kr,null);function V(){if(p==null||p.onRender(),!(e.displayDirective==="show"||e.show||e.animated&&y.value))return null;let P;const $=u.internalRenderBodyRef.value,{value:h}=a;if($)P=$([`${h}-popover-shared`,(c==null?void 0:c.value)&&`${h}-popover--rtl`,p==null?void 0:p.themeClass.value,e.overlap&&`${h}-popover-shared--overlap`,e.showArrow&&`${h}-popover-shared--show-arrow`,e.arrowPointToCenter&&`${h}-popover-shared--center-arrow`],v,g.value,z,T);else{const{value:L}=u.extraClassRef,{internalTrapFocus:J}=e,d=!He(r.header)||!He(r.footer),O=()=>{var I,F;const W=d?x(Lr,null,ne(r.header,E=>E?x("div",{class:[`${h}-popover__header`,e.headerClass],style:e.headerStyle},E):null),ne(r.default,E=>E?x("div",{class:[`${h}-popover__content`,e.contentClass],style:e.contentStyle},r):null),ne(r.footer,E=>E?x("div",{class:[`${h}-popover__footer`,e.footerClass],style:e.footerStyle},E):null)):e.scrollable?(I=r.default)===null||I===void 0?void 0:I.call(r):x("div",{class:[`${h}-popover__content`,e.contentClass],style:e.contentStyle},r),ee=e.scrollable?x(Wr,{themeOverrides:s.value.peerOverrides.Scrollbar,theme:s.value.peers.Scrollbar,contentClass:d?void 0:`${h}-popover__content ${(F=e.contentClass)!==null&&F!==void 0?F:""}`,contentStyle:d?void 0:e.contentStyle},{default:()=>W}):W,re=e.showArrow?Rn({arrowClass:e.arrowClass,arrowStyle:e.arrowStyle,arrowWrapperClass:e.arrowWrapperClass,arrowWrapperStyle:e.arrowWrapperStyle,clsPrefix:h}):null;return[ee,re]};P=x("div",Dr({class:[`${h}-popover`,`${h}-popover-shared`,(c==null?void 0:c.value)&&`${h}-popover--rtl`,p==null?void 0:p.themeClass.value,L.map(I=>`${h}-${I}`),{[`${h}-popover--scrollable`]:e.scrollable,[`${h}-popover--show-header-or-footer`]:d,[`${h}-popover--raw`]:e.raw,[`${h}-popover-shared--overlap`]:e.overlap,[`${h}-popover-shared--show-arrow`]:e.showArrow,[`${h}-popover-shared--center-arrow`]:e.arrowPointToCenter}],ref:v,style:g.value,onKeydown:u.handleKeydown,onMouseenter:z,onMouseleave:T},o),J?x(Fr,{active:e.show,autoFocus:!0},{default:O}):O())}return ar(P,C.value)}return{displayed:y,namespace:n,isMounted:u.isMountedRef,zIndex:u.zIndexRef,followerRef:f,adjustedTo:de(e),followerEnabled:m,renderContentNode:V}},render(){return x(ao,{ref:"followerRef",zIndex:this.zIndex,show:this.show,enabled:this.followerEnabled,to:this.adjustedTo,x:this.x,y:this.y,flip:this.flip,placement:this.placement,containerClass:this.namespace,overlap:this.overlap,width:this.width==="trigger"?"target":void 0,teleportDisabled:this.adjustedTo===de.tdkey},{default:()=>this.animated?x(kr,{name:"popover-transition",appear:this.isMounted,onEnter:()=>{this.followerEnabled=!0},onAfterLeave:()=>{var e;(e=this.internalOnAfterLeave)===null||e===void 0||e.call(this),this.followerEnabled=!1,this.displayed=!1}},{default:this.renderContentNode}):this.renderContentNode()})}}),Bn=Object.keys(dr),Mn={focus:["onFocus","onBlur"],click:["onClick"],hover:["onMouseenter","onMouseleave"],manual:[],nested:["onFocus","onBlur","onMouseenter","onMouseleave","onClick"]};function kn(e,r,o){Mn[r].forEach(n=>{e.props?e.props=Object.assign({},e.props):e.props={};const a=e.props[n],t=o[n];a?e.props[n]=(...l)=>{a(...l),t(...l)}:e.props[n]=t})}const zn={show:{type:Boolean,default:void 0},defaultShow:Boolean,showArrow:{type:Boolean,default:!0},trigger:{type:String,default:"hover"},delay:{type:Number,default:100},duration:{type:Number,default:100},raw:Boolean,placement:{type:String,default:"top"},x:Number,y:Number,arrowPointToCenter:Boolean,disabled:Boolean,getDisabled:Function,displayDirective:{type:String,default:"if"},arrowClass:String,arrowStyle:[String,Object],arrowWrapperClass:String,arrowWrapperStyle:[String,Object],flip:{type:Boolean,default:!0},animated:{type:Boolean,default:!0},width:{type:[Number,String],default:void 0},overlap:Boolean,keepAliveOnHover:{type:Boolean,default:!0},zIndex:Number,to:de.propTo,scrollable:Boolean,contentClass:String,contentStyle:[Object,String],headerClass:String,headerStyle:[Object,String],footerClass:String,footerStyle:[Object,String],onClickoutside:Function,"onUpdate:show":[Function,Array],onUpdateShow:[Function,Array],internalDeactivateImmediately:Boolean,internalSyncTargetWithParent:Boolean,internalInheritedEventHandlers:{type:Array,default:()=>[]},internalTrapFocus:Boolean,internalExtraClass:{type:Array,default:()=>[]},onShow:[Function,Array],onHide:[Function,Array],arrow:{type:Boolean,default:void 0},minWidth:Number,maxWidth:Number},En=Object.assign(Object.assign(Object.assign({},le.props),zn),{internalOnAfterLeave:Function,internalRenderBody:Function}),Yn=_e({name:"Popover",inheritAttrs:!1,props:En,slots:Object,__popover__:!0,setup(e){const r=qr(),o=U(null),n=K(()=>e.show),a=U(e.defaultShow),t=to(n,a),l=Le(()=>e.disabled?!1:t.value),s=()=>{if(e.disabled)return!0;const{getDisabled:d}=e;return!!(d!=null&&d())},c=()=>s()?!1:t.value,f=io(e,["arrow","showArrow"]),u=K(()=>e.overlap?!1:f.value);let v=null;const m=U(null),y=U(null),C=Le(()=>e.x!==void 0&&e.y!==void 0);function i(d){const{"onUpdate:show":O,onUpdateShow:I,onShow:F,onHide:W}=e;a.value=d,O&&oe(O,d),I&&oe(I,d),d&&F&&oe(F,!0),d&&W&&oe(W,!1)}function g(){v&&v.syncPosition()}function p(){const{value:d}=m;d&&(window.clearTimeout(d),m.value=null)}function w(){const{value:d}=y;d&&(window.clearTimeout(d),y.value=null)}function z(){const d=s();if(e.trigger==="focus"&&!d){if(c())return;i(!0)}}function T(){const d=s();if(e.trigger==="focus"&&!d){if(!c())return;i(!1)}}function R(){const d=s();if(e.trigger==="hover"&&!d){if(w(),m.value!==null||c())return;const O=()=>{i(!0),m.value=null},{delay:I}=e;I===0?O():m.value=window.setTimeout(O,I)}}function H(){const d=s();if(e.trigger==="hover"&&!d){if(p(),y.value!==null||!c())return;const O=()=>{i(!1),y.value=null},{duration:I}=e;I===0?O():y.value=window.setTimeout(O,I)}}function G(){H()}function V(d){var O;c()&&(e.trigger==="click"&&(p(),w(),i(!1)),(O=e.onClickoutside)===null||O===void 0||O.call(e,d))}function b(){if(e.trigger==="click"&&!s()){p(),w();const d=!c();i(d)}}function P(d){e.internalTrapFocus&&d.key==="Escape"&&(p(),w(),i(!1))}function $(d){a.value=d}function h(){var d;return(d=o.value)===null||d===void 0?void 0:d.targetRef}function L(d){v=d}return ae("NPopover",{getTriggerElement:h,handleKeydown:P,handleMouseEnter:R,handleMouseLeave:H,handleClickOutside:V,handleMouseMoveOutside:G,setBodyInstance:L,positionManuallyRef:C,isMountedRef:r,zIndexRef:te(e,"zIndex"),extraClassRef:te(e,"internalExtraClass"),internalRenderBodyRef:te(e,"internalRenderBody")}),nr(()=>{t.value&&s()&&i(!1)}),{binderInstRef:o,positionManually:C,mergedShowConsideringDisabledProp:l,uncontrolledShow:a,mergedShowArrow:u,getMergedShow:c,setShow:$,handleClick:b,handleMouseEnter:R,handleMouseLeave:H,handleFocus:z,handleBlur:T,syncPosition:g}},render(){var e;const{positionManually:r,$slots:o}=this;let n,a=!1;if(!r&&(n=Gr(o,"trigger"),n)){n=Yr(n),n=n.type===Vr?x("span",[n]):n;const t={onClick:this.handleClick,onMouseenter:this.handleMouseEnter,onMouseleave:this.handleMouseLeave,onFocus:this.handleFocus,onBlur:this.handleBlur};if(!((e=n.type)===null||e===void 0)&&e.__popover__)a=!0,n.props||(n.props={internalSyncTargetWithParent:!0,internalInheritedEventHandlers:[]}),n.props.internalSyncTargetWithParent=!0,n.props.internalInheritedEventHandlers?n.props.internalInheritedEventHandlers=[t,...n.props.internalInheritedEventHandlers]:n.props.internalInheritedEventHandlers=[t];else{const{internalInheritedEventHandlers:l}=this,s=[t,...l],c={onBlur:f=>{s.forEach(u=>{u.onBlur(f)})},onFocus:f=>{s.forEach(u=>{u.onFocus(f)})},onClick:f=>{s.forEach(u=>{u.onClick(f)})},onMouseenter:f=>{s.forEach(u=>{u.onMouseenter(f)})},onMouseleave:f=>{s.forEach(u=>{u.onMouseleave(f)})}};kn(n,l?"nested":r?"manual":this.trigger,c)}}return x(lo,{ref:"binderInstRef",syncTarget:!a,syncTargetWithParent:this.internalSyncTargetWithParent},{default:()=>{this.mergedShowConsideringDisabledProp;const t=this.getMergedShow();return[this.internalTrapFocus&&t?ar(x("div",{style:{position:"fixed",top:0,right:0,bottom:0,left:0}}),[[Xr,{enabled:t,zIndex:this.zIndex}]]):null,r?null:x(so,null,{default:()=>n}),x(An,Jr(this.$props,Bn,Object.assign(Object.assign({},this.$attrs),{showArrow:this.mergedShowArrow,show:t})),{default:()=>{var l,s;return(s=(l=this.$slots).default)===null||s===void 0?void 0:s.call(l)},header:()=>{var l,s;return(s=(l=this.$slots).header)===null||s===void 0?void 0:s.call(l)},footer:()=>{var l,s;return(s=(l=this.$slots).footer)===null||s===void 0?void 0:s.call(l)}})]}})}}),Hn={closeIconSizeTiny:"12px",closeIconSizeSmall:"12px",closeIconSizeMedium:"14px",closeIconSizeLarge:"14px",closeSizeTiny:"16px",closeSizeSmall:"16px",closeSizeMedium:"18px",closeSizeLarge:"18px",padding:"0 7px",closeMargin:"0 0 0 4px"};function Fn(e){const{textColor2:r,primaryColorHover:o,primaryColorPressed:n,primaryColor:a,infoColor:t,successColor:l,warningColor:s,errorColor:c,baseColor:f,borderColor:u,opacityDisabled:v,tagColor:m,closeIconColor:y,closeIconColorHover:C,closeIconColorPressed:i,borderRadiusSmall:g,fontSizeMini:p,fontSizeTiny:w,fontSizeSmall:z,fontSizeMedium:T,heightMini:R,heightTiny:H,heightSmall:G,heightMedium:V,closeColorHover:b,closeColorPressed:P,buttonColor2Hover:$,buttonColor2Pressed:h,fontWeightStrong:L}=e;return Object.assign(Object.assign({},Hn),{closeBorderRadius:g,heightTiny:R,heightSmall:H,heightMedium:G,heightLarge:V,borderRadius:g,opacityDisabled:v,fontSizeTiny:p,fontSizeSmall:w,fontSizeMedium:z,fontSizeLarge:T,fontWeightStrong:L,textColorCheckable:r,textColorHoverCheckable:r,textColorPressedCheckable:r,textColorChecked:f,colorCheckable:"#0000",colorHoverCheckable:$,colorPressedCheckable:h,colorChecked:a,colorCheckedHover:o,colorCheckedPressed:n,border:`1px solid ${u}`,textColor:r,color:m,colorBordered:"rgb(250, 250, 252)",closeIconColor:y,closeIconColorHover:C,closeIconColorPressed:i,closeColorHover:b,closeColorPressed:P,borderPrimary:`1px solid ${S(a,{alpha:.3})}`,textColorPrimary:a,colorPrimary:S(a,{alpha:.12}),colorBorderedPrimary:S(a,{alpha:.1}),closeIconColorPrimary:a,closeIconColorHoverPrimary:a,closeIconColorPressedPrimary:a,closeColorHoverPrimary:S(a,{alpha:.12}),closeColorPressedPrimary:S(a,{alpha:.18}),borderInfo:`1px solid ${S(t,{alpha:.3})}`,textColorInfo:t,colorInfo:S(t,{alpha:.12}),colorBorderedInfo:S(t,{alpha:.1}),closeIconColorInfo:t,closeIconColorHoverInfo:t,closeIconColorPressedInfo:t,closeColorHoverInfo:S(t,{alpha:.12}),closeColorPressedInfo:S(t,{alpha:.18}),borderSuccess:`1px solid ${S(l,{alpha:.3})}`,textColorSuccess:l,colorSuccess:S(l,{alpha:.12}),colorBorderedSuccess:S(l,{alpha:.1}),closeIconColorSuccess:l,closeIconColorHoverSuccess:l,closeIconColorPressedSuccess:l,closeColorHoverSuccess:S(l,{alpha:.12}),closeColorPressedSuccess:S(l,{alpha:.18}),borderWarning:`1px solid ${S(s,{alpha:.35})}`,textColorWarning:s,colorWarning:S(s,{alpha:.15}),colorBorderedWarning:S(s,{alpha:.12}),closeIconColorWarning:s,closeIconColorHoverWarning:s,closeIconColorPressedWarning:s,closeColorHoverWarning:S(s,{alpha:.12}),closeColorPressedWarning:S(s,{alpha:.18}),borderError:`1px solid ${S(c,{alpha:.23})}`,textColorError:c,colorError:S(c,{alpha:.1}),colorBorderedError:S(c,{alpha:.08}),closeIconColorError:c,closeIconColorHoverError:c,closeIconColorPressedError:c,closeColorHoverError:S(c,{alpha:.12}),closeColorPressedError:S(c,{alpha:.18})})}const Dn={common:er,self:Fn},Ln={color:Object,type:{type:String,default:"default"},round:Boolean,size:String,closable:Boolean,disabled:{type:Boolean,default:void 0}},Wn=D("tag",`
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
`,[M("strong",`
 font-weight: var(--n-font-weight-strong);
 `),j("border",`
 pointer-events: none;
 position: absolute;
 left: 0;
 right: 0;
 top: 0;
 bottom: 0;
 border-radius: inherit;
 border: var(--n-border);
 transition: border-color .3s var(--n-bezier);
 `),j("icon",`
 display: flex;
 margin: 0 4px 0 0;
 color: var(--n-text-color);
 transition: color .3s var(--n-bezier);
 font-size: var(--n-avatar-size-override);
 `),j("avatar",`
 display: flex;
 margin: 0 6px 0 0;
 `),j("close",`
 margin: var(--n-close-margin);
 transition:
 background-color .3s var(--n-bezier),
 color .3s var(--n-bezier);
 `),M("round",`
 padding: 0 calc(var(--n-height) / 3);
 border-radius: calc(var(--n-height) / 2);
 `,[j("icon",`
 margin: 0 4px 0 calc((var(--n-height) - 8px) / -2);
 `),j("avatar",`
 margin: 0 6px 0 calc((var(--n-height) - 8px) / -2);
 `),M("closable",`
 padding: 0 calc(var(--n-height) / 4) 0 calc(var(--n-height) / 3);
 `)]),M("icon, avatar",[M("round",`
 padding: 0 calc(var(--n-height) / 3) 0 calc(var(--n-height) / 2);
 `)]),M("disabled",`
 cursor: not-allowed !important;
 opacity: var(--n-opacity-disabled);
 `),M("checkable",`
 cursor: pointer;
 box-shadow: none;
 color: var(--n-text-color-checkable);
 background-color: var(--n-color-checkable);
 `,[X("disabled",[k("&:hover","background-color: var(--n-color-hover-checkable);",[X("checked","color: var(--n-text-color-hover-checkable);")]),k("&:active","background-color: var(--n-color-pressed-checkable);",[X("checked","color: var(--n-text-color-pressed-checkable);")])]),M("checked",`
 color: var(--n-text-color-checked);
 background-color: var(--n-color-checked);
 `,[X("disabled",[k("&:hover","background-color: var(--n-color-checked-hover);"),k("&:active","background-color: var(--n-color-checked-pressed);")])])])]),Nn=Object.assign(Object.assign(Object.assign({},le.props),Ln),{bordered:{type:Boolean,default:void 0},checked:Boolean,checkable:Boolean,strong:Boolean,triggerClickOnClose:Boolean,onClose:[Array,Function],onMouseenter:Function,onMouseleave:Function,"onUpdate:checked":Function,onUpdateChecked:Function,internalCloseFocusable:{type:Boolean,default:!0},internalCloseIsButtonTag:{type:Boolean,default:!0},onCheckedChange:Function}),jn=eo("n-tag"),Vn=_e({name:"Tag",props:Nn,slots:Object,setup(e){const r=U(null),{mergedBorderedRef:o,mergedClsPrefixRef:n,inlineThemeDisabled:a,mergedRtlRef:t,mergedComponentPropsRef:l}=rr(e),s=K(()=>{var i,g;return e.size||((g=(i=l==null?void 0:l.value)===null||i===void 0?void 0:i.Tag)===null||g===void 0?void 0:g.size)||"medium"}),c=le("Tag","-tag",Wn,Dn,e,n);ae(jn,{roundRef:te(e,"round")});function f(){if(!e.disabled&&e.checkable){const{checked:i,onCheckedChange:g,onUpdateChecked:p,"onUpdate:checked":w}=e;p&&p(!i),w&&w(!i),g&&g(!i)}}function u(i){if(e.triggerClickOnClose||i.stopPropagation(),!e.disabled){const{onClose:g}=e;g&&oe(g,i)}}const v={setTextContent(i){const{value:g}=r;g&&(g.textContent=i)}},m=or("Tag",t,n),y=K(()=>{const{type:i,color:{color:g,textColor:p}={}}=e,w=s.value,{common:{cubicBezierEaseInOut:z},self:{padding:T,closeMargin:R,borderRadius:H,opacityDisabled:G,textColorCheckable:V,textColorHoverCheckable:b,textColorPressedCheckable:P,textColorChecked:$,colorCheckable:h,colorHoverCheckable:L,colorPressedCheckable:J,colorChecked:d,colorCheckedHover:O,colorCheckedPressed:I,closeBorderRadius:F,fontWeightStrong:W,[A("colorBordered",i)]:ee,[A("closeSize",w)]:re,[A("closeIconSize",w)]:E,[A("fontSize",w)]:ve,[A("height",w)]:Ae,[A("color",i)]:ur,[A("textColor",i)]:fr,[A("border",i)]:hr,[A("closeIconColor",i)]:Be,[A("closeIconColorHover",i)]:vr,[A("closeIconColorPressed",i)]:gr,[A("closeColorHover",i)]:pr,[A("closeColorPressed",i)]:br}}=c.value,se=Zr(R);return{"--n-font-weight-strong":W,"--n-avatar-size-override":`calc(${Ae} - 8px)`,"--n-bezier":z,"--n-border-radius":H,"--n-border":hr,"--n-close-icon-size":E,"--n-close-color-pressed":br,"--n-close-color-hover":pr,"--n-close-border-radius":F,"--n-close-icon-color":Be,"--n-close-icon-color-hover":vr,"--n-close-icon-color-pressed":gr,"--n-close-icon-color-disabled":Be,"--n-close-margin-top":se.top,"--n-close-margin-right":se.right,"--n-close-margin-bottom":se.bottom,"--n-close-margin-left":se.left,"--n-close-size":re,"--n-color":g||(o.value?ee:ur),"--n-color-checkable":h,"--n-color-checked":d,"--n-color-checked-hover":O,"--n-color-checked-pressed":I,"--n-color-hover-checkable":L,"--n-color-pressed-checkable":J,"--n-font-size":ve,"--n-height":Ae,"--n-opacity-disabled":G,"--n-padding":T,"--n-text-color":p||fr,"--n-text-color-checkable":V,"--n-text-color-checked":$,"--n-text-color-hover-checkable":b,"--n-text-color-pressed-checkable":P}}),C=a?tr("tag",K(()=>{let i="";const{type:g,color:{color:p,textColor:w}={}}=e;return i+=g[0],i+=s.value[0],p&&(i+=`a${We(p)}`),w&&(i+=`b${We(w)}`),o.value&&(i+="c"),i}),y,e):void 0;return Object.assign(Object.assign({},v),{rtlEnabled:m,mergedClsPrefix:n,contentRef:r,mergedBordered:o,handleClick:f,handleCloseClick:u,cssVars:a?void 0:y,themeClass:C==null?void 0:C.themeClass,onRender:C==null?void 0:C.onRender})},render(){var e,r;const{mergedClsPrefix:o,rtlEnabled:n,closable:a,color:{borderColor:t}={},round:l,onRender:s,$slots:c}=this;s==null||s();const f=ne(c.avatar,v=>v&&x("div",{class:`${o}-tag__avatar`},v)),u=ne(c.icon,v=>v&&x("div",{class:`${o}-tag__icon`},v));return x("div",{class:[`${o}-tag`,this.themeClass,{[`${o}-tag--rtl`]:n,[`${o}-tag--strong`]:this.strong,[`${o}-tag--disabled`]:this.disabled,[`${o}-tag--checkable`]:this.checkable,[`${o}-tag--checked`]:this.checkable&&this.checked,[`${o}-tag--round`]:l,[`${o}-tag--avatar`]:f,[`${o}-tag--icon`]:u,[`${o}-tag--closable`]:a}],style:this.cssVars,onClick:this.handleClick,onMouseenter:this.onMouseenter,onMouseleave:this.onMouseleave},u||f,x("span",{class:`${o}-tag__content`,ref:"contentRef"},(r=(e=this.$slots).default)===null||r===void 0?void 0:r.call(e)),!this.checkable&&a?x(Qr,{clsPrefix:o,class:`${o}-tag__close`,disabled:this.disabled,onClick:this.handleCloseClick,focusable:this.internalCloseFocusable,round:l,isButtonTag:this.internalCloseIsButtonTag,absolute:!0}):null,!this.checkable&&this.mergedBordered?x("div",{class:`${o}-tag__border`,style:{borderColor:t}}):null)}});export{Yn as N,Vn as a,In as b,Hn as c,zn as p,Rn as r,_n as s,io as u};

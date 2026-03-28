
define([], function() {
const RequestParameter = {
    environmentFunction: () => { },
    setEnvironment: callback => RequestParameter.environmentFunction = callback,
}
    // 返回提供者
    return RequestParameter;
});
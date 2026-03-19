{
  "properties" : { },
  "id" : "errorlogdetail00000000000000001",
  "script" : null,
  "groupId" : "9ec6f9ec92d24a369952bb13eddc134f",
  "name" : "错误日志详情",
  "createTime" : null,
  "updateTime" : 1773905761331,
  "lock" : "0",
  "createBy" : null,
  "updateBy" : "admin",
  "path" : "/error/detail",
  "method" : "GET",
  "parameters" : [ {
    "name" : "id",
    "value" : "dec68437ff7b49c581672aceacf58379",
    "description" : null,
    "required" : true,
    "dataType" : "String",
    "type" : null,
    "defaultValue" : null,
    "validateType" : null,
    "error" : null,
    "expression" : null,
    "children" : null
  } ],
  "options" : [ ],
  "requestBody" : "",
  "headers" : [ ],
  "paths" : [ ],
  "responseBody" : "{\n    \"code\": 200,\n    \"message\": \"success\",\n    \"data\": {\n        \"id\": \"dec68437ff7b49c581672aceacf58379\",\n        \"requestPath\": \"/error/detail\",\n        \"requestMethod\": \"GET\",\n        \"requestParams\": {\n            \"id\": \"1\"\n        },\n        \"requestHeaders\": {\n            \"host\": \"localhost:8089\",\n            \"connection\": \"keep-alive\",\n            \"magic-request-script-id\": \"errorlogdetail00000000000000001\",\n            \"magic-token\": \"3e9f0a3858fd5c7c7e6bba1511a87ca9\",\n            \"magic-request-breakpoints\": \"\",\n            \"authorization\": \"Bearer null\",\n            \"sec-ch-ua-platform\": \"\\\"Windows\\\"\",\n            \"magic-request-client-id\": \"a0fec8344afd489a\",\n            \"sec-ch-ua\": \"\\\"Not-A.Brand\\\";v=\\\"24\\\", \\\"Chromium\\\";v=\\\"146\\\"\",\n            \"sec-ch-ua-mobile\": \"?0\",\n            \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36\",\n            \"accept\": \"application/json, text/plain, */*\",\n            \"content-type\": \"application/x-www-form-urlencoded\",\n            \"sec-fetch-site\": \"same-origin\",\n            \"sec-fetch-mode\": \"cors\",\n            \"sec-fetch-dest\": \"empty\",\n            \"referer\": \"http://localhost:8089/magic/web/index.html\",\n            \"accept-encoding\": \"gzip, deflate, br, zstd\",\n            \"accept-language\": \"zh-CN,zh;q=0.9\",\n            \"cookie\": \"Authorization=Wi_R7Kt9IRYiHF3E9_Qw8hT3Dzsz0GsRrI__; JSESSIONID=6AB848CF23DA42E638F056E99CF89F11\"\n        },\n        \"userId\": null,\n        \"ipAddress\": \"0:0:0:0:0:0:0:1\",\n        \"exceptionClass\": \"java.lang.NullPointerException\",\n        \"exceptionMessage\": \"对象为空\",\n        \"stackTrace\": \"java.lang.NullPointerException: 对象为空\\r\\n\\tat org.ssssssss.script.runtime.handle.FunctionCallHandle.invoke_method(FunctionCallHandle.java:133)\\r\\n\\tat MagicScript_14.execute(MagicScript_14.ms:8)\\r\\n\\tat org.ssssssss.script.MagicScript.execute(MagicScript.java:122)\\r\\n\\tat org.ssssssss.magicapi.utils.ScriptManager.executeScript(ScriptManager.java:25)\\r\\n\\tat org.ssssssss.magicapi.core.web.RequestHandler.invokeRequest(RequestHandler.java:336)\\r\\n\\tat org.ssssssss.magicapi.core.web.RequestHandler.invoke(RequestHandler.java:178)\\r\\n\\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\\r\\n\\tat java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)\\r\\n\\tat java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\\r\\n\\tat java.base/java.lang.reflect.Method.invoke(Method.java:569)\\r\\n\\tat org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:205)\\r\\n\\tat org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:150)\\r\\n\\tat org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)\\r\\n\\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:884)\\r\\n\\tat org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797)\\r\\n\\tat org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)\\r\\n\\tat org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1081)\\r\\n\\tat org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:974)\\r\\n\\tat org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1011)\\r\\n\\tat org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903)\\r\\n\\tat jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)\\r\\n\\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885)\\r\\n\\tat jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:205)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:69)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100)\\r\\n\\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93)\\r\\n\\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:109)\\r\\n\\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201)\\r\\n\\tat org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.ssssssss.magicapi.servlet.jakarta.MagicJakartaCorsFilter.doFilter(MagicJakartaCorsFilter.java:15)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:174)\\r\\n\\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:149)\\r\\n\\tat org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:166)\\r\\n\\tat org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:90)\\r\\n\\tat org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:482)\\r\\n\\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115)\\r\\n\\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:93)\\r\\n\\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)\\r\\n\\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:341)\\r\\n\\tat org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:391)\\r\\n\\tat org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63)\\r\\n\\tat org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:894)\\r\\n\\tat org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1740)\\r\\n\\tat org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52)\\r\\n\\tat org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)\\r\\n\\tat org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)\\r\\n\\tat org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)\\r\\n\\tat java.base/java.lang.Thread.run(Thread.java:840)\\r\\n\",\n        \"createTime\": \"2026-03-19 15:32:43\"\n    },\n    \"timestamp\": 1773905738902,\n    \"executeTime\": 14\n}",
  "description" : "根据错误日志ID查询详情",
  "requestBodyDefinition" : null,
  "responseBodyDefinition" : {
    "name" : "",
    "value" : "",
    "description" : "",
    "required" : false,
    "dataType" : "Object",
    "type" : null,
    "defaultValue" : null,
    "validateType" : "",
    "error" : "",
    "expression" : "",
    "children" : [ {
      "name" : "code",
      "value" : "200",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "message",
      "value" : "success",
      "description" : "",
      "required" : false,
      "dataType" : "String",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "data",
      "value" : "",
      "description" : "",
      "required" : false,
      "dataType" : "Object",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ {
        "name" : "id",
        "value" : "errorlogid123456789",
        "description" : "错误日志唯一ID",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "requestPath",
        "value" : "/api/test",
        "description" : "请求路径",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "requestMethod",
        "value" : "POST",
        "description" : "请求方法",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "requestParams",
        "value" : "{\"key\":\"value\"}",
        "description" : "请求参数",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "requestHeaders",
        "value" : "{\"Content-Type\":\"application/json\"}",
        "description" : "请求头",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "userId",
        "value" : "user123",
        "description" : "用户ID",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "ipAddress",
        "value" : "127.0.0.1",
        "description" : "IP地址",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "exceptionClass",
        "value" : "java.lang.NullPointerException",
        "description" : "异常类名",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "exceptionMessage",
        "value" : "Object reference not set",
        "description" : "异常消息",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "stackTrace",
        "value" : "at com.example...",
        "description" : "堆栈跟踪",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      }, {
        "name" : "createTime",
        "value" : "2024-03-19T10:00:00",
        "description" : "创建时间",
        "required" : false,
        "dataType" : "String",
        "type" : null,
        "defaultValue" : null,
        "validateType" : "",
        "error" : "",
        "expression" : "",
        "children" : [ ]
      } ]
    }, {
      "name" : "timestamp",
      "value" : "1742365200000",
      "description" : "",
      "required" : false,
      "dataType" : "Long",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    }, {
      "name" : "executeTime",
      "value" : "10",
      "description" : "",
      "required" : false,
      "dataType" : "Integer",
      "type" : null,
      "defaultValue" : null,
      "validateType" : "",
      "error" : "",
      "expression" : "",
      "children" : [ ]
    } ]
  }
}
================================
import org.ssssssss.magicapi.utils.JsonUtils
import org.ssssssss.magicboot.service.MagicErrorLogService
import org.ssssssss.magicboot.entity.MagicErrorLog

if (!id) {
    exit 0, '错误日志ID不能为空'
}

var errorLog = db.table("magic_error_log").where().eq("id", id).selectOne()

if (!errorLog) {
    exit 0, '错误日志不存在'
}

if (errorLog.requestParams) {
    errorLog.requestParams = JsonUtils.readValue(errorLog.requestParams, Map.class)
}

if (errorLog.requestHeaders) {
    errorLog.requestHeaders = JsonUtils.readValue(errorLog.requestHeaders, Map.class)
}

return errorLog
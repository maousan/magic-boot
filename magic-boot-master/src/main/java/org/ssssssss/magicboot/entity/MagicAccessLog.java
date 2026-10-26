package org.ssssssss.magicboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * magic-api 接口访问日志：写操作（非 GET）与异常请求的审计记录，异步写入
 */
@TableName("magic_access_log")
public class MagicAccessLog {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("request_path")
    private String requestPath;

    @TableField("request_method")
    private String requestMethod;

    @TableField("request_params")
    private String requestParams;

    @TableField("user_id")
    private String userId;

    @TableField("ip_address")
    private String ipAddress;

    /** 接口耗时（毫秒） */
    @TableField("execute_time")
    private Integer executeTime;

    /** ok=正常返回 / error=抛异常 */
    @TableField("state")
    private String state;

    @TableField("exception_message")
    private String exceptionMessage;

    @TableField("create_time")
    private LocalDateTime createTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRequestPath() { return requestPath; }
    public void setRequestPath(String requestPath) { this.requestPath = requestPath; }
    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }
    public String getRequestParams() { return requestParams; }
    public void setRequestParams(String requestParams) { this.requestParams = requestParams; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public Integer getExecuteTime() { return executeTime; }
    public void setExecuteTime(Integer executeTime) { this.executeTime = executeTime; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getExceptionMessage() { return exceptionMessage; }
    public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

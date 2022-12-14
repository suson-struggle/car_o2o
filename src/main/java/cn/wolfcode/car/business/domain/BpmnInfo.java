package cn.wolfcode.car.business.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class BpmnInfo {
    /** 主键*/
    private Long id;

    /** 流程(图)名称*/
    private String bpmnName;

    /** 流程(图)类型*/
    private String bpmnType;

    /** 流程部署id*/
    private String deploymentId;

    /** activity流程定义生成的主键*/
    private String actProcessId;

    /** activity流程定义生成的key*/
    private String actProcessKey;

    /** 部署时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deployTime;

    /** 描述信息*/
    private String info;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBpmnName() {
        return bpmnName;
    }

    public void setBpmnName(String bpmnName) {
        this.bpmnName = bpmnName;
    }

    public String getBpmnType() {
        return bpmnType;
    }

    public void setBpmnType(String bpmnType) {
        this.bpmnType = bpmnType;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getActProcessId() {
        return actProcessId;
    }

    public void setActProcessId(String actProcessId) {
        this.actProcessId = actProcessId;
    }

    public String getActProcessKey() {
        return actProcessKey;
    }

    public void setActProcessKey(String actProcessKey) {
        this.actProcessKey = actProcessKey;
    }

    public Date getDeployTime() {
        return deployTime;
    }

    public void setDeployTime(Date deployTime) {
        this.deployTime = deployTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
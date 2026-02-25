package edu.cornell.mannlib.vivo.harvest.contextmodel;

import java.util.concurrent.Future;

public class ModuleRuntimeContext {

    private String moduleName;

    private Process processHandle;

    private Future<?> future;

    private Long pid;


    public ModuleRuntimeContext() {
    }

    public ModuleRuntimeContext(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Process getProcessHandle() {
        return processHandle;
    }

    public void setProcessHandle(Process processHandle) {
        this.processHandle = processHandle;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}

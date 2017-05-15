package net.mobctrl.model;

/**
 * Created by yb on 2017/5/12 11:53.
 */

public class UserInfo {

    /**
     * id : 500094680
     * pId : 500094676
     * name :  成员组
     * checked : False
     * disabled : True
     * biaoshi : 400007677
     * fubiaoshi : 500094676
     * jilileixing : 1
     */

    private String id;
    private String pId;
    private String name;
    private String checked;
    private String disabled;
    private String biaoshi;
    private String fubiaoshi;
    private String jilileixing;

    public UserInfo(String name, String biaoshi, String fubiaoshi) {
        this.name = name;
        this.biaoshi = biaoshi;
        this.fubiaoshi = fubiaoshi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getDisabled() {
        return disabled;
    }

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    public String getBiaoshi() {
        return biaoshi;
    }

    public void setBiaoshi(String biaoshi) {
        this.biaoshi = biaoshi;
    }

    public String getFubiaoshi() {
        return fubiaoshi;
    }

    public void setFubiaoshi(String fubiaoshi) {
        this.fubiaoshi = fubiaoshi;
    }

    public String getJilileixing() {
        return jilileixing;
    }

    public void setJilileixing(String jilileixing) {
        this.jilileixing = jilileixing;
    }
}

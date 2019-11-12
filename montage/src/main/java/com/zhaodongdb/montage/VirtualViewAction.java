package com.zhaodongdb.montage;

public enum VirtualViewAction {

    ACTION_OPEN_NEW_MONTAGE_PAGE("montage"),

    ACTION_OPEN_NEW_COMMON_PAGE("jump");

    VirtualViewAction(String action) {
        this.action = action;
    }

    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean equals(String action) {
        return this.action.equals(action);
    }

}

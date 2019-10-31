package com.zhaodongdb.montage;

import java.io.Serializable;
import java.util.Map;

/**
 * Created on 2018/1/6.
 * Description:
 *
 * @author bianyue
 */
public class PageData implements Serializable {

    private Map<String, String> templates;
    private String data;

    public Map<String, String> getTemplates() {
        return templates;
    }

    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

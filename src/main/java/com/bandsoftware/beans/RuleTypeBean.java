package com.bandsoftware.beans;

import com.bandsoftware.data.EspressoRuleObject;

/**
 * Created by Tyler on 4/21/14.
 */
public class RuleTypeBean {

    private String name;
    private int ident;
    private String method;
    private String template;

    public RuleTypeBean(int ident) {
        this.ident = ident;
        setName(EspressoRuleObject.RULE_TYPES.ruleName(ident));
    }

    public RuleTypeBean(String name, int ident) {
        this.ident = ident;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}

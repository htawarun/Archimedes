package com.bandsoftware.data;

/**
 * Created by Tyler on 4/5/14.
 */
public class EspressoRuleBean {

    private String entityName;
    private String attributeName;
    private String prop1;
    private String prop2;
    private String prop3;
    private String prop4;
    private String prop5;
    private String ruleText1;
    private String ruleText2;
    private String ruleText3;
    private String ruleText4;
    private String ruleText5;
    private String autoName;
    private String predicate;
    private String name;
    private String verbs;
    private boolean active = false;
    private String comments;
    private String ruleIdent;
    private String projectIdent;
    private String ident;

    /**
     * this is the prototype of a rule object
     * you must do a POST (201) to get the raw JSON Object
     * and then POST again with the rule values populated
     *  public String REST_PROTOTYPE = "\"ts\": $ts,"+
     "\"entity_name\": $entityName,"+
     "\"attribute_name\": $attributeName,"+
     "\"prop1\": $prop1,"+
     "\"prop2\": $prop2,"+
     "\"prop3\": $prop3,"+
     "\"prop4\": $prop4,"+
     "\"prop5\": $prop5,"+
     "\"rule_text1\": $ruleText1,"+
     "\"rule_text2\": $ruleText2,"+
     "\"rule_text3\": $ruleText3,"+
     "\"rule_text4\": $ruleText4,"+
     "\"rule_text5\": $ruleText5,"+
     "\"predicate\": $predicate,"+
     "\"name\": $name,"+
     "\"auto_name\": $autoName,"+
     "\"verbs\": $verbs,"+
     "\"comments\": $comments,"+
     "\"active\": $active,"+
     "\"project_ident\": $projectIdent,"+
     "\"ruletype_ident\": $ruleType";
     */



    public EspressoRuleBean(){
        super();
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }

    public String getProp3() {
        return prop3;
    }

    public void setProp3(String prop3) {
        this.prop3 = prop3;
    }

    public String getProp4() {
        return prop4;
    }

    public void setProp4(String prop4) {
        this.prop4 = prop4;
    }

    public String getProp5() {
        return prop5;
    }

    public void setProp5(String prop5) {
        this.prop5 = prop5;
    }

    public String getRuleText1() {
        return ruleText1;
    }

    public void setRuleText1(String ruleText1) {
        this.ruleText1 = ruleText1;
    }

    public String getRuleText2() {
        return ruleText2;
    }

    public void setRuleText2(String ruleText2) {
        this.ruleText2 = ruleText2;
    }

    public String getRuleText3() {
        return ruleText3;
    }

    public void setRuleText3(String ruleText3) {
        this.ruleText3 = ruleText3;
    }

    public String getRuleText4() {
        return ruleText4;
    }

    public void setRuleText4(String ruleText4) {
        this.ruleText4 = ruleText4;
    }

    public String getRuleText5() {
        return ruleText5;
    }

    public void setRuleText5(String ruleText5) {
        this.ruleText5 = ruleText5;
    }

    public String getAutoName() {
        return autoName;
    }

    public void setAutoName(String autoName) {
        this.autoName = autoName;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerbs() {
        return verbs;
    }

    public void setVerbs(String verbs) {
        this.verbs = verbs;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getRuleIdent() {
        return ruleIdent;
    }

    public void setRuleIdent(String ruleIdent) {
        this.ruleIdent = ruleIdent;
    }

    public String getProjectIdent() {
        return projectIdent;
    }

    public void setProjectIdent(String projectIdent) {
        this.projectIdent = projectIdent;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

}

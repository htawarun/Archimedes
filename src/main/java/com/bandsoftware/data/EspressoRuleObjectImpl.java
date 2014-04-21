package com.bandsoftware.data;

/**
 * Created by Tyler on 4/3/14.
 * '1', '2013-10-12 00:03:59', 'sum', NULL, 'Derive @{attribute_name} as sum(@{rule_text1}.@{rule_text3}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})', 'sum child attr into parent', NULL, '1', NULL
 * '2', '2013-10-12 00:03:59', 'count', NULL, 'Derive @{attribute_name} as count(@{rule_text1}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})', 'count child attr into parent', NULL, '2', NULL
 * '3', '2013-10-12 00:03:59', 'formula', NULL, 'Derive @{attribute_name} as @{rule_text1}', 'formula using table & parent attributes', NULL, '3', NULL
 * '4', '2013-10-12 00:03:59', 'parent copy', NULL, 'Derive @{attribute_name} as parentcopy(@{rule_text1}.@{rule_text2})', 'non-cascaded parent value', NULL, '4', NULL
 * '5', '2013-10-12 00:03:59', 'validation', NULL, 'Validation @{rule_text1}', 'multi-attribute validation', NULL, '5', NULL
 * '6', '2013-10-12 00:03:59', 'commit validation', NULL, 'Commit validation @{rule_text1}', 'multi-attribute validation after all rows', NULL, '6', NULL
 * '7', '2013-11-08 03:09:04', 'event', NULL, 'Event: @{rule_text1}.@{rule_text2}', 'extensible event', NULL, '7', NULL
 * '8', '2013-11-08 03:09:04', 'early event', NULL, 'Early event: @{rule_text1}.@{rule_text2}', 'extensible event before rule execution', NULL, '8', NULL
 * '9', '2013-11-08 03:09:04', 'commit event', NULL, 'Commit event: @{rule_text1}.@{rule_text2}', 'extensible event after all rows', NULL, '9', NULL
 * '11', '2013-10-12 00:03:59', 'minimum', NULL, 'Derive @{attribute_name} as min(@{rule_text1}.@{rule_text3}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})', 'replicate the minimum value from a child attribute', NULL, '10', NULL
 * '12', '2013-10-12 00:03:59', 'maximum', NULL, 'Derive @{attribute_name} as max(@{rule_text1}.@{rule_text3}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})', 'replicate the maximum value from a child attribute', NULL, '11', NULL
 * '13', '2013-10-12 00:03:59', 'managed parent', NULL, 'Create parent @{rule_text2} if it does not exist.', 'create a parent if it does not exist', NULL, '12', NULL
 */
public class EspressoRuleObjectImpl extends EspressoRuleObject {


    public EspressoRuleObjectImpl() {
        super();
        init();
    }

    public void init() {
        //set timestamp
    }

    /**
     * use this to pass a DataObject and Attribute (optional?)
     *
     * @param entityName
     * @param attributeName
     */
    public EspressoRuleObjectImpl(String entityName, String attributeName) {
        super();
        assert entityName != null;
        getRuleBean().setEntity_name(entityName);
        getRuleBean().setAttribute_name(attributeName);
    }

    /**
     * 'sum child attr into parent'
     * Derive @{attribute_name} as sum(@{rule_text1}.@{rule_text3}@{rule_text2 == null ? \'\' :
     * \' where \' + rule_text2})'
     *
     * @param relnName  this is the relationship name (LIst)
     * @param where     (Optional) Qualification
     * @param childAttr selected attribute
     */
    public void createSum(String relnName, String where, String childAttr) {
        ruleBean.setRuletype_ident(RULE_TYPES.SUM.getRuleIdent());
        ruleBean.setRule_text1(relnName+"List");
        String ruleText = where != null ? transformSteps(where) : null;//do we need to transform this where?
        ruleBean.setRule_text2(ruleText);
        ruleBean.setRule_text3(childAttr);
        // setProp4("javascript");
        if (ruleText != null) ruleText = " where " + ruleText;
        else ruleText = "";
        ruleBean.setName("Derive " + ruleBean.getAttribute_name() + " as sum(" + relnName + "List." + childAttr + ")" + ruleText);
        ruleBean.setComments(where);
    }

    /**
     * count child attr into parent'
     * 'Derive @{attribute_name} as count(@{rule_text1}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})
     *
     * @param where    - this is the (Optional) where clause
     * @param relnName this is the primary relationship name (List)
     */
    public void createCount(String relnName, String where) {
        ruleBean.setRuletype_ident(RULE_TYPES.COUNT.getRuleIdent());
        ruleBean.setRule_text1(relnName+"List");
        String ruleText = where != null ? transformSteps(where) : null;
        ruleBean.setRule_text2(ruleText);
        ruleBean.setProp4("javascript");

        ruleBean.setName("Derive {" + ruleBean.getAttribute_name() + "} as count(" + relnName + "List}  " + (ruleBean.getRule_text2() != null ? " where " + ruleBean.getRule_text2() : ""));
        ruleBean.setComments(where);
    }

    /**
     * formula',
     * 'Derive @{attribute_name} as @{rule_text1}', 'formula using table & parent attributes',
     */
    public void createFormula(String ruleText1) {
        ruleBean.setRuletype_ident(RULE_TYPES.FORMULA.getRuleIdent());
        String ruleText = ruleText1 != null ? transformSteps(ruleText1) : null;
        ruleBean.setRule_text1(ruleText);
        ruleBean.setProp4("javascript");
        ruleBean.setName("Derive {" + ruleBean.getAttribute_name() + "} as {" + ruleText + "}");
        ruleBean.setComments(ruleText1);
    }

    /**
     * validation', NULL, 'Validation @{rule_text1}', 'multi-attribute validation'
     *
     * @param ruleText1
     * @param errorMessage
     */
    public void createValidation(String ruleText1, String errorMessage) {
        ruleBean.setRuletype_ident(RULE_TYPES.VALIDATION.getRuleIdent());
        String ruleText = ruleText1 != null ? transformSteps(ruleText1) : null;
        ruleBean.setRule_text1(ruleText);
        ruleBean.setRule_text2(errorMessage);
        ruleBean.setProp4("javascript");
        ruleBean.setName("Validation {" + ruleBean.getAttribute_name() + "} as {" + ruleText + "}");
        ruleBean.setComments(ruleText1);
    }

    /**
     * Create parent @{rule_text2} if it does not exist.', 'create a parent if it does not exist',
     *
     * @param code
     * @param reln
     */
    public void createParentReplicate(String code, String reln) {
        ruleBean.setRuletype_ident(RULE_TYPES.PARENT_REPLICATE.getRuleIdent());
        ruleBean.setAttribute_name(null);
        ruleBean.setRule_text1(code ==  null?"":code);
        ruleBean.setRule_text2(reln);
        ruleBean.setProp4("javascript");
        ruleBean.setName("Create parent {" + reln + "} if it does not exist.");
        ruleBean.setComments("Insert parent if does not exists");
    }

    /**
     * 'Derive @{attribute_name} as parentcopy(@{rule_text1}.@{rule_text2})', 'non-cascaded parent value', NULL,
     * parent copy
     * @param relationship
     * @param sourceAttr
     */
    public void createParentCopy(String relationship, String sourceAttr) {
        ruleBean.setRuletype_ident(RULE_TYPES.PARENT_COPY.getRuleIdent());
        ruleBean.setRule_text1(relationship ==  null?"":relationship+"List");
        ruleBean.setRule_text2(sourceAttr);
        ruleBean.setProp4("javascript");
        ruleBean.setName("Derive {" + ruleBean.getAttribute_name() + "} as parentcopy(" + relationship + "." + sourceAttr + "}");
        ruleBean.setComments("'non-cascaded parent value");
    }


    /**
     * 'Event: @{rule_text1}.@{rule_text2}', 'extensible event',
     *
     * @param code
     * @param verbs (INSERT UPDATE DELETE)
     * @param async (optional) "async" any non null value
     */
    public void createEvent(String code, String verbs, String async) {
        ruleBean.setRuletype_ident(RULE_TYPES.EVENT.getRuleIdent());
        ruleBean.setAttribute_name(null);
        String ruleText = code != null ? transformSteps(code) : null;
        ruleBean.setRule_text1(ruleText);
        ruleBean.setRule_text2(verbs);
        ruleBean.setComments(code);
        ruleBean.setName("Event: {" + ruleText + "}.@{" + verbs + "}'");
        ruleBean.setProp2(async != null ? "async" : null);
        ruleBean.setProp4("javascript");
    }

    /**
     * 'minimum',
     * Derive @{attribute_name} as min(@{rule_text1}.@{rule_text3}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})',
     * 'replicate the minimum value from a child attribute',
     *
     * @param ruleText1
     * @param ruleText2
     * @param ruleText3
     */
    public void createMin(String ruleText1, String ruleText2, String ruleText3) {
        ruleBean.setRuletype_ident(RULE_TYPES.MIN.getRuleIdent());
        ruleBean.setRule_text1(ruleText1);
        ruleBean.setRule_text2(ruleText2);
        ruleBean.setRule_text3(ruleText3);
    }

    /**
     * 'maximum',
     * Derive @{attribute_name} as min(@{rule_text1}.@{rule_text3}@{rule_text2 == null ? \'\' : \' where \' + rule_text2})',
     * 'replicate the maximum value from a child attribute',
     *
     * @param ruleText1
     * @param ruleText2
     * @param ruleText3
     */
    public void createMax(String ruleText1, String ruleText2, String ruleText3) {
        ruleBean.setRuletype_ident(RULE_TYPES.MAX.getRuleIdent());
        ruleBean.setRule_text1(ruleText1);
        ruleBean.setRule_text2(ruleText2);
        ruleBean.setRule_text3(ruleText3);
    }
}

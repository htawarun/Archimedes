package com.bandsoftware.data;

import java.util.*;

/**
 * Created by Tyler on 4/3/14.
 * <p/>
 * Rule identity for Espresso Logic REST Services
 * <p/>
 * send to get prototype: http://bandrepo.my.espressologic.com/rest/abl/admin/v2/AllRules/1007
 * get: proptotype (201 created) - null values and inactive
 * verb: "INSERT"
 * <p/>
 * Request URL:http://bandrepo.my.espressologic.com/rest/abl/admin/v2/AllRules/1007
 * Request Method:PUT
 * checksum: "A:d3110027653b28b6"
 * href: "http://bandrepo.my.espressologic.com/rest/abl/admin/v2/AllRules/1007"
 * resource: "AllRules"
 * verb: "UPDATE"
 */

public class EspressoRuleObject extends BSDDataObject {
    //Espresso Logic Rule Prototype
    protected EspressoRuleBean ruleBean;


    public enum RULE_TYPES {
        SUM("1"),
        COUNT("2"),
        FORMULA("3"),
        PARENT_COPY("4"),
        VALIDATION("5"),
        COMMIT_VALIDATION("6"),
        EVENT("7"),
        EARLY_EVENT("8"),
        COMMIT_EVENT("9"),
        MIN("10"),
        MAX("11"),
        PARENT_REPLICATE("12");

        private String projectIdent; //rule type

        private RULE_TYPES(String t) {
            projectIdent = t;
        }

        public String getProjectIdent() {
            return projectIdent;
        }
    }

    public String METHOD_CALL_PROTOTYPE = " call$(\"$methodName\" "; // call$("getSomeMethod, arg1,arg2)")

    public EspressoRuleObject() {
        super();
        init();
    }

    public EspressoRuleBean getRuleBean() {
        return ruleBean;
    }

    public void setRuleBean(EspressoRuleBean ruleBean) {
        this.ruleBean = ruleBean;
    }

    private void init() {
        if (ruleBean == null) {
            setRuleBean(new EspressoRuleBean());
        }
    }

    /**
     * main entry point to change VB type rules into JavaScript syntax
     *
     * @param ruleText
     * @return
     */
    public String transformSteps(String ruleText) {
        String rule = transformStep1(ruleText);
        rule = transformStep2(this.getAttrList(), rule);
        rule = transformStep4(rule);
        rule = transformStep5(rule);
        rule = this.transformStep3(rule); //methods
        return transformSpecialEqualsCase(rule);//transformStep6AddSemicolon(rule);
    }


    /**
     * replace VB style rules syntax with JavaScrip syntax
     *
     * @param rule
     * @return
     */
    protected String transformStep1(String rule) {
        String newRule = rule != null ? rule.replaceAll("$value", "return") : "";
        newRule = newRule.replaceAll("\\(", " ( "); // put some spaces around known code style issues
        newRule = newRule.replaceAll("\\)", " ) ");
        newRule = newRule.replaceAll("!=", " NE ");
        newRule = newRule.replaceAll("=", " = ");
        newRule = newRule.replaceAll("NE", " != ");
        newRule = newRule.replaceAll(" IS NULL", " == null");
        newRule = newRule.replaceAll(" IS NOT NULL", " != null");
        newRule = newRule.replaceAll("\\$value  =", "return ");
        newRule = newRule.replaceAll("\\$value =", "return ");
        newRule = newRule.replaceAll(" and ", " && ");
        newRule = newRule.replaceAll(" or ", "|| ");
        newRule = newRule.replaceAll(" AND ", " && ");
        newRule = newRule.replaceAll(" OR ", "|| ");
        /* move this to string tokens*/
        newRule = newRule.replaceAll(" else ", " } else { ");
        newRule = newRule.replaceAll("Else", " } else { ");
        //newRule = newRule.replaceAll("else \\{ if", " } else if ");
        newRule = newRule.replaceAll("end if", " }");
        newRule = newRule.replaceAll("End If", " }");
        newRule = newRule.replaceAll("endIf", " }");
        newRule = newRule.replaceAll("then", " { ");
        newRule = newRule.replaceAll("Then", " { ");
        newRule = newRule.replaceAll(":old.", "oldRow.");
        newRule = newRule.replaceAll(":Old.", "oldRow.");

        newRule = newRule.replaceAll("True", "1");
        newRule = newRule.replaceAll("False", "0");
        /**/
        return newRule.trim();
    }

    /**
     * replace attribute name with row.[attrname]
     *
     * @param attrList
     * @param rule
     * @return
     */
    protected String transformStep2(Enumeration<String> attrList, String rule) {
        StringTokenizer st = new StringTokenizer(rule, " ");
        String token;
        String newRule = rule;
        StringBuffer sb = new StringBuffer();
        List<String> attrNameList = new Vector<String>();
        while (attrList.hasMoreElements()) {
            String attrName = (String) attrList.nextElement();
            attrNameList.add(attrName);
        }
        while (st.hasMoreElements()) {

            token = (String) st.nextToken();
            if (attrNameList.contains(token)){
                for (String attrName : attrNameList) {
                    if (token.equalsIgnoreCase(attrName)) {
                        sb.append("row." + token);
                        sb.append(" ");
                    } else {
                        if (token.equalsIgnoreCase(":Old." + attrName)) {
                            sb.append("oldRow." + token);
                            sb.append(" ");
                        }
                    }
                }
            } else {
                sb.append(token);
                sb.append(" ");
            }


        }
        return sb.toString();
    }

    /**
     * any remaining terms that are not attributes will be determined as methodNames
     *
     * @param rule contains method e.g. getMethod() or getMethod(row.AttributeName)
     * @return
     */
    protected String transformStep3(String rule) {

        String newRule = rule != null ? rule : "";
        String args = "";
        Map<String, String> methodArgMap = findMethods(newRule);
        for (String methodName : methodArgMap.keySet()) {
            args = methodArgMap.get(methodName);
            String methodPrototype = this.METHOD_CALL_PROTOTYPE.replaceAll("\\$methodName", methodName);
            if (args != null && !"".equals(args)) {
                args = "," + args + ")";
            } else {
                args = args + args + ")";
            }
            newRule = newRule.replace(methodName, methodPrototype + args);
        }
        return newRule;
    }

    private Map<String, String> findMethods(String rule) {
        Map<String, String> map = new HashMap<String, String>();
        StringBuffer addSemicolonToRule = new StringBuffer();
        StringTokenizer st = new StringTokenizer(rule, " ");
        String token;
        String newRule = "";
        while (st.hasMoreElements()) {
            token = st.nextToken();

            //to do ARGS??? this is a no param method
            if (token.endsWith("()")) {
                map.put(token, "");
            }
            newRule += token;
        }
        return map;
    }

    /**
     * replace verbs with logicContext
     *
     * @param rule
     * @return
     */
    protected String transformStep4(String rule) {
        String newRule = (rule != null) ? rule.replaceAll("Inserting", "logicContext.verb == INSERT") : "";
        newRule = newRule.replaceAll("Updating", "logicContext.verb == UPDATE");
        newRule = newRule.replaceAll("Deleting", "logicContext.verb == DELETE");
        return newRule;
    }

    /**
     * append return to front of statement (only for simple javascript)
     *
     * @param rule
     * @return
     */
    protected String transformStep5(String rule) {
        String newRule = (rule != null) ? rule : "";
        if (!newRule.startsWith("if ") && !newRule.startsWith("If") && !newRule.startsWith("// ")) {
            if (!newRule.startsWith("return ") && (!newRule.startsWith("if") || !newRule.startsWith("If"))) {
                newRule = "return " + newRule;
            }
        }
        //to do replace return with value = and then define [var value;] and at end append [return value;]
        return newRule;
    }

    protected String transformSpecialEqualsCase(String rule) {
        String newRule = (rule != null) ? rule.trim() : "";
        StringBuffer addSemicolonToRule = new StringBuffer();
        StringTokenizer st = new StringTokenizer(newRule, " ");
        String token;
        boolean startSpecialEqualsCase = false; //inside IF statement
        boolean commentSection = false;
        while (st.hasMoreElements()) {
            token = st.nextToken();
            if (token.startsWith("/*") || token.startsWith("//")) {
                commentSection = true;
            }
            if (token.equalsIgnoreCase("if") && !commentSection) {
                startSpecialEqualsCase = true;
            }

            if (startSpecialEqualsCase && !commentSection) {
                //we hit the end of an If statement (and not in a comment) so append ;
                if (startSpecialEqualsCase && token.equals("=") && !commentSection) {
                    addSemicolonToRule.append("==="); //replace
                    addSemicolonToRule.append(" ");

                } else {
                    if((token.equals("{") || token.equals("}")) && !commentSection){
                        startSpecialEqualsCase = false;
                        addSemicolonToRule.append(token);
                        addSemicolonToRule.append("\n");
                    } else {
                        addSemicolonToRule.append(token);
                        addSemicolonToRule.append(" ");
                    }
                    if (!commentSection) {
                        if (token.endsWith("*/") || token.startsWith("\r") || token.equals("\n")) {
                            commentSection = true;
                        }
                    }
                }
            } else {
                addSemicolonToRule.append(token);
                addSemicolonToRule.append(" ");
                if (isEndOfIfStatemet(token)) {
                    startSpecialEqualsCase = false;
                }
            }
        }

        return addSemicolonToRule.toString();
    }
    //NOTE 1 - need to deal with if (A=B) THEN change to ==
    //Note 2 - if not an attribute name but a value if( a = BANANA) THEN - need to put a quote around it 'BANANA'
    //note 3 - if a number or value ends in 0) we need to split off the paren and add it back
    //note 4 - if we hit // or /* - need to ignore the entire sentence until newline or */

    /**
     * append semicolon to end of statement lines
     *
     * @param rule
     * @return
     */
    protected String transformStep6AddSemicolon(String rule) {
        String newRule = (rule != null) ? rule.trim() : "";
        StringBuffer addSemicolonToRule = new StringBuffer();
        StringTokenizer st = new StringTokenizer(newRule, " ");
        String token;
        boolean startSpecialEqualsCase = false; //inside IF statement
        boolean commentSection = false;
        while (st.hasMoreElements()) {
            token = st.nextToken();
            if (token.startsWith("/*") || token.startsWith("//")) {
                commentSection = true;
            }
            if (token.equalsIgnoreCase("if") && !commentSection) {
                startSpecialEqualsCase = true;
            }

            if (isEndOfIfStatemet(token) && startSpecialEqualsCase && !commentSection) {
                //we hit the end of an If statement (and not in a comment) so append ;
                addSemicolonToRule.append(";");
                addSemicolonToRule.append(shouldQuote(token));
                addSemicolonToRule.append(" ");
                startSpecialEqualsCase = false;

            } else {
                if (startSpecialEqualsCase && token.equals("=") && !commentSection) {
                    addSemicolonToRule.append("==="); //replace
                    addSemicolonToRule.append(" ");
                } else {
                    if (!commentSection) {
                        addSemicolonToRule.append(shouldQuote(token));
                        addSemicolonToRule.append(" ");
                    } else {
                        addSemicolonToRule.append(token);
                        addSemicolonToRule.append(" ");
                        if (token.endsWith("*/") || token.endsWith("\r") || token.endsWith("\n")) {
                            commentSection = true;
                        }
                    }
                }
            }

        }
        if (!addSemicolonToRule.toString().contains(";") && addSemicolonToRule.toString().contains("return ")) {
            addSemicolonToRule.append("; ");
        }
        return addSemicolonToRule.toString();
    }

    private String shouldQuote(String tokenStr) {

        return (reservedToken(tokenStr) || isNumber(tokenStr)) ? tokenStr : "\"" + tokenStr + "\"";
    }

    private boolean isNumber(String num) {
        try {
            new Integer(num).intValue();

        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    protected String tokenList = "abstract,assert,boolean,break,byte,case,catch,char,class,const*,continue,default" +
            "double,do,else,enum,extends,false,final,finally,float,for,goto*,if,If,then,end,elseIf," +
            "implements,import,instanceof,int,interface,long,native,new,null,package,private,protected," +
            "public,return,short,static,strictfp,super,switch,synchronized,this,throw,throws,transient," +
            "true,try,void,volatile,while," +
            "(,),;,=,==,===,!=,+,+=,-,*.\\,//,>,>=,<,<=,<>,&&,||,IS,NOT,NULL," +
            "call$,row.,oldRow.,logicContext,Insert,Update,Delete";

    private boolean reservedToken(String tokenStr) {
        StringTokenizer token = new StringTokenizer(tokenList, ",");
        while (token.hasMoreElements()) {
            String aKeyword = (String) token.nextToken();
            if (aKeyword.equalsIgnoreCase(tokenStr) || tokenStr.startsWith(aKeyword) || tokenStr.endsWith(aKeyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * special end of case words
     *
     * @param token
     * @return
     */
    private boolean isEndOfIfStatemet(String token) {
        boolean ans = false;
        //if(boolen) then return expression; end/else return exression; end
        String[] keywords = new String[6];
        keywords[0] = "end";
        keywords[1] = "endif";
        keywords[2] = "else";
        keywords[3] = "elseif";
        keywords[4] = ")";
        keywords[5] = "*/";

        for (int i = 0; i < keywords.length; i++) {
            if (keywords[i].equalsIgnoreCase(token)) {
                ans = true;
                break;
            }
        }
        return ans;
    }
    public void setAttrList(Enumeration<String> attrList) {
        if (attrList != null) {
            while ( attrList.hasMoreElements()) {
                String name = (String) attrList.nextElement();
                setAttr(name, name);
            }
        }
    }
    public void setAttrList(List<String> attrList) {
        if (attrList != null) {
            for (String name : attrList) {
                setAttr(name, name);
            }
        }
    }

}

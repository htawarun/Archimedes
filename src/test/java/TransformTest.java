/**
 * Created by Tyler on 4/4/14.
 */

import com.bandsoftware.data.*;
import org.junit.*;

import java.util.List;
import java.util.Vector;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TransformTest {

    EspressoRuleObjectImpl ruleObjectImpl;
    EspressoRuleBean ruleObject;



    public void testTransformSum(){

    }

    @Before
    public void setUp() {
       // super.setUp();

        ruleObjectImpl = new EspressoRuleObjectImpl("DataObject","AttributeName");
        ruleObject = ruleObjectImpl.getRuleBean();
    }

    @Test
    public void testSum(){

        ruleObjectImpl.createSum("relnName","where","childAttr");
        assertTrue(ruleObject.getProjectIdent().equals(EspressoRuleObject.RULE_TYPES.SUM.getProjectIdent()));

    }

    @Test
    public void testSumWhereNull(){
        ruleObjectImpl.createSum("relnName",null,"childAttr");
        assertTrue(ruleObject.getProjectIdent().equals(EspressoRuleObject.RULE_TYPES.SUM.getProjectIdent()));
        assertNull(ruleObject.getRuleText2());
    }

    @Test
    public void testSumWhereContainsValue(){
        String where = "$value = a + b";
        ruleObjectImpl.createSum("relnName", where, "childAttr");
        assertTrue(ruleObject.getRuleText2().startsWith("return "));
        String autoName = ruleObject.getAutoName();
        //TO DO - fix the quotes around variables (non-numeric)
        //compare("Derive AttributeName as sum(relnName.childAttr) where return \"a\" + \"b\" ; ", autoName);
        compare("Derive AttributeName as sum(relnName.childAttr) where return a + b ",autoName);
    }
    @Test
    public void testSumWhereContainsAttrList(){
        String where = "$value = apple + banana";

        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("apple","apple");
        ruleObjectImpl.setAttr("banana","banana");
        ruleObjectImpl.createSum("relnName",where,"childAttr");
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("return "));
        compare("Derive AttributeName as sum(relnName.childAttr) where return row.apple + row.banana ", autoName);
    }

    @Test
    public void testSumWhereContainsAttrListNoReturn(){
        String where = "apple + banana";

        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("apple","apple");
        ruleObjectImpl.setAttr("banana","banana");
        ruleObjectImpl.createSum("relnName",where,"childAttr");
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("return "));
        compare("Derive AttributeName as sum(relnName.childAttr) where return row.apple + row.banana ", autoName);
    }
    @Test
    public void testSumIfStatements(){

        String where = "if (apple + banana) > 100 Then $value = 1 else $value = 2 endIf";
        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("apple","apple");
        ruleObjectImpl.setAttr("banana","banana");
        ruleObjectImpl.createSum("relnName",where,"childAttr");
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("if "));
        String expectedResult = "Derive AttributeName as sum(relnName.childAttr) where if ( row.apple + row.banana ) > 100 {\n" +
                "return 1 } else { return 2 } ";
        compare(expectedResult, autoName);
    }
    //
    @Test
    public void testComplexIfStatements(){

        String where = "if ( ( (Updating and  CreditLimit  =  :Old.CreditLimit )  OR  CreditLimit IS NULL )    "+
                "AND (Inserting  OR   ( ( :Old.MaxCreditLimit  !=  MaxCreditLimit )  "+
                "AND  ( :Old.MaxCreditLimit  =   CreditLimit ) ) ) ) then   $value =  MaxCreditLimit End If";
        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("MaxCreditLimit", "MaxCreditLimit");
        ruleObjectImpl.setAttr("CreditLimit", "CreditLimit");
        ruleObjectImpl.createFormula(where);
        String autoName = ruleObject.getAutoName();
       // assertTrue(autoName.startsWith("if "));
        String expected = "Derive {AttributeName} as {if ( ( ( logicContext.verb == UPDATE && row.CreditLimit === oldRow.CreditLimit ) || row.CreditLimit == null ) && ( logicContext.verb == INSERT || ( ( oldRow.MaxCreditLimit != row.MaxCreditLimit ) && ( oldRow.MaxCreditLimit === row.CreditLimit ) ) ) ) {\n" +
                "return row.MaxCreditLimit } }";
        compare(expected,autoName);
    }

    //Derive AttributeName as sum(relnName.childAttr) where if ( (logicContext.verb == "'UPDATE'"
    // //&& "CreditLimit" === ":Old.CreditLimit" ; ) || "CreditLimit" IS NULL ; )
    // //&& (logicContext.verb == "'INSERT'" || ( ( ":Old.MaxCreditLimit" != "MaxCreditLimit" ; ) &
    // //& ( ":Old.MaxCreditLimit" = "CreditLimit" ; ) ; ) ; ) then return "MaxCreditLimitEnd" I  
 /* Corrected
    if ( (logicContext.verb == UPDATE &&  row.CreditLimit  ==   oldRow.CreditLimit )
||  row.CreditLimit === null && (logicContext.verb == INSERT
||  ( (  oldRow.MaxCreditLimit  !=  row.MaxCreditLimit )
&&  (  oldRow.MaxCreditLimit  ===   row.CreditLimit ) ) ) ) THEN
return row.MaxCreditLimit;
EndIf
     */
    @Test
    public void testSumIfStatementsEqualsSign(){

        String where = "If ( Total_Method_Cnt = 0) Then   $value = 0 end if";
        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("Total_Method_Cnt","Total_Method_Cnt");

        ruleObjectImpl.createSum("relnName",where,"childAttr");
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("If "));
        compare("Derive AttributeName as sum(relnName.childAttr) where If ( row.Total_Method_Cnt === 0 ) {\n" +
                "return 0 } ",autoName);
    }

    @Test
    public void testSumIfStatementsWithMethodCall(){

        String where = "If ( Total_Method_Cnt = 0) Then   $value = getValue1() else $value = getValue2() end if";
        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("Total_Method_Cnt","Total_Method_Cnt");

        ruleObjectImpl.createSum("relnName",where,"childAttr");
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("If "));
        String expected = "Derive AttributeName as sum(relnName.childAttr) where If ( row.Total_Method_Cnt === 0 ) {\n"+
                "return getValue1 ( ) } else { return getValue2 ( ) } ";
        compare(expected,autoName);
    }

    @Test
    public void testSumIfStatementsWithOld(){

        String where = "If ( Total_Method_Cnt != :Old.Total_Method_Cnt) Then   $value = 1 else $value = 2 end if";
        Vector<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("Total_Method_Cnt","Total_Method_Cnt");

        ruleObjectImpl.createSum("relnName",where,"childAttr");
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("If "));
        compare("Derive AttributeName as sum(relnName.childAttr) "+
                "where If ( row.Total_Method_Cnt != oldRow.Total_Method_Cnt ) {\n" +
                "return 1 } else { return 2 } ",autoName);
    }

    @Test
    public void testReturnBoolean(){
        String where = "return isJava = True /* Yes */ ";
        List<String> attrList = new Vector<String>();
        ruleObjectImpl.setAttr("isJava","isJava");

        ruleObjectImpl.createCount("relnName", where);
        String autoName = ruleObject.getAutoName();
        assertTrue(ruleObject.getRuleText2().startsWith("return "));
        compare("Derive {AttributeName} as count(relnName} where return row.isJava = 1 /* Yes */ ",autoName);
    }
    private void compare(String expectedResult, String actual){
        System.out.println(actual);
        System.out.println(expectedResult);
        System.out.println("===================");
        assertTrue(expectedResult.equals(actual));
    }
}

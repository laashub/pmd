/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import static net.sourceforge.pmd.lang.java.ParserTstUtil.getOrderedNodes;
import static net.sourceforge.pmd.lang.java.ParserTstUtil.parseJava15;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;

@Ignore
public class StatementAndBraceFinderTest {

    @Test
    public void testStatementExpressionParentChildLinks() {
        ASTStatementExpression se = getOrderedNodes(ASTStatementExpression.class, TEST1).get(0);
        ASTMethodDeclaration seParent = (ASTMethodDeclaration) se.getDataFlowNode().getParents().get(0).getNode();
        assertEquals(se, seParent.getDataFlowNode().getChildren().get(0).getNode());
        assertEquals(seParent, se.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testVariableDeclaratorParentChildLinks() {
        ASTVariableDeclarator vd = getOrderedNodes(ASTVariableDeclarator.class, TEST2).get(0);
        ASTMethodDeclaration vdParent = (ASTMethodDeclaration) vd.getDataFlowNode().getParents().get(0).getNode();
        assertEquals(vd, vdParent.getDataFlowNode().getChildren().get(0).getNode());
        assertEquals(vdParent, vd.getDataFlowNode().getParents().get(0).getNode());
    }

    @Test
    public void testIfStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, TEST3).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.IF_EXPR));
        assertTrue(dfn.isType(NodeType.IF_LAST_STATEMENT_WITHOUT_ELSE));
    }

    @Test
    public void testWhileStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, TEST4).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.WHILE_EXPR));
        assertTrue(dfn.isType(NodeType.WHILE_LAST_STATEMENT));
    }

    @Test
    public void testForStmtHasCorrectTypes() {
        ASTExpression exp = getOrderedNodes(ASTExpression.class, TEST5).get(0);
        DataFlowNode dfn = exp.getDataFlowNode().getFlow().get(2);
        assertTrue(dfn.isType(NodeType.FOR_INIT));
        dfn = exp.getDataFlowNode().getFlow().get(3);
        assertTrue(dfn.isType(NodeType.FOR_EXPR));
        dfn = exp.getDataFlowNode().getFlow().get(4);
        assertTrue(dfn.isType(NodeType.FOR_UPDATE));
        assertTrue(dfn.isType(NodeType.FOR_BEFORE_FIRST_STATEMENT));
        assertTrue(dfn.isType(NodeType.FOR_END));
    }

    @Test(expected = RuntimeException.class)
    public void testOnlyWorksForMethodsAndConstructors() {
        StatementAndBraceFinder sbf = new StatementAndBraceFinder(LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler().getDataFlowHandler());

        ASTCompilationUnit astCompilationUnit = parseJava15(TEST1);

        sbf.buildDataFlowFor(astCompilationUnit.getFirstChildOfType(ASTMethodDeclaration.class));
        // FIXME look at history of this test
        // sbf.buildDataFlowFor(new ASTConstructorDeclaration(1));
        sbf.buildDataFlowFor(astCompilationUnit);
    }

    private static final String TEST1 = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  x = 2;" + PMD.EOL
            + " }" + PMD.EOL + "}";

    private static final String TEST2 = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  int x;" + PMD.EOL
            + " }" + PMD.EOL + "}";

    private static final String TEST3 = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  if (x) {}" + PMD.EOL
            + " }" + PMD.EOL + "}";

    private static final String TEST4 = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  while (x) {}" + PMD.EOL
            + " }" + PMD.EOL + "}";

    private static final String TEST5 = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL
            + "  for (int i=0; i<10; i++) {}" + PMD.EOL + " }" + PMD.EOL + "}";
}

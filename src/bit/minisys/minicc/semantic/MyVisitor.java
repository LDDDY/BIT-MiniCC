package bit.minisys.minicc.semantic;

import bit.minisys.minicc.semantic.ast.*;

import java.util.ArrayList;
import java.util.List;

public class MyVisitor implements ASTVisitor {

    private MySymbolTable globalVarTable;
    private MySymbolTable globalFuncTable;
    private MySymbolTable localVarTable;
    private int loopDepth = 0;

    private static final String ES01_VARIABLE = "Identifier";
    private static final String ES01_FUNC_CALL = "FunctionCall";
    private static final String ES02_FUNC_DEF = "FunctionDefine";
    private static final String ES02_VAR_DECLARATION = "Declaration";
    private List<String> errorInfoList = new ArrayList<>();

    public List<String> getErrorInfoList() {
        return errorInfoList;
    }

    private void notDefinedErr(String item, String errCase) {
        String errInfo = errCase.equals(ES01_VARIABLE)
                ? "[ES01] " + ES01_VARIABLE + ": \"" + item + "\" is not defined."
                : "[ES01] " + ES01_FUNC_CALL + ": \"" + item + "\" is not declared.";
        errorInfoList.add(errInfo);
    }

    private void multiDefinedErr(String item, String errCase) {
        String errInfo = errCase.equals(ES02_VAR_DECLARATION)
                ? "[ES02] " + ES02_VAR_DECLARATION + ": \"" + item + "\" has been declared."
                : "[ES02] " + ES02_FUNC_DEF + ": \"" + item + "\" has been defined.";
        errorInfoList.add(errInfo);
    }

    private void breakOutsideLoopErr() {
        String errInfo = "[ES03] BreakStatement: must be in a LoopStatement.";
        errorInfoList.add(errInfo);
    }


    public MyVisitor() {
        globalVarTable = new MySymbolTable();
        localVarTable = globalVarTable;
        globalFuncTable = new MySymbolTable();
    }

    @Override
    public void visit(ASTCompilationUnit program) throws Exception {
        program.scope = globalVarTable;
        for (ASTNode astNode : program.items) {
            if (astNode instanceof ASTDeclaration) {
                this.visit((ASTDeclaration) astNode);
            } else if (astNode instanceof ASTFunctionDefine) {
                this.visit((ASTFunctionDefine) astNode);
            }
        }
    }

    @Override
    public void visit(ASTDeclaration declaration) throws Exception {
        declaration.scope = localVarTable;

        for (ASTInitList initDeclarator : declaration.initLists) {
            this.visit(initDeclarator);

            String name = initDeclarator.declarator.getName();

            if (localVarTable.itemExistInThisTable(name)) {
                multiDefinedErr(name, ES02_VAR_DECLARATION);
                return;
            }

            if (declaration.scope == globalVarTable) {
                globalVarTable.addItem(name);
            } else {
                localVarTable.addItem(name);
            }

        }
    }

    @Override
    public void visit(ASTArrayDeclarator arrayDeclarator) throws Exception {

    }

    @Override
    public void visit(ASTVariableDeclarator variableDeclarator) throws Exception {
        variableDeclarator.scope = localVarTable;
    }

    @Override
    public void visit(ASTFunctionDeclarator functionDeclarator) throws Exception {
        this.visit(functionDeclarator.declarator);
        for (ASTParamsDeclarator astParamsDeclarator : functionDeclarator.params) {
            this.visit(astParamsDeclarator);
        }
    }

    @Override
    public void visit(ASTParamsDeclarator paramsDeclarator) throws Exception {
    }

    @Override
    public void visit(ASTArrayAccess arrayAccess) throws Exception {

    }

    @Override
    public void visit(ASTBinaryExpression binaryExpression) throws Exception {
        this.visit(binaryExpression.expr1);
        this.visit(binaryExpression.expr2);
    }

    @Override
    public void visit(ASTBreakStatement breakStat) throws Exception {
        if (loopDepth <= 0) {
            breakOutsideLoopErr();
        }
    }

    @Override
    public void visit(ASTContinueStatement continueStatement) throws Exception {

    }

    @Override
    public void visit(ASTCastExpression castExpression) throws Exception {

    }

    @Override
    public void visit(ASTCharConstant charConst) throws Exception {

    }

    @Override
    public void visit(ASTCompoundStatement compoundStat) throws Exception {
        compoundStat.scope = localVarTable;
        localVarTable = new MySymbolTable();
        localVarTable.setFather(compoundStat.scope);

        for (ASTNode astNode : compoundStat.blockItems) {
            if (astNode instanceof ASTDeclaration) {
                visit((ASTDeclaration) astNode);
            } else if (astNode instanceof ASTStatement) {
                visit((ASTStatement) astNode);
            }
        }

        localVarTable = compoundStat.scope;
    }

    @Override
    public void visit(ASTConditionExpression conditionExpression) throws Exception {

    }

    @Override
    public void visit(ASTExpression expression) throws Exception {
        if (expression instanceof ASTConditionExpression) {
            this.visit((ASTConditionExpression) expression);
        } else if (expression instanceof ASTBinaryExpression) {
            this.visit((ASTBinaryExpression) expression);
        } else if (expression instanceof ASTCastExpression) {
            this.visit((ASTCastExpression) expression);
        } else if (expression instanceof ASTUnaryExpression) {
            this.visit((ASTUnaryExpression) expression);
        } else if (expression instanceof ASTPostfixExpression) {
            this.visit((ASTPostfixExpression) expression);
        } else if (expression instanceof ASTArrayAccess) {
            this.visit((ASTArrayAccess) expression);
        } else if (expression instanceof ASTMemberAccess) {
            this.visit((ASTMemberAccess) expression);
        } else if (expression instanceof ASTFunctionCall) {
            this.visit((ASTFunctionCall) expression);
        } else if (expression instanceof ASTIdentifier) {
            this.visit((ASTIdentifier) expression);
        } else if (expression instanceof ASTIntegerConstant) {
            this.visit((ASTIntegerConstant) expression);
        } else if (expression instanceof ASTFloatConstant) {
            this.visit((ASTFloatConstant) expression);
        } else if (expression instanceof ASTCharConstant) {
            this.visit((ASTCharConstant) expression);
        } else if (expression instanceof ASTStringConstant) {
            this.visit((ASTStringConstant) expression);
        }
    }

    @Override
    public void visit(ASTExpressionStatement expressionStat) throws Exception {

    }

    @Override
    public void visit(ASTFloatConstant floatConst) throws Exception {

    }

    @Override
    public void visit(ASTFunctionCall funcCall) throws Exception {
        String functionName = ((ASTIdentifier) funcCall.funcname).value;

        if (!globalFuncTable.itemExistInThisAndFatherTables(functionName)) {
            notDefinedErr(functionName, ES01_FUNC_CALL);
        }
    }

    @Override
    public void visit(ASTGotoStatement gotoStat) throws Exception {

    }

    @Override
    public void visit(ASTIdentifier identifier) throws Exception {
        String name = identifier.value;
        if (!localVarTable.itemExistInThisAndFatherTables(name)) {
            notDefinedErr(name, ES01_VARIABLE);
        }

    }

    @Override
    public void visit(ASTInitList initList) throws Exception {
        this.visit(initList.declarator);
        for (ASTExpression expr : initList.exprs) {
            this.visit(expr);
        }
    }

    @Override
    public void visit(ASTIntegerConstant intConst) throws Exception {

    }

    @Override
    public void visit(ASTIterationDeclaredStatement iterationDeclaredStat) throws Exception {
        iterationDeclaredStat.scope = localVarTable;
        localVarTable = new MySymbolTable();
        localVarTable.setFather(iterationDeclaredStat.scope);
        this.loopDepth += 1;

        if (iterationDeclaredStat.init != null) {
            this.visit(iterationDeclaredStat.init);
        }

        if (iterationDeclaredStat.cond != null) {
            for (ASTExpression cond : iterationDeclaredStat.cond) {
                this.visit(cond);
            }
        }

        if (iterationDeclaredStat.step != null) {
            for (ASTExpression step : iterationDeclaredStat.step) {
                this.visit(step);
            }
        }

        this.visit(iterationDeclaredStat.stat);

        loopDepth -= 1;
        localVarTable = iterationDeclaredStat.scope;
    }

    @Override
    public void visit(ASTIterationStatement iterationStat) throws Exception {
        iterationStat.scope = localVarTable;
        localVarTable = new MySymbolTable();
        localVarTable.setFather(iterationStat.scope);
        loopDepth += 1;

        if (iterationStat.init != null) {
            for (ASTExpression init : iterationStat.init) {
                this.visit(init);
            }
        }

        if (iterationStat.cond != null) {
            for (ASTExpression cond : iterationStat.cond) {
                this.visit(cond);
            }
        }

        if (iterationStat.step != null) {
            for (ASTExpression step : iterationStat.step) {
                this.visit(step);
            }
        }

        this.visit(iterationStat.stat);

        loopDepth -= 1;
        localVarTable = iterationStat.scope;
    }

    @Override
    public void visit(ASTLabeledStatement labeledStat) throws Exception {
        this.visit(labeledStat.stat);
    }

    @Override
    public void visit(ASTMemberAccess memberAccess) throws Exception {

    }

    @Override
    public void visit(ASTPostfixExpression postfixExpression) throws Exception {
        this.visit(postfixExpression.expr);
    }

    @Override
    public void visit(ASTReturnStatement returnStat) throws Exception {
        returnStat.scope = localVarTable;
        for (ASTExpression expr : returnStat.expr) {
            this.visit(expr);
        }
    }

    @Override
    public void visit(ASTSelectionStatement selectionStat) throws Exception {

    }

    @Override
    public void visit(ASTStringConstant stringConst) throws Exception {

    }

    @Override
    public void visit(ASTTypename typename) throws Exception {

    }

    @Override
    public void visit(ASTUnaryExpression unaryExpression) throws Exception {
        this.visit(unaryExpression.expr);
    }

    @Override
    public void visit(ASTUnaryTypename unaryTypename) throws Exception {

    }

    @Override
    public void visit(ASTFunctionDefine functionDefine) throws Exception {

        String funcname = functionDefine.declarator.getName();
        if (globalFuncTable.itemExistInThisAndFatherTables(funcname)) {
            multiDefinedErr(funcname, ES02_FUNC_DEF);
            return;
        }

        functionDefine.scope = localVarTable;
        localVarTable = new MySymbolTable();

        ASTFunctionDeclarator astFunctionDeclarator = (ASTFunctionDeclarator) functionDefine.declarator;
        for (ASTParamsDeclarator param : astFunctionDeclarator.params) {
            String paramName = param.declarator.getName();
            localVarTable.addItem(paramName);
        }

        globalFuncTable.addItem(funcname);
        this.visit(functionDefine.declarator);
        this.visit(functionDefine.body);
        localVarTable = globalVarTable;
    }

    @Override
    public void visit(ASTDeclarator declarator) throws Exception {
        if (declarator instanceof ASTVariableDeclarator) {
            visit((ASTVariableDeclarator) declarator);
        } else if (declarator instanceof ASTArrayDeclarator) {
            visit((ASTArrayDeclarator) declarator);
        } else if (declarator instanceof ASTFunctionDeclarator) {
            visit((ASTFunctionDeclarator) declarator);
        }
    }

    @Override
    public void visit(ASTStatement statement) throws Exception {
        if (statement instanceof ASTBreakStatement) {
            this.visit((ASTBreakStatement) statement);
        } else if (statement instanceof ASTContinueStatement) {
            this.visit((ASTContinueStatement) statement);
        } else if (statement instanceof ASTGotoStatement) {
            this.visit((ASTGotoStatement) statement);
        } else if (statement instanceof ASTReturnStatement) {
            this.visit((ASTReturnStatement) statement);
        } else if (statement instanceof ASTCompoundStatement) {
            this.visit((ASTCompoundStatement) statement);
        } else if (statement instanceof ASTSelectionStatement) {
            this.visit((ASTSelectionStatement) statement);
        } else if (statement instanceof ASTIterationStatement) {
            this.visit((ASTIterationStatement) statement);
        } else if (statement instanceof ASTIterationDeclaredStatement) {
            this.visit((ASTIterationDeclaredStatement) statement);
        } else if (statement instanceof ASTExpressionStatement) {
            this.visit((ASTExpressionStatement) statement);
        } else if (statement instanceof ASTLabeledStatement) {
            this.visit((ASTLabeledStatement) statement);
        }
    }

    @Override
    public void visit(ASTToken token) throws Exception {

    }
}

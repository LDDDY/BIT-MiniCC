package bit.minisys.minicc.icgen;

import java.util.*;

import bit.minisys.minicc.parser.ast.*;

public class MyICBuilder implements ASTVisitor{

    private Map<ASTNode, ASTNode> nodeValueMap = new HashMap<>();
    private List<Quat> quats = new ArrayList<>();
    private Integer tmpId = 0;

    public List<Quat> getQuats() {
        return quats;
    }

    @Override
    public void visit(ASTCompilationUnit program) throws Exception {
        for (ASTNode node : program.items) {
            if(node instanceof ASTFunctionDefine)
                visit((ASTFunctionDefine)node);
        }
    }

    @Override
    public void visit(ASTDeclaration declaration) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTArrayDeclarator arrayDeclarator) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTVariableDeclarator variableDeclarator) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTFunctionDeclarator functionDeclarator) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTParamsDeclarator paramsDeclarator) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTArrayAccess arrayAccess) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTBinaryExpression binaryExpression) throws Exception {

        ASTNode res = null;
        ASTNode opnd1 = null;
        ASTNode opnd2 = null;
        String op = binaryExpression.op.value;

        if (op.equals("=")) {
            visit(binaryExpression.expr1);
            // 得到子结点 expr1 的返回值
            res = nodeValueMap.get(binaryExpression.expr1);
            if (binaryExpression.expr2 instanceof ASTIdentifier
                    || binaryExpression.expr2 instanceof ASTIntegerConstant) {
                opnd1 = binaryExpression.expr2;
            } else if(binaryExpression.expr2 instanceof ASTBinaryExpression) {
                ASTBinaryExpression value = (ASTBinaryExpression)binaryExpression.expr2;
                op = value.op.value;
                visit(value.expr1);
                opnd1 = nodeValueMap.get(value.expr1);
                visit(value.expr2);
                opnd2 = nodeValueMap.get(value.expr2);
            } else if(binaryExpression.expr2 instanceof ASTUnaryExpression
                    || binaryExpression.expr2 instanceof ASTPostfixExpression){
                this.visit(binaryExpression.expr2);
                opnd1 = nodeValueMap.get(binaryExpression.expr2);
            }

        }else if (op.equals("+") || op.equals("-") || op.equals("*")
                || op.equals("/") || op.equals("%") || op.equals(">")
                || op.equals("<") || op.equals(">=") || op.equals("<=")
                || op.equals("==") || op.equals("!=") || op.equals("||")
                || op.equals("&&")) {
            // 结果存储到中间变量
            tmpId++;
            res = new TemporaryValue(tmpId);
            visit(binaryExpression.expr1);
            opnd1 = nodeValueMap.get(binaryExpression.expr1);
            visit(binaryExpression.expr2);
            opnd2 = nodeValueMap.get(binaryExpression.expr2);
        }else if(op.equals("+=") || op.equals("-=") || op.equals("*=")
                || op.equals("/=") || op.equals("%=")){
            this.visit(binaryExpression.expr1);
            opnd1 = nodeValueMap.get(binaryExpression.expr1);
            this.visit(binaryExpression.expr2);
            opnd2 = nodeValueMap.get(binaryExpression.expr2);
            res = opnd1;
        }

        // build quat
        Quat quat = new Quat(op, res, opnd1, opnd2);
        quats.add(quat);
        nodeValueMap.put(binaryExpression, res);
    }

    @Override
    public void visit(ASTUnaryExpression unaryExpression) throws Exception {
        String op = unaryExpression.op.value;
        if (op.equals("++") || op.equals("--")) {
            this.visit(unaryExpression.expr);
            ASTNode res = nodeValueMap.get(unaryExpression.expr);
            Quat quat = new Quat(op, res, res, null);
            quats.add(quat);
            nodeValueMap.put(unaryExpression, res);
        } else {
            this.visit(unaryExpression.expr);
            tmpId++;
            ASTNode res = new TemporaryValue(tmpId);
            ASTNode expr = nodeValueMap.get(unaryExpression.expr);
            Quat quat = new Quat(op, res, expr, null);
            quats.add(quat);
            nodeValueMap.put(unaryExpression, res);
        }
    }

    @Override
    public void visit(ASTPostfixExpression postfixExpression) throws Exception {
        String op = postfixExpression.op.value;
        tmpId++;
        ASTNode temp = new TemporaryValue(tmpId);

        Quat quat = new Quat("=", temp, postfixExpression.expr, null);
        quats.add(quat);
        Quat quat1 = new Quat(op, postfixExpression.expr, temp, null);
        quats.add(quat1);

        nodeValueMap.put(postfixExpression, temp);
    }

    @Override
    public void visit(ASTBreakStatement breakStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTContinueStatement continueStatement) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTCastExpression castExpression) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTCharConstant charConst) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTCompoundStatement compoundStat) throws Exception {
        for (ASTNode node : compoundStat.blockItems) {
            if(node instanceof ASTDeclaration) {
                // visit((ASTDeclaration)node);
            }else if (node instanceof ASTStatement) {
                visit((ASTStatement)node);
            }
        }

    }

    @Override
    public void visit(ASTConditionExpression conditionExpression) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTExpression expression) throws Exception {
        if(expression instanceof ASTArrayAccess) {
            visit((ASTArrayAccess)expression);
        }else if(expression instanceof ASTBinaryExpression) {
            visit((ASTBinaryExpression)expression);
        }else if(expression instanceof ASTCastExpression) {
            visit((ASTCastExpression)expression);
        }else if(expression instanceof ASTCharConstant) {
            visit((ASTCharConstant)expression);
        }else if(expression instanceof ASTConditionExpression) {
            visit((ASTConditionExpression)expression);
        }else if(expression instanceof ASTFloatConstant) {
            visit((ASTFloatConstant)expression);
        }else if(expression instanceof ASTFunctionCall) {
            visit((ASTFunctionCall)expression);
        }else if(expression instanceof ASTIdentifier) {
            visit((ASTIdentifier)expression);
        }else if(expression instanceof ASTIntegerConstant) {
            visit((ASTIntegerConstant)expression);
        }else if(expression instanceof ASTMemberAccess) {
            visit((ASTMemberAccess)expression);
        }else if(expression instanceof ASTPostfixExpression) {
            visit((ASTPostfixExpression)expression);
        }else if(expression instanceof ASTStringConstant) {
            visit((ASTStringConstant)expression);
        }else if(expression instanceof ASTUnaryExpression) {
            visit((ASTUnaryExpression)expression);
        }else if(expression instanceof ASTUnaryTypename){
            visit((ASTUnaryTypename)expression);
        }
    }

    @Override
    public void visit(ASTExpressionStatement expressionStat) throws Exception {
        for (ASTExpression node : expressionStat.exprs) {
            visit((ASTExpression)node);
        }
    }

    @Override
    public void visit(ASTFloatConstant floatConst) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTFunctionCall funcCall) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTGotoStatement gotoStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTIdentifier identifier) throws Exception {
        nodeValueMap.put(identifier, identifier);
    }

    @Override
    public void visit(ASTInitList initList) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTIntegerConstant intConst) throws Exception {
        nodeValueMap.put(intConst, intConst);
    }

    @Override
    public void visit(ASTIterationDeclaredStatement iterationDeclaredStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTIterationStatement iterationStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTLabeledStatement labeledStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTMemberAccess memberAccess) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTReturnStatement returnStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTSelectionStatement selectionStat) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTStringConstant stringConst) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTTypename typename) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTUnaryTypename unaryTypename) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTFunctionDefine functionDefine) throws Exception {
        visit(functionDefine.body);
    }

    @Override
    public void visit(ASTDeclarator declarator) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ASTStatement statement) throws Exception {
        if(statement instanceof ASTIterationDeclaredStatement) {
            visit((ASTIterationDeclaredStatement)statement);
        }else if(statement instanceof ASTIterationStatement) {
            visit((ASTIterationStatement)statement);
        }else if(statement instanceof ASTCompoundStatement) {
            visit((ASTCompoundStatement)statement);
        }else if(statement instanceof ASTSelectionStatement) {
            visit((ASTSelectionStatement)statement);
        }else if(statement instanceof ASTExpressionStatement) {
            visit((ASTExpressionStatement)statement);
        }else if(statement instanceof ASTBreakStatement) {
            visit((ASTBreakStatement)statement);
        }else if(statement instanceof ASTContinueStatement) {
            visit((ASTContinueStatement)statement);
        }else if(statement instanceof ASTReturnStatement) {
            visit((ASTReturnStatement)statement);
        }else if(statement instanceof ASTGotoStatement) {
            visit((ASTGotoStatement)statement);
        }else if(statement instanceof ASTLabeledStatement) {
            visit((ASTLabeledStatement)statement);
        }
    }

    @Override
    public void visit(ASTToken token) throws Exception {
        // TODO Auto-generated method stub

    }

}

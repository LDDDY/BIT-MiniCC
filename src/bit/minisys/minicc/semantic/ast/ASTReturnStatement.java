package bit.minisys.minicc.semantic.ast;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.LinkedList;
@JsonTypeName("ReturnStatement")
public class ASTReturnStatement extends ASTStatement {

	public LinkedList<ASTExpression> expr;

	public ASTReturnStatement() {
		super("ReturnStatement");
		this.expr = new LinkedList<ASTExpression>();
	}
	public ASTReturnStatement(LinkedList<ASTExpression> expr) {
		super("ReturnStatement");
		this.expr = expr;
	}
	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

}

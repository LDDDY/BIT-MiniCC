package bit.minisys.minicc.semantic.ast;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.LinkedList;
@JsonTypeName("SelectionStatement")
public class ASTSelectionStatement extends ASTStatement {
	
	public LinkedList<ASTExpression> cond;
	public ASTStatement then;
	public ASTStatement otherwise;
	
	public ASTSelectionStatement() {
		super("SelectionStatement");
	}
	public ASTSelectionStatement(LinkedList<ASTExpression> cond, ASTStatement then, ASTStatement otherwise) {
		super("SelectionStatement");
		this.cond = cond;
		this.then = then;
		this.otherwise = otherwise;
	}
	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

}

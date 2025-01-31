package bit.minisys.minicc.semantic.ast;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

@JsonTypeName("Typename")
public class ASTTypename extends ASTNode {
	public List<ASTToken> specfiers;
	public ASTDeclarator declarator;
	
	public ASTTypename() {
		super("Typename");
	}
	public ASTTypename(List<ASTToken> specList, ASTDeclarator absDeclarator) {
		super("Typename");
		this.specfiers = specList;
		this.declarator = absDeclarator;
	}
	@Override
	public void accept(ASTVisitor visitor) throws Exception {
		visitor.visit(this);
	}

}

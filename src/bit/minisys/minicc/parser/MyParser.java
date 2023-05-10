package bit.minisys.minicc.parser;
import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.parser.ast.ASTNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.CharStreams;

import java.io.File;
import java.util.Arrays;

public class MyParser implements IMiniCCParser{

    @Override
    public String run(String iFile) throws Exception {
        System.out.println("Parsing...");

        String oFile = MiniCCUtil.removeAllExt(iFile) + MiniCCCfg.MINICC_PARSER_OUTPUT_EXT;

        CharStream charStream = CharStreams.fromFileName(iFile);

        CLexer lexer = new CLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CParser parser = new CParser(tokens);
        ParseTree tree = parser.compilationUnit();

        MyVisitor visitor = new MyVisitor();
        ASTNode root = visitor.visit(tree);

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(oFile), root);

        TreeViewer treeViewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        treeViewer.open();

        return oFile;
    }
}

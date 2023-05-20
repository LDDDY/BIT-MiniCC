package bit.minisys.minicc.semantic;


import bit.minisys.minicc.semantic.ast.ASTCompilationUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class MySemanticAnalyzer implements IMiniCCSemantic {
    List<String> errList;

    @Override
    public String run(String iFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ASTCompilationUnit program = (ASTCompilationUnit) mapper.readValue(new File(iFile), ASTCompilationUnit.class);

        MyVisitor myVisitor = new MyVisitor();
        program.accept(myVisitor);

        errList = myVisitor.getErrorInfoList();
        outputErrInfo();

        System.out.println("4. SemanticAnalyse finished!");

        return null;
    }

    private void outputErrInfo() {
        if(errList == null || errList.isEmpty()) {
            return;
        }
        System.out.println("Error:");
        for(String err: errList) {
            System.out.println(err);
        }
    }
}

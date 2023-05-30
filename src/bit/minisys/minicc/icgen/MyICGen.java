package bit.minisys.minicc.icgen;

import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.internal.util.MiniCCUtil;
import bit.minisys.minicc.parser.ast.ASTCompilationUnit;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class MyICGen implements IMiniCCICGen{
    @Override
    public String run(String iFile) throws Exception {
        // iFile is xx.ast.json
        // fetch AST Tree
        ObjectMapper mapper = new ObjectMapper();
        ASTCompilationUnit program = (ASTCompilationUnit)mapper.readValue(new File(iFile), ASTCompilationUnit.class);

        MyICBuilder icBuilder = new MyICBuilder();
        program.accept(icBuilder);

        // oFile is xx.ir.txt
        String oFile = MiniCCUtil.remove2Ext(iFile) + MiniCCCfg.MINICC_ICGEN_OUTPUT_EXT;
        ExampleICPrinter icPrinter = new ExampleICPrinter(icBuilder.getQuats());
        icPrinter.print(oFile);
        System.out.println("5. ICGen finished!");
        return oFile;
    }
}

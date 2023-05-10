package bit.minisys.minicc.scanner;

import bit.minisys.minicc.MiniCCCfg;
import bit.minisys.minicc.internal.util.MiniCCUtil;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Objects;

public class MyScanner implements IMiniCCScanner{

    private String genToken(int num, String lexme, String type, int cIndex, int lIndex) {
        String strToken = "";
        strToken += "[@" + num + "," + cIndex + ":" + (cIndex + lexme.length() - 1);
        strToken += "='" + lexme + "',<" + type + ">," + (lIndex + 1) + ":" + cIndex + "]\n";
        return strToken;
    }

    private Lexer getLexer(String filePathStr) {
        // 获取源文件内容, 按行存储在ArrayList中
        ArrayList srcLines = MiniCCUtil.readFile(filePathStr);

        StringBuilder inputStrBuilder = new StringBuilder();
        for(Object srcLine: srcLines) {
            inputStrBuilder.append(srcLine).append("\n");
        }
        String inputStr = inputStrBuilder.toString();

        return new Lexer(new StringReader(inputStr));
    }

    @Override
    public String run(String iFile) throws Exception {

        Lexer lexer = getLexer(iFile);
        int tokenNum = 0; // token编号
        StringBuilder tokensBuilder = new StringBuilder();

        while(!lexer.yyatEOF()) {

            String typeName = lexer.yylex();
            if(Objects.equals(typeName, "blank")) {
                continue;
            }
            if(Objects.equals(typeName, "EOF")) {
                break;
            }

            String word = lexer.yytext();
            int colNum = lexer.getColNum();
            int lineNum = lexer.getLineNum();

            String token = genToken(tokenNum, word, typeName, colNum, lineNum);
            tokenNum++;
            tokensBuilder.append(token);

        }

        String oFile = MiniCCUtil.removeAllExt(iFile) + MiniCCCfg.MINICC_SCANNER_OUTPUT_EXT;
        MiniCCUtil.createAndWriteFile(oFile, tokensBuilder.toString());

        return oFile;
    }
}

package bit.minisys.minicc.scanner;

%%

/*options*/
%class Lexer
%public
%line
%column
%type String

/*code*/
%{
    public enum TypeName {
        identifier,
        integer_constant,
        floating_constant,
        character_constant,
        string_literal,
        blank
    }

    public int getLineNum() {
        return yyline;
    }

    public int getColNum() {
        return yycolumn;
    }
%}

/*keyword*/
Keyword = (auto|break|case|char|const|continue|default|do|double|else|enum|extern|float|for|goto|if|inline|int|long|register|restrict|return|short|signed|sizeof|static|struct|switch|typedef|union|unsigned|void|volatile|while)

/*integer constant*/
IntegerConstant = {DecimalConstant}{IntegerSuffix}?|{OctalConstant}{IntegerSuffix}?|{HexadecimalConstant}{IntegerSuffix}?

DecimalConstant = {NonzeroDigit}{Digit}*|0
OctalConstant = 0{OctalDigit}+
HexadecimalConstant = {HexadecimalPrefix}{HexadecimalDigit}+
HexadecimalPrefix = (0x|0X)
Digit = [0-9]
NonzeroDigit = [1-9]
OctalDigit = [0-7]
HexadecimalDigit = [0-9a-fA-F]

IntegerSuffix = {UnsignedSuffix}{LongSuffix}?|{UnsignedSuffix}{LongLongSuffix}|{LongSuffix}{UnsignedSuffix}?|{LongLongSuffix}{UnsignedSuffix}?

UnsignedSuffix = (u|U)
LongSuffix = (l|L)
LongLongSuffix = (ll|LL)

/*floating constant*/
FloatingConstant = {DecimalFloatingConstant}|{HexadecimalFloatingConstant}

DecimalFloatingConstant = {FractionalConstant}{ExponentPart}?{FloatingSuffix}?|{DigitSequence}{ExponentPart}{FloatingSuffix}?

HexadecimalFloatingConstant = {HexadecimalPrefix}{HexadecimalFractionalConstant}{BinaryExponentPart}{FloatingSuffix}?|{HexadecimalPrefix}{HexadecimalDigitSequence}{BinaryExponentPart}{FloatingSuffix}?

FractionalConstant = {DigitSequence}?\.{DigitSequence}|{DigitSequence}\.
ExponentPart = e{Sign}?{DigitSequence}|E{Sign}?{DigitSequence}
Sign = (\+|-)
DigitSequence = {Digit}+

HexadecimalFractionalConstant = {HexadecimalDigitSequence}?\.{HexadecimalDigitSequence}|{HexadecimalDigitSequence}\.

BinaryExponentPart = p{Sign}?{DigitSequence}|P{Sign}?{DigitSequence}
HexadecimalDigitSequence = {HexadecimalDigit}+
FloatingSuffix = (f|l|F|L)

/*universal character names*/
UniversalCharacterName = \\u{HexQuad}|\\U{HexQuad}{HexQuad}
HexQuad = {HexadecimalDigit}{4}

/*character constant*/
CharacterConstant = [LuU]?\'{CCharSequence}\'
CCharSequence = {CChar}+
CChar = [^\'\\n\\]|{Escapesequence}

Escapesequence = {SimpleEscapeSequence}|{OctalEscapeSequence}|{HexadecimalEscapeSequence}|{UniversalCharacterName}

SimpleEscapeSequence = \\(a|b|f|n|r|t|v|\'|\"|\?|\\)
OctalEscapeSequence = \\{OctalDigit}{1,3}
HexadecimalEscapeSequence = \\x{HexadecimalDigit}+

/*string literals*/
StringLiteral = {EncodingPrefix}?\"{SCharSequence}?\"
EncodingPrefix = (u8|u|U|L)
SCharSequence = {SChar}+
SChar = [^\"\\n\\]|{Escapesequence}

/*punctuators*/
Punctuator = {Punctuator1}|{Punctuator2}
Punctuator1 = (\[|\]|\(|\)|\{|\}|<|>|\.|&|\*|\+|\~|\!|\/|-|%|\^|\||\?|:|;|=|,|#)
Punctuator2 = (->|\+\+|--|<<|>>|<=|>=|==|\!=|&&|\|\||\.\.\.|\*=|\/=|%=|\+=|-=|<<=|>>=|&=|\^=|\|=|##|<:|:>|<%|%>|%:|%:%:)


%%

/*identifier*/
[ \t\n]+ { return TypeName.blank.name(); }
{Keyword} { return "\'" + yytext() + "\'"; }
[a-zA-Z_][a-zA-Z0-9_]* { return TypeName.identifier.name(); }
{IntegerConstant} { return TypeName.integer_constant.name(); }
{FloatingConstant} { return TypeName.floating_constant.name(); }
{CharacterConstant} { return TypeName.character_constant.name(); }
{StringLiteral} { return TypeName.string_literal.name(); }
{Punctuator} { return "\'" + yytext() + "\'"; }
<<EOF>> { return "EOF"; }

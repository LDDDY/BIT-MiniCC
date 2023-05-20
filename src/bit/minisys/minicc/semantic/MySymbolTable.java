package bit.minisys.minicc.semantic;

import java.util.HashSet;
import java.util.Set;

public class MySymbolTable {
    private MySymbolTable father = null;
    private Set<String> items = new HashSet<>();

    public void setFather(MySymbolTable father) {
        this.father = father;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public boolean itemExistInThisTable(String item) {
        return items.contains(item);
    }

    public boolean itemExistInThisAndFatherTables(String item) {
        return items.contains(item)
                || (father != null && father.itemExistInThisAndFatherTables(item));
    }
}

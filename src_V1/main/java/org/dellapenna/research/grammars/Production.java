package org.dellapenna.research.grammars;

import org.dellapenna.research.grammars.symbols.Symbol;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Production {

    List<Symbol> getLHS();

    List<Symbol> getRHS();

    @Override
    public String toString();

    public JSONObject toJson();

    public Symbol[] getSymbols(String type);
}

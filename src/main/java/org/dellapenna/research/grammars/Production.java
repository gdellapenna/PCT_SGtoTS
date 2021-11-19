package org.dellapenna.research.grammars;

import org.dellapenna.research.grammars.symbols.Symbol;
import java.util.List;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Production {

    List<Symbol> getLHS();

    Nonterminal getLHSNonterminal() ;

    List<Symbol> getRHS();

    Symbol[] getSymbols(String type);

    @Override
    String toString();

    JSONObject toJson();

}

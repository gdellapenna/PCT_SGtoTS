package org.dellapenna.research.grammars;

import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Grammar {

    List<? extends Production> getProductions();

    List<Nonterminal> getStartSymbols();
    Nonterminal getStartSymbol();

    @Override
    String toString();

    JSONObject toJson();

    public Symbol[] getSymbols(String type);
}

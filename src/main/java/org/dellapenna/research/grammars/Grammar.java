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

    List<Nonterminal> getStartSymbols();

    Nonterminal getStartSymbol() throws GrammarException;

    void setStartSymbol(Nonterminal s);
    
    void setStartSymbols(List<Nonterminal> s);

    void addStartSymbol(Nonterminal s);

    ///
    void addProduction(Production p) throws GrammarException;

    List<? extends Production> getProductions();

    List<? extends Production> getProductionsByLHS(Nonterminal lhs);

    ///
    public Symbol[] getSymbols(String type);

    @Override
    String toString();

    JSONObject toJson();

}

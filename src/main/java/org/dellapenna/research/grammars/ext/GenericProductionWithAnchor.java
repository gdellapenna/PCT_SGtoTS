package org.dellapenna.research.grammars.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dellapenna.research.grammars.GenericProduction;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;

public abstract class GenericProductionWithAnchor extends GenericProduction {

    private List<Integer> anchor = new ArrayList<>();

    public GenericProductionWithAnchor(Nonterminal lhs, List<Symbol> rhs) {
        super(lhs, rhs);
    }

    public GenericProductionWithAnchor(Nonterminal lhs, List<Integer> anchor, List<Symbol> rhs) throws GrammarException {
        super(lhs, rhs);
        this.anchor = anchor;
    }

    public GenericProductionWithAnchor(Nonterminal lhs, Integer[] anchor, Symbol... rhs) throws GrammarException {
        this(lhs, Arrays.asList(anchor), Arrays.asList(rhs));
    }

    public GenericProductionWithAnchor(Nonterminal lhs, Symbol... rhs) {
        this(lhs, Arrays.asList(rhs));
    }

    public List<Integer> getAnchor() {
        return anchor;
    }

    @Override
    public String toString() {
        //Se non ci sono ancore usiamo la versione classica.
        if (anchor.isEmpty()) {
            return super.toString();
        } //Altrimenti le stampiamo.
        else {
            String LHSString = "";
            for (Symbol s : getLHS()) {
                LHSString += s + " ";
            }

            String RHSString = "";
            for (Symbol s : getRHS()) {
                RHSString += s + " ";
            }
            return LHSString + " ->" + Arrays.toString(getAnchor().toArray()) + " " + RHSString;
        }
    }

   
}

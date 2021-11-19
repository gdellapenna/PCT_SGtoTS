package org.dellapenna.research.grammars;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;

public abstract class GenericProduction implements Production {

    private List<Symbol> lhs;
    private List<Symbol> rhs;

    public GenericProduction(List<Symbol> lhs, List<Symbol> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public GenericProduction(Symbol lhs, List<Symbol> rhs) {
        this(Collections.singletonList(lhs), rhs);
    }

    public GenericProduction(Nonterminal lhs, Symbol... rhs) {
        this(lhs, Arrays.asList(rhs));
    }

    @Override
    public List<Symbol> getLHS() {
        return lhs;
    }

    @Override
    public Nonterminal getLHSNonterminal() {
        for (Symbol s : getLHS()) {
            if (s instanceof Nonterminal) {
                return (Nonterminal) s;
            }
        }
        return null;
    }

    @Override
    public List<Symbol> getRHS() {
        return rhs;
    }

    @Override
    public String toString() {
        String LHSString = "";
        for (Symbol s : getLHS()) {
            LHSString += s + " ";
        }
        String RHSString = "";
        for (Symbol s : getRHS()) {
            RHSString += s + " ";
        }
        return LHSString + " -> " + RHSString;

    }

    @Override
    public Symbol[] getSymbols(String type) {
        Set<Symbol> result = new HashSet();
        for (Symbol s : getLHS()) {
            if (s.getTypeName().equals(type)) {
                result.add(s);
            }
        }

        for (Symbol s : getRHS()) {
            if (s.getTypeName().equals(type)) {
                result.add(s);
            }
        }
        return result.toArray(new Symbol[0]);
    }

}

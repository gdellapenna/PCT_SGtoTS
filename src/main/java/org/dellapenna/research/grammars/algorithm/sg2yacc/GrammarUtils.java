/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dellapenna.research.grammars.algorithm.sg2yacc;

import java.util.ArrayList;
import java.util.List;
import org.dellapenna.research.grammars.Grammar;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.Production;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author giuse
 */
public class GrammarUtils {

    private static GrammarUtils instance = null;

    public static GrammarUtils getInstance() {
        if (instance == null) {
            instance = new GrammarUtils();
        }
        return instance;
    }

    public boolean checkIfRecursive(Grammar g) throws GrammarException {

        Boolean rec = false;
        List<Production> prods = (List<Production> ) g.getProductions();
        List<Production> sprods = (List<Production> ) g.getProductionsByLHS(g.getStartSymbol());
        for (int i = 0; i < sprods.size(); i++) {
            prods.add(i, sprods.get(i));
        }

        for (Production prod : g.getProductionsByLHS(g.getStartSymbol())) {
            List<Nonterminal> list = new ArrayList<>();
            if (checkIfRecursiveProd(g, prod, list)) {
                rec = true;
            }

        }
        return rec;
    }

    private boolean checkIfRecursiveProd(Grammar g, Production prod, List<Nonterminal> list) {

        if (prod.getRHS().contains(prod.getLHSNonterminal())) {
            return true;
        }
        for (Symbol s : prod.getRHS()) {
            if (s instanceof Nonterminal) {
                if (list.contains((Nonterminal) s)) {
                    return true;
                } else {
                    list.add((Nonterminal) s);
                    for (Production sgprod : (List<Production>) g.getProductionsByLHS((Nonterminal) s)) {
                        return checkIfRecursiveProd(g, sgprod, list);
                    }
                }
            }
        }
        return false;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dellapenna.research.grammars.sg2yacc;

import java.util.ArrayList;
import java.util.List;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
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

    public boolean checkIfRecursive(SGGrammar sg) {

        Boolean rec = false;
        List<SGProduction> prods = (List<SGProduction>) sg.getProductions();
        List<SGProduction> sprods = (List<SGProduction>) sg.getProductionsByNonterminal(sg.getStartSymbol());
        for (int i = 0; i < sprods.size(); i++) {
            prods.add(i, sprods.get(i));
        }

        for (SGProduction prod : (List<SGProduction>) sg.getProductionsByNonterminal(sg.getStartSymbol())) {
            List<Nonterminal> list = new ArrayList<>();
            if (checkIfRecursiveProd(sg, prod, list)) {
                rec = true;
            }

        }
        return rec;
    }

    private boolean checkIfRecursiveProd(SGGrammar sg, SGProduction prod, List<Nonterminal> list) {

        if (prod.getRHS().contains(prod.getLHSNonterminal())) {
            return true;
        }
        for (Symbol s : prod.getRHS()) {
            if (s instanceof Nonterminal) {

                if (list.contains((Nonterminal) s)) {
                    return true;
                } else {
                    list.add((Nonterminal) s);
                    for (SGProduction sgprod : sg.getProductionsByNonterminal((Nonterminal) s)) {
                        return checkIfRecursiveProd(sg, sgprod, list);
                    }
                }

            }
        }
        return false;

    }

}

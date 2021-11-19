package org.dellapenna.research.grammars.sg2yacc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.cf.CFGProduction;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Terminal;

/**
 *
 * @author Andrea
 */
public class RelInside {

    private static RelInside instance = null;

    public static RelInside getInstance() {
        if (instance == null) {
            instance = new RelInside();
        }
        return instance;
    }

    public HashMap<Nonterminal, List<Terminal>> apply(CFGGrammar g) throws GrammarException {

        //Restituiremo una mappa che lega terminali ai propri insiemi relfollow.
        HashMap<Nonterminal, List<Terminal>> relinside_map = new HashMap<>();
        //Troviamo i nonterminali. 
        for (Symbol nt : g.getSymbols("R-NT")) {
            relinside_single(g, (Nonterminal) nt, relinside_map);
        }
        return relinside_map;
    }

    private List<Terminal> relinside_single(CFGGrammar g, Nonterminal x, Map<Nonterminal, List<Terminal>> relinside_map) throws GrammarException {
        if (relinside_map.containsKey(x)) {
            return relinside_map.get(x);
        } else if (x instanceof Terminal) {
            return Collections.singletonList((Terminal) x);
        } else {
            Set<Terminal> relinside_set = new LinkedHashSet<>();
            //Prendiamo le produzioni che a sinistra hanno quel nonterminale.
            for (CFGProduction production : (List<CFGProduction>) g.getProductionsByNonterminal(x)) {
                List<Integer> anchors;

                if (production.getAnchor().isEmpty()) {
                    //Se non ci sono ancore prendiamo tutto
                    anchors = IntStream.rangeClosed(1, production.getRHS().size())
                            .boxed().collect(Collectors.toList());
                } else {
                    anchors = production.getAnchor();
                }
                for (Integer i : anchors) {
                    Symbol s = production.getRHS().get(i - 1);
                    if (s instanceof CombinedSymbol && ((CombinedSymbol) s).isTerminal()) {
                        relinside_set.add((Terminal) ((CombinedSymbol) production.getRHS().get(i - 1)).getSymbol(1));
                    } else {
                        relinside_set.addAll(relinside_single(g, (Nonterminal) s, relinside_map));
                    }
                }
            }

            List<Terminal> relinside_list = new ArrayList<>(relinside_set);
            relinside_map.put(x, relinside_list);
            return relinside_list;
        }

    }
}

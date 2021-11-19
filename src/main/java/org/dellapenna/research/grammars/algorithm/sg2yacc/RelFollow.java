package org.dellapenna.research.grammars.algorithm.sg2yacc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGrammar;
import org.dellapenna.research.grammars.cf.ext.CFProductionWithAnchor;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author Andrea
 */
public class RelFollow {

    private static RelFollow instance = null;

    public static RelFollow getInstance() {
        if (instance == null) {
            instance = new RelFollow();
        }
        return instance;
    }

    public HashMap<Nonterminal, List<Relation>> apply(CFGrammar g) throws GrammarException {

        //Restituiremo una mappa che lega terminali ai propri insiemi relfollow.
        HashMap<Nonterminal, List<Relation>> relfollow_map = new HashMap<>();
        //Troviamo i nonterminali. 
        for (Symbol nt : g.getSymbols("R-NT")) {
            relfollow_single(g, (Nonterminal) nt, relfollow_map);
        }
        return relfollow_map;
    }

    private List<Relation> relfollow_single(CFGrammar g, Nonterminal A, Map<Nonterminal, List<Relation>> relfollow_map) throws GrammarException {
        if (relfollow_map.containsKey(A)) {
            return relfollow_map.get(A);
        } else {
            Set<Relation> relfollow_set = new LinkedHashSet<>();
            //REGOLA 1: Se è uno Start Symbol, va aggiunto solo REL$.
            if (g.getStartSymbols().contains(A)) {
                relfollow_set.add(AlphaRelation.create("REL$"));
            } else {
                for (CFProductionWithAnchor production : (List<CFProductionWithAnchor>) g.getProductions()) {
                    List<Symbol> rhs = production.getRHS();
                    for (int i = 0; i < rhs.size(); ++i) {
                        //Cerchiamo il simbolo.
                        if (rhs.get(i).equals(A)) {
                            //Controlliamo che non sia l'ultimo. In questo caso, REGOLA 2a
                            if (i < rhs.size() - 1) {
                                boolean found = false;
                                //Prendiamo la parte a destra del simbolo trovato.
                                for (int j = i + 1; j < rhs.size(); ++j) {
                                    AlphaRelation rel = (AlphaRelation) ((CombinedSymbol) rhs.get(j)).getSymbol(0);
                                    if (rel.getIndex() == (j - (i + 1)) + 1) {
                                        relfollow_set.add(rel);
                                        found = true;
                                    }
                                }
                                //Se non ne abbiamo trovato nessuno applichiamo la regola 2b
                                if (!found) {
                                    relfollow_set.addAll(relfollow_single(g, production.getLHSNonterminal(), relfollow_map));
                                }
                            } else {
                                //Altrimenti regola 3
                                relfollow_set.addAll(relfollow_single(g, production.getLHSNonterminal(), relfollow_map));
                            }
                        }
                    }
                }
            }
            List<Relation> relfollow_list = new ArrayList<>(relfollow_set);
            relfollow_map.put(A, relfollow_list);
            return relfollow_list;
        }
    }
}

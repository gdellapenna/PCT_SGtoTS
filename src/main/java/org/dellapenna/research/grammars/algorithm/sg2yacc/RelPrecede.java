package org.dellapenna.research.grammars.algorithm.sg2yacc;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author Andrea
 */
public class RelPrecede {

    private static RelPrecede instance = null;

    public static RelPrecede getInstance() {
        if (instance == null) {
            instance = new RelPrecede();
        }
        return instance;
    }

    public HashMap<Nonterminal, List<Relation>> apply(SGGrammar sg) throws GrammarException {

        HashMap<Nonterminal, List<Relation>> relprecede_map = new HashMap<>();
        //Per ogni nonterminale, costruiamo la sua execute.        
        for (Symbol nt : sg.getSymbols("NT")) {
            relprecede_single(sg, (Nonterminal) nt, relprecede_map);
        }
        return relprecede_map;

    }

    private List<Relation> relprecede_single(SGGrammar sg, Nonterminal A, Map<Nonterminal, List<Relation>> relprecede_map) throws GrammarException {
        if (relprecede_map.containsKey(A)) {
            return relprecede_map.get(A);
        } else {
            LinkedHashSet<Relation> relprecede_set = new LinkedHashSet<>();
            //Come da algoritmo, per lo start symbol inseriamo tanti Sp quante sono le produzioni con esso in testa.
            if (A.equals(sg.getStartSymbol())) {
                //Per ogni produzione di questo tipo, aggiungiamo un RELSP.    
                for (int i = 0; i < sg.getProductionsByLHS(A).size(); ++i) {
                    relprecede_set.add(AlphaRelation.create("RELSP" + (i + 1)));
                }
            } else {
                //Controlliamo le produzioni dove quel non-terminale appare a destra.
                for (SGProduction p : (List<SGProduction>) sg.getProductions()) {
                    if (p.getRHS().contains(A)) {
                        for (int i = 0; i < p.getRHS().size(); ++i) {
                            if (p.getRHS().get(i).equals(A)) {
                                //Se il simbolo si trova in prima posizione, come da algorimo, inseriamo in tutto quello
                                //che c'è nella Relprecede del simbolo a sinistra.
                                if (i == 0) {
                                    relprecede_set.addAll(relprecede_single(sg, p.getLHSNonterminal(), relprecede_map));
                                } else {
                                    //altrimenti aggiungiamo semplicemente la relazione che si trova appena prima. 
                                    relprecede_set.add((Relation) p.getRHS().get(i - 1));
                                }
                            }
                        }
                    }
                }
            }
            List<Relation> relprecede_list = new ArrayList<>(relprecede_set);
            relprecede_map.put(A, relprecede_list);
            return relprecede_list;
        }
    }

    

}

package org.dellapenna.research.grammars.algorithm.sg2yacc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author Andrea
 */
public class Normalize {

    private static Normalize instance = null;

    public static Normalize getInstance() {
        if (instance == null) {
            instance = new Normalize();
        }
        return instance;
    }

    public SGGrammar apply(SGGrammar sg) throws GrammarException {

        return normalize_iteration(sg);
    }

    private SGGrammar normalize_iteration(SGGrammar sg) throws GrammarException {
        //controlliamo se la grammatica è ricorsiva
        if (GrammarUtils.getInstance().checkIfRecursive(sg)) {
            throw new GrammarException("La grammatica fornita è ricorsiva.");
        }
        //Controlliamo se la grammatica contiene duplicati.
        Symbol A = next_duplicate_LHSNonterminal(sg);
        if (A != null) {
            //Costruiamo la grammatica da restituire. Lo Start Symbol è lo stesso.
            SGGrammar next_sg = new SGGrammar();
            next_sg.setStartSymbol(sg.getStartSymbol());

            List<Symbol> A_split_symbols = new ArrayList<>();
            int i = 0;
            //Per ogni produzione con A a sinistra in sg, inseriamo una produzione col pedice in result
            for (SGProduction p : (List<SGProduction>) sg.getProductions()) {
                if (p.getLHSNonterminal().equals(A)) {
                    i++;
                    Nonterminal Ai = AlphaNonterminal.create(A.toString() + i);
                    A_split_symbols.add(Ai);
                    SGProduction pi = new SGProduction(Ai, p.getAnchor(), p.getRHS());
                    next_sg.addProduction(pi);
                }
            }
            /* Ora, dopo aver numerato i simboli, dobbiamo moltiplicare le produzioni sulla destra.
            * Analizziamo ogni produzione. Se contengono il simbolo ripetuto a a destra, le modifichiamo,
            * Aggiungendo tante produzioni quanti sono i simboli con pedice.
            * Altrimenti semplicemente copiamo le produzioni nella nuova grammatica 
             */
            for (SGProduction p : (List<SGProduction>) sg.getProductions()) {
                //Se la produzione contiene il simbolo a destra...
                if (p.getRHS().contains((Symbol) A)) {
                    List<List<Symbol>> new_rhss = split_rhs(p.getRHS(), A, A_split_symbols);
                    for (List<Symbol> new_rhs : new_rhss) {
                        next_sg.addProduction(p.getLHSNonterminal(), p.getAnchor(), new_rhs.toArray(new Symbol[0]));
                    }
                } else {
                    //Copiamo la produzione nel result se non era una di quella con A nel LHS
                    if (!p.getLHS().get(0).equals(A)) {
                        next_sg.addProduction(p);
                    }
                }
            }
            return normalize_iteration(next_sg);
        } else {
            return sg;
        }
    }

    private List<List<Symbol>> split_rhs(List<Symbol> rhs, Symbol A, List<Symbol> A_split_symbols) {

        List<Symbol> new_rhs_common_prefix = new LinkedList<>();
        List<List<Symbol>> A_split_productions = new LinkedList<>();

        for (int i = 0; i < rhs.size(); i++) {
            if (rhs.get(i).equals(A)) {
                List<List<Symbol>> rhs_continuations = split_rhs(rhs.subList(i + 1, rhs.size()), A, A_split_symbols);
                for (Symbol Ai : A_split_symbols) {
                    for (List<Symbol> rhs_continuation : rhs_continuations) {
                        List<Symbol> new_rhs = new LinkedList<>();
                        new_rhs.addAll(new_rhs_common_prefix);
                        new_rhs.add(Ai);
                        new_rhs.addAll(rhs_continuation);
                        A_split_productions.add(new_rhs);
                    }
                }
                return A_split_productions;
            } else {
                new_rhs_common_prefix.add(rhs.get(i));
            }
        }
        //se arriviamo qui, vuol dire che la rhs non conteneva A...
        return Collections.singletonList(rhs);

    }

    private Symbol next_duplicate_LHSNonterminal(SGGrammar sg) throws GrammarException {
        for (int i = 0; i < sg.getProductions().size(); ++i) {
            SGProduction p = (SGProduction) sg.getProductions().get(i);
            if (p.getLHSNonterminal().equals(sg.getStartSymbol())) {
                continue;
            }
            for (int j = i + 1; j < sg.getProductions().size(); ++j) {
                SGProduction q = (SGProduction) sg.getProductions().get(j);
                if (q.getLHSNonterminal().equals(p.getLHSNonterminal())) {
                    return p.getLHSNonterminal();
                }
            }
        }
        return null;
    }
}

package org.dellapenna.research.grammars.algorithm.sg2yacc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGrammar;
import org.dellapenna.research.grammars.cf.ext.CFProductionWithAnchor;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author Andrea
 */
public class SpatialToString {

    private static SpatialToString instance = null;

    public static SpatialToString getInstance() {
        if (instance == null) {
            instance = new SpatialToString();
        }
        return instance;
    }

    public CFGrammar apply(SGGrammar sg, Map<Nonterminal, List<Relation>> relPrecedeResult) throws GrammarException {

        CFGrammar g = new CFGrammar();
        List<SGProduction> productions = (List<SGProduction>) sg.getProductions();

        //Per ogni produzione ne generiamo tante quante sono le relazioni in relprecede
        for (SGProduction p : productions) {
            Nonterminal A = p.getLHSNonterminal();
            if (!A.equals(sg.getStartSymbol())) {
                //Così otteniamo la lista di relazioni in Relprecede(A)
                List<Relation> relprecede_A = relPrecedeResult.get(A);
                //per ogni rel in Relprecede(A) formiamo una nuova produzione
                for (Relation REL : relprecede_A) {
                    //Uniamo la REL al simbolo a sinistra.
                    CombinedSymbol new_LHS = CombinedSymbol.create(REL, A);
                    List<Symbol> new_RHS = new ArrayList<>();
                    //Uniamo la REL al primo simbolo a destra.
                    CombinedSymbol new_RHS_first = CombinedSymbol.create(REL, p.getRHS().get(0));
                    new_RHS_first.setTerminal(p.getRHS().get(0).isTerminal());
                    new_RHS.add(new_RHS_first);
                    //Creiamo la sigma dei restanti simboli.
                    new_RHS.addAll(sigma(p.getRHS().subList(1, p.getRHS().size())));
                    //Riprendiamo la stessa ancora.
                    List<Integer> anchor = p.getAnchor();
                    //Creiamo la produzione
                    CFProductionWithAnchor newprod = new CFProductionWithAnchor(new_LHS, anchor, new_RHS);
                    g.addProduction(newprod);
                }
            }
        }

        //trattamento ad-hoc per gli start symbols
        int starts = 0;
        Nonterminal S = sg.getStartSymbol();
        List<Relation> relprecede_S = relPrecedeResult.get(S);
        for (SGProduction p : (List<SGProduction>) sg.getProductionsByLHS(S)) {
            Relation REL = relprecede_S.get(starts);
            CombinedSymbol newLHS = CombinedSymbol.create(REL, S);
            List<Symbol> newRHS = new ArrayList<>();
            CombinedSymbol new_RHS_first = CombinedSymbol.create(REL, p.getRHS().get(0));

            new_RHS_first.setTerminal(p.getRHS().get(0).isTerminal());

            newRHS.add(new_RHS_first);
            newRHS.addAll(sigma(p.getRHS().subList(1, p.getRHS().size())));
            List<Integer> anchor = p.getAnchor();
            CFProductionWithAnchor newprod = new CFProductionWithAnchor(newLHS, anchor, newRHS);
            g.addProduction(newprod);
            g.addStartSymbol(newprod.getLHSNonterminal());
            starts++;
        }

        //Eliminiamo gli start Symbol duplicati.
        LinkedHashSet<Nonterminal> hashSet = new LinkedHashSet<>(g.getStartSymbols());
        ArrayList<Nonterminal> listNoDup = new ArrayList<>(hashSet);
        g.setStartSymbols(listNoDup);

        removeUselessProductions(g);

        return g;
    }

    private void removeUselessProductions(CFGrammar g) throws GrammarException {
        List<Nonterminal> useless_symbols = new ArrayList<>();
        for (Symbol A : g.getSymbols("R-NT")) {
            Boolean A_is_useful = false;
            if (g.getStartSymbols().contains((Nonterminal) A)) {
                A_is_useful = true;
            } else {
                for (CFProductionWithAnchor p : (List<CFProductionWithAnchor>) g.getProductions()) {
                    if (p.getRHS().contains(A)) {
                        A_is_useful = true;
                        break;
                    }
                }
            }
            if (!A_is_useful) {
                useless_symbols.add((Nonterminal) A);
            }
        }
        for (Nonterminal A : useless_symbols) {
            List<CFProductionWithAnchor> useless_productions = (List<CFProductionWithAnchor>) g.getProductionsByLHS(A);
            Iterator<CFProductionWithAnchor> it = useless_productions.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        if (!useless_symbols.isEmpty()) { //Andrea's fix
            removeUselessProductions(g);
        }
    }

    private List<CombinedSymbol> sigma(List<Symbol> alpha) throws GrammarException {
        List<CombinedSymbol> sigma_result = new ArrayList<CombinedSymbol>();
        //Per prima cosa, la lista di simboli deve iniziare con una relazione.
        //Altrimenti qualcosa è andato storto.
        if (!alpha.isEmpty()) {
            if (!(alpha.get(0) instanceof AlphaRelation)) {
                throw new GrammarException("Stringa malformata");
            } else {
                //Dobbiamo trasformare a due a due i simboli in simboli combinati.    
                for (int i = 0; i < alpha.size() - 1; i += 2) {
                    CombinedSymbol combined = CombinedSymbol.create(alpha.get(i), alpha.get(i + 1));
                    combined.setTerminal(!(alpha.get(i + 1) instanceof Nonterminal));
                    sigma_result.add(combined);
                }
            }
        }
        return sigma_result;
    }

}

package org.dellapenna.research.grammars.sg2yacc.old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.cf.CFGProduction;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Terminal;

/**
 *
 * @author Andrea
 */
public class _SGtoG {

    public static CFGGrammar execute(SGGrammar sg, Map<Nonterminal, List<Relation>> Relprecede) throws GrammarException {

        CFGGrammar result = new CFGGrammar();
        List<SGProduction> productions = (List<SGProduction>) sg.getProductions();
        Boolean start = false;
        int starts = 0;

        //Come da execute, per ogni produzione ne generiamo tante quante sono le relazioni in relprecede
        for (SGProduction prod : productions) {
            Symbol A = prod.getLHSNonterminal();
            //Così otteniamo la lista di relazioni in Relprecede(A)
            List<Relation> relations = Relprecede.get(A);
            //Come da execute, per ogni rel in Relprecede(A) formiamo una nuova produzione
            for (Relation rel : relations) {
                if (A.equals(sg.getStartSymbol())) {
                    start = true;
                    rel = relations.get(starts);
                }
                //Uniamo la REL al simbolo a sinistra.
                CombinedSymbol left = CombinedSymbol.create(rel, prod.getLHSNonterminal());
                List<Symbol> Right = new ArrayList<Symbol>();
                //Uniamo la REL al primo simbolo a destra.
                CombinedSymbol firstright = CombinedSymbol.create(rel, prod.getRHS().get(0));
                if (prod.getRHS().get(0) instanceof Terminal) {
                    firstright.setTerminal(true);
                }
                Right.add(firstright);
                //Creiamo la epsilon dei restanti simboli.
                List<Symbol> withoutfirst = new ArrayList<Symbol>();
                withoutfirst.addAll(prod.getRHS());
                withoutfirst.remove(0);
                if (prod.getRHS().size() > 1) {
                    Right.addAll(unify(withoutfirst));
                }
                //Riprendiamo la stessa ancora.
                List<Integer> anchors = prod.getAnchor();
                //Creiamo la produzione
                CFGProduction newprod = new CFGProduction(left, anchors, Right);
                result.addProduction(newprod);
                //Questo serve ad impostare il "corretto" Start Symbol.
                if (start == true) {
                    result.addStartSymbol(newprod.getLHSNonterminal());
                    //result.setStartSymbol(newprod.getLHSNonterminal()); //GDP EDIT
                    starts++;
                    break;

                }

                start = false;
            }

            //Eliminiamo gli start Symbol duplicati.
            LinkedHashSet<Nonterminal> hashSet = new LinkedHashSet<>(result.getStartSymbols());

            ArrayList<Nonterminal> listNoDup = new ArrayList<>(hashSet);

            result.setStartSymbols(listNoDup);

        }

        //Eliminiamo produzioni inutili
        for (Symbol nt : result.getSymbols("RNT")) {

            Boolean useful = false;

            for (CFGProduction prod3 : (List<CFGProduction>) result.getProductions()) {

                if (prod3.getRHS().contains(nt)) {
                    useful = true;
                }

            }
            if (result.getStartSymbols().contains(nt)) {

                useful = true;

            }
            if (!useful) {

                List<CFGProduction> todelete = (List<CFGProduction>) result.getProductionsByNonterminal((Nonterminal) nt);
                for (Iterator<CFGProduction> iterator = todelete.iterator(); iterator.hasNext();) {
                    CFGProduction value = iterator.next();

                    iterator.remove();

                }

            }

        }
//        System.out.println("E il risultato è :" + result);
//        System.out.println("_________________________");
//        System.out.println("Gli Start Symbol sono :" + result.getStartSymbols());
//        System.out.println("_________________________");
        return result;
    }

    private static List<CombinedSymbol> unify(List<Symbol> alpha) throws GrammarException {

        int i = 0;
        List<CombinedSymbol> Epsilon = new ArrayList<CombinedSymbol>();

        //Per prima cosa, la lista di simboli deve iniziare con una relazione.
        //Altrimenti qualcosa è andato storto.
        if (!(alpha.get(i) instanceof AlphaRelation)) {

            throw new GrammarException("Stringa malformata");
        } else {
            //Dobbiamo trasformare a due a due i simboli in simboli combinati.    
            while (i < alpha.size() - 1) {

                CombinedSymbol combined;

                if (alpha.get(i + 1) instanceof Nonterminal) {
                    combined = CombinedSymbol.create(alpha.get(i), alpha.get(i + 1));
                    combined.setTerminal(false);
                } else {
                    combined = CombinedSymbol.create(alpha.get(i), alpha.get(i + 1));
                    combined.setTerminal(true);
                }

                Epsilon.add(combined);

                i = i + 2;

            }

        }
        return Epsilon;
    }

}

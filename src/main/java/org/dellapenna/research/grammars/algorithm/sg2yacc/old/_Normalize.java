package org.dellapenna.research.grammars.algorithm.sg2yacc.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.Production;
import org.dellapenna.research.grammars.sg.SGGrammar;
import org.dellapenna.research.grammars.sg.SGProduction;
import org.dellapenna.research.grammars.algorithm.sg2yacc.GrammarUtils;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;

/**
 *
 * @author Andrea
 */
public class _Normalize {

    public static SGGrammar execute(SGGrammar sg) throws GrammarException {

        if (GrammarUtils.getInstance().checkIfRecursive(sg)) {

            throw new GrammarException("La grammatica fornita è ricorsiva.");

        }
        int count = 0;
        List<SGProduction> productions = (List<SGProduction>) sg.getProductions();
        List<Symbol> nonterminals = Arrays.asList(sg.getSymbols("NT"));
        Nonterminal start = sg.getStartSymbol();

        //Controlliamo se la grammatica contiene duplicati.
        Symbol a = hasduplicates(productions, nonterminals, start);

        //Fase conclusiva della ricorsione: se non ci sono più modifiche da fare ritorniamo subito.
        if (a == null) {
            return sg;
        }

        //Costruiamo la grammatica da restituire. Lo Start Symbol è lo stesso.
        SGGrammar result = new SGGrammar();

        result.setStartSymbol(start);

        if (a != null) {
            int i = 0;
            List<SGProduction> AProductions = new ArrayList<SGProduction>();
            List<Symbol> Added = new ArrayList<Symbol>();

            //Facciamo una lista delle produzioni che hanno A a sinistra.
            for (Production p : productions) {
                if (p.getLHS().get(0).equals(a)) {
                    AProductions.add((SGProduction) p);
                }
            }

            //Per ogni produzione duplicata in sg, inseriamo una produzione col pedice in result.
            while (i < howmanyduplicates(a, productions)) {
                Nonterminal A = AlphaNonterminal.create(a.toString() + i);
                Added.add(A);
                count++;
                SGProduction P = new SGProduction(A, AProductions.get(i).getAnchor(), AProductions.get(i).getRHS());
                result.addProduction(P);

                i++;
            }

            //Ora, dopo aver numerato i simboli, dobbiamo moltiplicare le produzioni sulla destra.
            /*Analizziamo ogni produzione. Se contengono il simbolo ripetuto a a destra, le modifichiamo,
           * Aggiungendo tante produzioni quanti sono i simboli con pedice.
           *Altrimenti semplicemente copiamo le produzioni nella nuova grammatica */
            for (SGProduction p : productions) {

                List<Symbol> newRHS = p.getRHS();

                //Se la produzione contiene il simbolo a destra...
                if (p.getRHS().contains((Symbol) a)) {
                    int it = 0;
                    List<Integer> APositions = new ArrayList<>();

                    //Trova la posizione dell'elemento da modificare
                    while (it < newRHS.size()) {

                        if (newRHS.get(it).equals(a)) {
                            int pos = it;
                            APositions.add(pos);
                        }

                        it++;
                    }

                    int j = 0;

                    List<SGProduction> list = new LinkedList<SGProduction>();
                    SGProduction sgp;

                    if (APositions.size() == 1) {

                        while (j < Added.size()) {
                            newRHS.set(APositions.get(0), Added.get(j));

                            //Lo cambiamo e aggiungiamo le produzioni in result.
                            //Una produzione per ogni elemento in Added.
                            Symbol[] symbolarr = new Symbol[newRHS.size()];
                            symbolarr = newRHS.toArray(symbolarr);

                            result.addProduction(p.getLHSNonterminal(), p.getAnchor(), symbolarr);

                            j++;
                        }
                    } else {

                        multipleAs(p, Added, result, p.getRHS().get(APositions.get(0)), APositions.size());
                        j++;

                        //throw new GrammarException("Ci sono troppe variabili dello stesso tipo a destra");                    
                    }

                } else {
                    //Copiamo la produzione nel result
                    if (!p.getLHS().get(0).equals(a)) {
                        result.addProduction(p);
                    }

                }
            }

        }

        return execute(result);

    }

    private static List<SGProduction> multipleAs(SGProduction prod, List<Symbol> nonterminals, SGGrammar result, Symbol toSub, int times) throws GrammarException {

        List<SGProduction> resultlist = new LinkedList<>();
        List<List<Symbol>> newrights = multiplerightAs(prod.getRHS(), nonterminals, toSub, times);

        for (List<Symbol> newright : newrights) {

            Symbol[] symbolarr = new Symbol[newright.size()];
            symbolarr = newright.toArray(symbolarr);
            result.addProduction(prod.getLHSNonterminal(), prod.getAnchor(), symbolarr);

        }

        return resultlist;

    }

    private static List<List<Symbol>> multiplerightAs(List<Symbol> prod, List<Symbol> nonterminals, Symbol toSub, int times) {

        List<List<Symbol>> newrights = new LinkedList<>();
        List<List<Symbol>> actual = new LinkedList<>();

        if (prod.contains(toSub)) {

            for (Symbol nt : nonterminals) {

                List<Symbol> newprod = sub(prod, nt, toSub);

                actual.addAll(multiplerightAs(newprod, nonterminals, toSub, times));

            }
        } else {
            actual.add(prod);
        }

        return actual;

    }

    //private static List<List<Symbol>> singlerightAs()
    private static List<Symbol> sub(List<Symbol> right, Symbol nonterminal, Symbol toSub) {

        List<Symbol> newright = new LinkedList<>();
        newright.addAll(right);

        for (int it = 0; it < right.size(); it++) {

            if (right.get(it).equals(toSub)) {

                newright.set(it, nonterminal);
                break;

            }

        }

        return newright;

    }

    private static Symbol hasduplicates(List<? extends Production> productions, List<Symbol> nonterminals, Nonterminal start) {
        int cont = 0;
        for (Symbol nt : nonterminals) {
            cont = 0;
            if (!nt.equals(start)) {
                for (Production prod : productions) {
                    if (prod.getLHS().get(0).equals(nt)) {
                        cont++;
                    }
                }
                if (cont > 1) {
                    return nt;
                }
            }
        }
        return null;

    }

    private static int howmanyduplicates(Symbol A, List<? extends Production> prods) {
        int cont = 0;
        for (Production p : prods) {
            if (p.getLHS().get(0).equals(A)) {
                cont++;
            }
        }
        return cont;
    }

}

package org.dellapenna.research.grammars.sg2yacc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.cf.CFGProduction;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.Action;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.dellapenna.research.grammars.ts.TSProduction;

/**
 *
 * @author Andrea
 */
public class GrammarToSchema {

    private static GrammarToSchema instance = null;

    public static GrammarToSchema getInstance() {
        if (instance == null) {
            instance = new GrammarToSchema();
        }
        return instance;
    }

    public CFGGrammar apply(CFGGrammar g, Map<Nonterminal, List<Terminal>> relInsideResult, Map<Nonterminal, List<Relation>> relFollowResult) throws GrammarException {
        System.out.println("** Pass1: ");
        CFGGrammar pass1 = pass1(g, relInsideResult, relFollowResult);
        System.out.println(pass1);
        System.out.println("** Pass2: ");
        CFGGrammar pass2 = pass2(pass1, relInsideResult, relFollowResult); //FORSE NON SERVE
        System.out.println(pass2);
        System.out.println("** Pass3+4: ");
        CFGGrammar result = pass34(pass2, relInsideResult, relFollowResult);
        return result;
    }

    public CFGGrammar pass1(CFGGrammar g, Map<Nonterminal, List<Terminal>> relInsideResult, Map<Nonterminal, List<Relation>> relFollowResult) throws GrammarException {

        CFGGrammar result = new CFGGrammar();
        result.setStartSymbols(g.getStartSymbols());
        
        //Per ogni produzione
        for (CFGProduction p : (List<CFGProduction>) g.getProductions()) {
            List<Symbol> new_rhs = new ArrayList<>();
            TSProduction new_p = new TSProduction(p.getLHSNonterminal(), p.getAnchor(), new_rhs);
            //List<Integer> new_anchor = new ArrayList<>(p.getAnchor());
            for (int i = 0; i < p.getRHS().size(); i++) {
                CombinedSymbol s = (CombinedSymbol) p.getRHS().get(i);
                if (i == 0) { //s è all'inizio
                    if (s.isTerminal()) { //1.b
                        new_rhs.add(s.getSymbol(1));
                    } else {
                        new_rhs.add(s); //??
                    }
                } else { // s è in mezzo
                    //int added_actions=0;
                    int index = ((AlphaRelation) s.getSymbol(0)).getIndex();
                    CombinedSymbol t = (CombinedSymbol) p.getRHS().get(i - index);
                    List<Terminal> relinside_s;
                    if (s.isTerminal()) {
                        relinside_s = Collections.singletonList((Terminal) s.getSymbol(1));
                    } else {
                        relinside_s = relInsideResult.get(s);
                    }
                    List<Terminal> relinside_t;
                    if (t.isTerminal()) {
                        relinside_t = Collections.singletonList((Terminal) t.getSymbol(1));
                    } else {
                        relinside_t = relInsideResult.get(t);
                    }
                    if (s.isTerminal()) { //s T (1)
                        for (Terminal A : relinside_t) {
                            new_p.addActionAt(new_rhs.size() - 1, Action.createAction((Relation) s.getSymbol(0), A, (Terminal) s.getSymbol(1)));

                        }
                        new_rhs.add(s.getSymbol(1));
                    } else { //s NT 
                        if (t.isTerminal()) { //s NT, t T (1.a)
                            for (Terminal A : relinside_s) {
                                new_p.addActionAt(new_rhs.size() - 1, Action.createAction((Relation) s.getSymbol(0), (Terminal) t.getSymbol(1), A));
                            }
                        } else { //s NT, t NT (????)
                            for (Terminal B : relinside_t) {
                                for (Terminal A : relinside_s) {
                                    new_p.addActionAt(new_rhs.size() - 1, Action.createAction((Relation) s.getSymbol(0), B, A));
                                }
                            }
                        }
                        new_rhs.add(s);
                    }
                }
            }
            result.addProduction(new_p);
        }
        
        return result;

    }

    public CFGGrammar pass2(CFGGrammar g, Map<Nonterminal, List<Terminal>> relInsideResult, Map<Nonterminal, List<Relation>> relFollowResult) throws GrammarException {

        CFGGrammar result = new CFGGrammar();
        result.setStartSymbols(g.getStartSymbols());
        
        //Punto 2: inseriamo le relazioni al termine delle produzioni che hanno un terminale come ultimo simbolo.
        for (CFGProduction p : (List<CFGProduction>) g.getProductions()) {
            List<Symbol> new_rhs = new ArrayList<>();
            TSProduction new_p = new TSProduction(p.getLHSNonterminal(), p.getAnchor(), new_rhs);
            new_p.addActions(((TSProduction)p).getActions());

            //se c'è il pedice invece lo applichiamo a quei terminali.
            List<Integer> anchors = new ArrayList(p.getAnchor());
            //Se non c'è il pedice, lo applichiamo all'ultimo terminale.
            if (anchors.isEmpty()) {
                anchors.add(p.getRHS().size());
            }

            for (int i = 0; i < p.getRHS().size(); i++) {
                Symbol s = p.getRHS().get(i);
                new_rhs.add(s);
                if (anchors.contains(i + 1)) {
                    if (s instanceof CombinedSymbol) {
                        s = ((CombinedSymbol) s).getSymbol(1);
                    }
                    if (s instanceof Terminal) { // s T a fine produzione o sull'ancora (2)
                        for (Relation REL : relFollowResult.get(p.getLHSNonterminal())) {
                            //simbolo a cui si riferisce la REL
                            Symbol t =  p.getRHS().get(i - ((AlphaRelation) REL).getIndex() +1 /*FORSE, CONSIDERATO CHE SIAMO NEL FOLLOW*/);
                            if (!t.isTerminal()) {
                                //le REL che si riferiscono a un terminale sono già gestite al punto 1
                                for (Terminal t1 : relInsideResult.get((Nonterminal) t)) {
                                    new_p.addActionAt(new_rhs.size() - 1, Action.createAction(REL, s, t1));
                                }
                            }
                        }

                    } else if (s instanceof CombinedSymbol) { // s NT o s REL_x a fine produzione o sull'ancora (2???)
                        for (Terminal t2 : relInsideResult.get((CombinedSymbol) s)) {
                            for (Relation REL : relFollowResult.get(p.getLHSNonterminal())) {
                                //simbolo a cui si riferisce la REL
                                Symbol t =  p.getRHS().get(i - ((AlphaRelation) REL).getIndex() +1 /*FORSE, CONSIDERATO CHE SIAMO NEL FOLLOW*/);
                                if (!t.isTerminal()) {
                                    //le REL che si riferiscono a un terminale sono già gestite al punto 1
                                    for (Terminal t1 : relInsideResult.get((Nonterminal) t)) {
                                        new_p.addActionAt(new_rhs.size() - 1, Action.createAction(REL, t2, t1));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            result.addProduction(new_p);
        }
        //Fine punto 2

        return result;

    }

    public CFGGrammar pass34(CFGGrammar g, Map<Nonterminal, List<Terminal>> relInsideResult, Map<Nonterminal, List<Relation>> relFollowResult) throws GrammarException {

        //Punto 3: inseriamo {()} alla fine dei terminali nelle produzioni che non puntano direttamente ad essi.
        //FORSE NON SERVE
        for (CFGProduction p : (List<CFGProduction>) g.getProductions()) {
            if (!(p.getAnchor().isEmpty()) && !(p.getAnchor().contains(p.getRHS().size())) && (p.getRHS().get(p.getRHS().size()-1).isTerminal())) {
                ((TSProduction) p).addActionAt(p.getRHS().size() - 1, Action.createEmptyAction());
            }
        }
        //Fine punto 3

        //Punto 4: Inseriamo le nuove produzioni S per ogni Sp_S.
        Nonterminal S = AlphaNonterminal.create("StartSymbol");
        for (Nonterminal start : g.getStartSymbols()) {
            g.addProduction(new CFGProduction(S, start));
        }
        //Fine punto 4

        g.setStartSymbol(S);
        return g;

    }

    //////////////////////////
//    public static CFGGrammar execute(CFGGrammar g, Map<Nonterminal, List<Terminal>> relInsideResult, Map<Nonterminal, List<Relation>> relFollowResult) throws GrammarException {
//
//        CFGGrammar result = new CFGGrammar();
//
//        //Per ogni produzione
//        for (CFGProduction p : (List<CFGProduction>) g.getProductions()) {
//
//            List<Symbol> new_RHS = new ArrayList<>();
//            //Andremo a fare lo "scan" di prod e fare le eventuali modifiche in newprod. Quindi ci serve una nuova parte destra.
//            //Inoltre, newprod cambia dimensione, mentre prod rimane fissa. Per questo teniamo traccia della differenza negli iteratori
//            //tramite l'intero diff.
//            new_RHS.addAll(p.getRHS());
//            CFGProduction new_p = new CFGProduction(p.getLHSNonterminal(), p.getAnchor(), new_RHS);
//            int diff = 0;
//
//            //Per ogni terminale in quella produzione
//            for (int i = 0; i < p.getRHS().size(); i++) {
//                if (!(p.getRHS().get(i) instanceof CombinedSymbol)) {
//                    continue; //perchè dovrebbe succedere?
//                }
//                CombinedSymbol s = ((CombinedSymbol) p.getRHS().get(i));
//                if (s.isTerminal()) {
//
//                    //con index troviamo il simbolo al quale si riferisce la relazione
//                    int index = ((AlphaRelation) s.getSymbol(0)).getIndex();
//                    int j = i;
//                    if (i - index >= 0) { //perchè dovrebbe succedere il contrario?
//                        //cs è il terminale trovato    
//                        CombinedSymbol cs = (CombinedSymbol) p.getRHS().get(i);
//                        //csprevious è il simbolo a cui si riferisce la relazione
//                        Symbol csprevious = p.getRHS().get(i - index);
//                        List<Action> listofactions = new ArrayList<>();
//
//                        //Se csprevious è un terminale, la sua relinside sarà semplicemente esso stesso
//                        if (csprevious instanceof CombinedSymbol && ((CombinedSymbol) csprevious).isTerminal()) {
//                            ArrayList<Terminal> calist = new ArrayList<>();
//                            calist.add((Terminal) ((CombinedSymbol) csprevious).getSymbol(1));
//                            listofactions = Action.createActions((CombinedSymbol) csprevious, cs, calist);
//                        } //Altrimenti lo prendiamo dalla mappa di relinside
//                        else if (csprevious instanceof CombinedSymbol && !((CombinedSymbol) csprevious).isTerminal()) {
//                            listofactions = Action.createActions((CombinedSymbol) csprevious, cs, relInsideResult.get(csprevious));
//                        }
//                        for (Action a : listofactions) {
//                            new_p.getRHS().set(j + diff, a);
//                            new_p.getRHS().add(j + diff + 1, cs.getSymbol(1));
//                            j++;
//                        }
//
//                        diff = diff + j - i;
//
//                    }
//
//                } else {
//                    int relindex = ((AlphaRelation) s.getSymbol(0)).getIndex();
//                    //punto 1.1
//                    if ((i - relindex < 0)) {
//                        continue;
//                    }
//                    if (((CombinedSymbol) p.getRHS().get(i - relindex)).isTerminal()) {
//
//                        int j = i;
//
//                        //ncs è il nonterminale trovato
//                        CombinedSymbol ncs = (CombinedSymbol) p.getRHS().get(i);
//                        //tcs è il terminale a cui si riferisce la relazione
//                        CombinedSymbol tcs = (CombinedSymbol) p.getRHS().get(i - relindex);
//
//                        List<Action> listofactions = new ArrayList<>();
//
//                        //Qui sappiamo di star trattando con un terminale e un non-terminale.
//                        listofactions = Action.createActions(relInsideResult.get(ncs), tcs, ncs);
//
//                        for (Action a : listofactions) {
//                            new_p.getRHS().set(j + diff, a);
//                            new_p.getRHS().add(j + diff + 1, ncs);
//                            j++;
//                        }
//
//                        diff = diff + j - i;
//
//                    } else {
//
//                        int j = i;
//
//                        //ncs è il nonterminale trovato
//                        CombinedSymbol ncs = (CombinedSymbol) p.getRHS().get(i);
//                        //tcs è il nonterminale a cui si riferisce la relazione
//                        CombinedSymbol tcs = (CombinedSymbol) p.getRHS().get(i - relindex);
//
//                        List<Action> listofactions = new ArrayList<>();
//
//                        listofactions = Action.createActions(ncs, tcs, relInsideResult.get(ncs), relInsideResult.get(tcs));
//
//                        for (Action a : listofactions) {
//                            new_p.getRHS().set(j + diff, a);
//                            new_p.getRHS().add(j + diff + 1, ncs);
//                            j++;
//                        }
//
//                        diff = diff + j - i;
//
//                    }
//
//                }
//
//            }
//
//            result.addProduction(new_p);
//
//        }
//
//        //Ora eliminiamo le relazioni dai terminali che sono all'inizio.
//        for (CFGProduction prod : (List<CFGProduction>) result.getProductions()) {
//
//            if (prod.getRHS().get(0) instanceof CombinedSymbol && ((CombinedSymbol) prod.getRHS().get(0)).isTerminal()) {
//
//                prod.getRHS().set(0, ((CombinedSymbol) prod.getRHS().get(0)).getSymbol(1));
//
//            }
//
//        }
//
//        //Fine punto 1
//        //Punto 2: inseriamo le relazioni al termine delle produzioni che hanno un terminale come ultimo simbolo.
//        for (CFGProduction prod : (List<CFGProduction>) result.getProductions()) {
//
//            //Se non c'è il pedice, lo applichiamo all'ultimo terminale.
//            if (prod.getAnchor().isEmpty() && prod.getRHS().get(prod.getRHS().size() - 1) instanceof Terminal) {
//
//                Terminal x = (Terminal) prod.getRHS().get(prod.getRHS().size() - 1);
//
//                //Inseriamo queste relazioni solo se si riferiscono a non-terminali legati nella rfollow.
//                //In caso di terminali, sono già stati inseriti dal punto 1.
//                for (CombinedSymbol cs : relfollow.get(prod.getLHSNonterminal())) {
//
//                    if (!cs.isTerminal()) {
//
//                        if (cs.getSymbol(1) != null) {
//
//                            prod.getRHS().addAll(Action.createAction(relinside.get(cs), x, cs));
//
//                        }
//
//                    }
//
//                }
//
//            } else {
//
//                //se c'è il pedice invece lo applichiamo a quei terminali.
//                for (Integer intero : prod.getAnchor()) {
//
//                    Symbol x = prod.getRHS().get(intero - 1);
//                    int j = 0;
//
//                    if (x instanceof Terminal) {
//
//                        for (CombinedSymbol cs : relFollowResult.get(prod.getLHSNonterminal())) {
//
//                            if (!cs.isTerminal()) {
//
//                                if (cs.getSymbol(1) != null) {
//
//                                    for (Action a : Action.createAction(relInsideResult.get(cs), (Terminal) x, cs)) {
//
//                                        prod.getRHS().add(intero + j, a);
//                                        j++;
//
//                                    }
//
//                                }
//                            }
//                        }
//
//                    } else if (x instanceof CombinedSymbol) {
//
//                        for (Terminal ter : relInsideResult.get((CombinedSymbol) x)) {
//
//                            for (CombinedSymbol cs : relFollowResult.get(prod.getLHSNonterminal())) {
//
//                                if (!cs.isTerminal()) {
//
//                                    if (cs.getSymbol(1) != null) {
//
//                                        prod.getRHS().addAll(Action.createAction(relInsideResult.get(cs), ter, cs));
//
//                                    }
//                                }
//                            }
//
//                        }
//
//                    }
//
//                }
//
//            }
//
//        }
//        //Fine punto 2
//        //Punto 3: inseriamo {()} alla fine dei terminali nelle produzioni che non puntano direttamente ad essi.
//
//        for (CFGProduction prod : (List<CFGProduction>) result.getProductions()) {
//
//            if (!(prod.getAnchor().isEmpty()) && !(prod.getAnchor().contains(prod.getRHS().size()))) {
//
//                Action action = new Action("{()}");
//                prod.getRHS().add(action);
//
//            }
//
//        }
//        //Fine punto 3
//
//        //Punto 4: Inseriamo le nuove produzioni S per ogni Sp_S.
//        Nonterminal S = AlphaNonterminal.create("StartSymbol");
//
//        for (Nonterminal start : g.getStartSymbols()) {
//
//            CFGProduction newprod = new CFGProduction(S, start);
//            result.addProduction(newprod);
//
//        }
//        //Fine punto 4
//
//        result.setStartSymbol(S);
//        return result;
//    }
}

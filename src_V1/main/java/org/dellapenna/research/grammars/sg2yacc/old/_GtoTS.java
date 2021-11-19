package org.dellapenna.research.grammars.sg2yacc.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.cf.CFGGrammar;
import org.dellapenna.research.grammars.cf.CFGProduction;
import org.dellapenna.research.grammars.symbols.Action;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Terminal;

/**
 *
 * @author Andrea
 */
public class _GtoTS {
    
      private static _GtoTS instance = null;

    public static _GtoTS getInstance() {
        if (instance == null) {
            instance = new _GtoTS();
        }
        return instance;
    }

//    public CFGGrammar apply(CFGGrammar g, HashMap<Nonterminal, List<Terminal>> relinside, HashMap<Nonterminal, List<CombinedSymbol>> relfollow) throws GrammarException {
//
//        CFGGrammar result = new CFGGrammar();
//
//        //Per ogni produzione
//        for (CFGProduction prod : (List<CFGProduction>) g.getProductions()) {
//
//            List<Symbol> newRHS = new ArrayList<>();
//            //Andremo a fare lo "scan" di prod e fare le eventuali modifiche in newprod. Quindi ci serve una nuova parte destra.
//            //Inoltre, newprod cambia dimensione, mentre prod rimane fissa. Per questo teniamo traccia della differenza negli iteratori
//            //tramite l'intero diff.
//            newRHS.addAll(prod.getRHS());
//            CFGProduction newprod = new CFGProduction(prod.getLHSNonterminal(), prod.getAnchor(), newRHS);
//            int diff = 0;
//
//            //Per ogni terminale in quella produzione
//            for (int i = 0; i < prod.getRHS().size(); i++) {
//
//                if (!(prod.getRHS().get(i) instanceof CombinedSymbol)) {
//                    continue;
//                }
//                CombinedSymbol s = ((CombinedSymbol) prod.getRHS().get(i));
//
//                if (s.isTerminal()) {
//
//                    //con index troviamo il simbolo al quale si riferisce la relazione
//                    int index = ((AlphaRelation) s.getSymbol(0)).getIndex();
//                    int j = i;
//                    if (i - index >= 0) {
//
//                        //cs è il terminale trovato    
//                        CombinedSymbol cs = (CombinedSymbol) prod.getRHS().get(i);
//                        //csprevious è il simbolo a cui si riferisce la relazione
//                        Symbol csprevious = prod.getRHS().get(i - index);
//                        List<Action> listofactions = new ArrayList<>();
//
//                        //Se csprevious è un terminale, la sua relinside sarà semplicemente esso stesso
//                        if (csprevious instanceof CombinedSymbol && ((CombinedSymbol) csprevious).isTerminal()) {
//                            ArrayList<Terminal> calist = new ArrayList<>();
//                            calist.add((Terminal) ((CombinedSymbol) csprevious).getSymbol(1));
//                            listofactions = Action.createActions((CombinedSymbol) csprevious, cs, calist);
//                        } //Altrimenti lo prendiamo dalla mappa di relinside
//                        else if (csprevious instanceof CombinedSymbol && !((CombinedSymbol) csprevious).isTerminal()) {
//                            listofactions = Action.createActions((CombinedSymbol) csprevious, cs, relinside.get(csprevious));
//                        }
//                        for (Action a : listofactions) {
//                            newprod.getRHS().set(j + diff, a);
//                            newprod.getRHS().add(j + diff + 1, cs.getSymbol(1));
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
//                    if (((CombinedSymbol) prod.getRHS().get(i - relindex)).isTerminal()) {
//
//                        int j = i;
//
//                        //ncs è il nonterminale trovato
//                        CombinedSymbol ncs = (CombinedSymbol) prod.getRHS().get(i);
//                        //tcs è il terminale a cui si riferisce la relazione
//                        CombinedSymbol tcs = (CombinedSymbol) prod.getRHS().get(i - relindex);
//
//                        List<Action> listofactions = new ArrayList<>();
//
//                        //Qui sappiamo di star trattando con un terminale e un non-terminale.
//                        listofactions = Action.createActions(relinside.get(ncs), tcs, ncs);
//
//                        for (Action a : listofactions) {
//                            newprod.getRHS().set(j + diff, a);
//                            newprod.getRHS().add(j + diff + 1, ncs);
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
//                        CombinedSymbol ncs = (CombinedSymbol) prod.getRHS().get(i);
//                        //tcs è il nonterminale a cui si riferisce la relazione
//                        CombinedSymbol tcs = (CombinedSymbol) prod.getRHS().get(i - relindex);
//
//                        List<Action> listofactions = new ArrayList<>();
//
//                        listofactions = Action.createActions(ncs, tcs, relinside.get(ncs), relinside.get(tcs));
//
//                        for (Action a : listofactions) {
//                            newprod.getRHS().set(j + diff, a);
//                            newprod.getRHS().add(j + diff + 1, ncs);
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
//            result.addProduction(newprod);
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
//                /*  Terminal x = (Terminal) prod.getRHS().get(prod.getRHS().size()-1); 
//               
//               //Inseriamo queste relazioni solo se si riferiscono a non-terminali legati nella rfollow.
//               //In caso di terminali, sono già stati inseriti dal punto 1.
//                for(CombinedSymbol cs : relfollow.get(prod.getLHSNonterminal())){
//                    
//                    if(!cs.isTerminal()){
//                        
//                        if(cs.getSymbol(1) != null){
//
//                          prod.getRHS().addAll(Action.createAction(relinside.get(cs), x, cs));
//
//                        }
//                    
//                    }
//                    
//                }
//                 */
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
//                        for (CombinedSymbol cs : relfollow.get(prod.getLHSNonterminal())) {
//
//                            if (!cs.isTerminal()) {
//
//                                if (cs.getSymbol(1) != null) {
//
//                                    for (Action a : Action.createAction(relinside.get(cs), (Terminal) x, cs)) {
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
//                        for (Terminal ter : relinside.get((CombinedSymbol) x)) {
//
//                            for (CombinedSymbol cs : relfollow.get(prod.getLHSNonterminal())) {
//
//                                if (!cs.isTerminal()) {
//
//                                    if (cs.getSymbol(1) != null) {
//
//                                        prod.getRHS().addAll(Action.createAction(relinside.get(cs), ter, cs));
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

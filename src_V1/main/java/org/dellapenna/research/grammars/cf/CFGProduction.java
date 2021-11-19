package org.dellapenna.research.grammars.cf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.AlphaSymbol;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.Production;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Symbols;
import org.json.JSONArray;
import org.json.JSONObject;

public class CFGProduction implements Production {

    private Nonterminal lhs;
    private List<Symbol> rhs;

    //In teoria le produzioni CF non contengono un'ancora. Tuttavia, sono necessarie nella nostra metodologia e vengono eliminate solamente
    //nell'ultimo step.  
    private List<Integer> anchor = new ArrayList<>();

    public CFGProduction(Nonterminal lhs, List<Symbol> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public CFGProduction(Nonterminal lhs, List<Integer> anchor, List<Symbol> rhs) throws GrammarException {
        this.lhs = lhs;
        //Controlla che il pedice non superi il numero di simboli.
//        for (int i : anchor) {
//            if (i > rhs.size() / 2 + 1) {
//                throw new GrammarException("Invalid SG production anchor " + i);
//            }
//        }
//        for (int i : anchor) {
//            if (i > rhs.size() || !((rhs.get(i - 1) instanceof Terminal) || (rhs.get(i - 1) instanceof Nonterminal))) {
//                throw new GrammarException("Invalid SG production anchor " + i);
//            }
//        }
        this.anchor = anchor;
        this.rhs = rhs;
    }

    public CFGProduction(Nonterminal lhs, Integer[] anchor, Symbol... rhs) throws GrammarException {
        this(lhs, Arrays.asList(anchor), Arrays.asList(rhs));
    }

    public CFGProduction(Nonterminal lhs, Symbol... rhs) {
        this.lhs = lhs;
        this.rhs = Arrays.asList(rhs);
    }

    @Override
    //restiuisce la lista di simboli al lato sinistro
    public List<Symbol> getLHS() {
        return Collections.singletonList(lhs);
    }

    //restituisce il nonterminale a sinistra
    public Nonterminal getLHSNonterminal() {
        return lhs;
    }

    @Override
    //restiuisce la lista di simboli al lato destro
    public List<Symbol> getRHS() {
        return rhs;
    }

    //restiuisce l'ancora della produzione (lista di interi)
    public List<Integer> getAnchor() {
        return anchor;
    }

    @Override
    public String toString() {
        //Se non ci sono ancore usiamo la versione classica.
        if (anchor.isEmpty()) {
            String RHSString = "";
            for (Symbol s : getRHS()) {
                RHSString += s + " ";
            }
            return getLHSNonterminal() + " -> " + RHSString;
        } //Altrimenti le stampiamo.
        else {
            String RHSString = "";
            for (Symbol s : getRHS()) {
                RHSString += s + " ";
            }
            return getLHSNonterminal() + " ->" + Arrays.toString(getAnchor().toArray()) + " " + RHSString;
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        JSONArray r = new JSONArray();
        for (Symbol s : getRHS()) {
            r.put(s.toJson());
        }
        o.put(getLHSNonterminal().toString(), r);
        return o;
    }

    public static CFGProduction fromJson(JSONObject o) throws GrammarException {
        if (o.length() == 1) {
            String lhs = o.names().getString(0);
            JSONArray rhsa = o.getJSONArray(lhs);
            List<Symbol> rhs = new ArrayList<>();
            for (int i = 0; i < rhsa.length(); ++i) {
                rhs.add(AlphaSymbol.fromJson(rhsa.get(i)));
            }
            return new CFGProduction((AlphaNonterminal) Symbols.create(lhs, Nonterminal.TYPENAME), rhs);
        }
        throw new GrammarException("Unable to read a CF production from " + o);
    }

    @Override
    //restituisce tutti i simboli di un certo tipo usati nella produzione
    public Symbol[] getSymbols(String type) {
        Set<Symbol> result = new HashSet();
        for (Symbol s : getLHS()) {
            if (s.getTypeName().equals(type)) {
                result.add(s);
            }
        }

        for (Symbol s : getRHS()) {
            if (s.getTypeName().equals(type)) {
                result.add(s);
            }
        }
        return result.toArray(new Symbol[0]);
    }

}

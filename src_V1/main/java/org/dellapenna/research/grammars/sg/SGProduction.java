package org.dellapenna.research.grammars.sg;

import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
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

public class SGProduction implements Production {

    private Nonterminal lhs;
    private List<Symbol> rhs;
    private List<Integer> anchor;

    public SGProduction(Nonterminal lhs, List<Integer> anchor, List<Symbol> rhs) throws GrammarException {
        this.lhs = lhs;

        //Controlla che non inizi con una relazione, o che sia benformata in generale.
        for (int i = 0; i < rhs.size(); i++) {
            if ((i % 2 == 0 && rhs.get(i) instanceof Relation) || (i % 2 == 1 && !(rhs.get(i) instanceof Relation))) {
                throw new GrammarException("Malformed SG production at index " + i);
            }
        }
        //Controlla che il pedice non superi il numero di simboli.
        for (int i : anchor) {
            if (i > rhs.size() / 2 + 1) {
                throw new GrammarException("Invalid SG production anchor " + i);
            }
        }
        this.anchor = anchor;
        this.rhs = rhs;
    }

    public SGProduction(Nonterminal lhs, Integer[] anchor, Symbol... rhs) throws GrammarException {
        this(lhs, Arrays.asList(anchor), Arrays.asList(rhs));
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
        if (anchor.isEmpty()) { //Se non ci sono ancore usiamo la versione classica
            String RHSString = "";
            for (Symbol s : getRHS()) {
                RHSString += s + " ";
            }
            return getLHSNonterminal() + " -> " + RHSString;

        } //Altrimenti le stampiamo
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

        JSONArray a = new JSONArray();
        r.put(a);
        for (int i : getAnchor()) {
            a.put(i);
        }

        for (Symbol s : getRHS()) {
            r.put(s.toJson());
        }
        o.put(getLHSNonterminal().toString(), r);
        return o;
    }

    public static SGProduction fromJson(JSONObject o) throws GrammarException {
        if (o.length() == 1) {
            String lhs = o.names().getString(0);
            JSONArray rhsa = o.getJSONArray(lhs);
            List<Symbol> rhs = new ArrayList<>();
            JSONArray anchora = rhsa.getJSONArray(0);
            List<Integer> anchor = new ArrayList<>();
            for (int i = 0; i < anchora.length(); ++i) {
                anchor.add(anchora.getInt(i));
            }
            for (int i = 1; i < rhsa.length(); ++i) {
                if (i % 2 == 0) {
                    rhs.add(AlphaRelation.fromJson(rhsa.getJSONObject(i)));
                } else {
                    rhs.add(AlphaSymbol.fromJson(rhsa.getString(i)));
                }
            }

            return new SGProduction((AlphaNonterminal) Symbols.create(lhs, Nonterminal.TYPENAME), anchor, rhs);
        }
        throw new GrammarException("Unable to read a SG production from " + o);
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

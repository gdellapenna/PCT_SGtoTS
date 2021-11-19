package org.dellapenna.research.grammars.sg;

import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.dellapenna.research.grammars.GenericGrammar;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Symbols;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.json.JSONArray;
import org.json.JSONObject;

public class SGGrammar extends GenericGrammar {

    public SGGrammar(Nonterminal startSymbol, List<SGProduction> productions) throws GrammarException {
        this(Arrays.asList(startSymbol), productions);
    }

    public SGGrammar(List<Nonterminal> startSymbols, List<SGProduction> productions) throws GrammarException {
        super(startSymbols, productions);
    }

    public SGGrammar() {
        super();
    }

    public void addProduction(Nonterminal lhs, List<Integer> anchor, Symbol... rhs) throws GrammarException {
        addProduction(new SGProduction(lhs, anchor, Arrays.asList(rhs)));
    }

    public void addProduction(Nonterminal lhs, Integer[] anchor, Symbol... rhs) throws GrammarException {
        addProduction(new SGProduction(lhs, anchor, rhs));
    }

    public void addProduction(Nonterminal lhs, Symbol... rhs) throws GrammarException {
        addProduction(new SGProduction(lhs, new Integer[0], rhs));
    }

    public static SGGrammar fromJson(JSONObject o) throws GrammarException {
        JSONArray r = o.getJSONArray("terminals");
        for (int i = 0; i < r.length(); ++i) { //register only
            Symbols.create(r.getString(i), Terminal.TYPENAME);
        }
        r = o.getJSONArray("nonterminals");
        for (int i = 0; i < r.length(); ++i) { //register only
            Symbols.create(r.getString(i), Nonterminal.TYPENAME);
        }
        if (o.has("relations")) {
            r = o.getJSONArray("relations");
            for (int i = 0; i < r.length(); ++i) { //register only
                String name = r.getJSONObject(i).names().getString(0);
                int index = r.getJSONObject(i).getInt(name);
                Symbols.create(new AlphaRelation.RelationSignature(name, index), Relation.TYPENAME);
            }
        }
        List<Nonterminal> starts = new ArrayList<>();
        r = o.getJSONArray("start");
        for (int i = 0; i < r.length(); ++i) {
            starts.add((Nonterminal) Symbols.get(r.getString(i)));
        }

        JSONArray productionsa = o.getJSONArray("productions");
        List<SGProduction> productions = new ArrayList<>();
        for (int i = 0; i < productionsa.length(); ++i) {
            productions.add(SGProduction.fromJson(productionsa.getJSONObject(i)));
        }
        return new SGGrammar(starts, productions);
    }

    public String getRandomSentenceFor(Nonterminal n) {
        Random r = new Random();
        //Partiamo dalla stringa vuota.
        String sentence = "";
        //prendiamo le produzioni di quel nonterminale
        List<SGProduction> prods = (List<SGProduction>) getProductionsByLHS(n);
        //Ne scegliamo una casuale.
        SGProduction sProd = prods.get(r.nextInt(prods.size()));

        //Per ogni simbolo presente nella parte destra della produzione scelta, 
        //se è un terminale, lo aggiungiamo alla sentenza.
        //Se è una relazione, lo aggiungiamo con degli spazi
        //Se è un nonterminale, applichiamo ricorsivamente il metodo.
        for (Symbol s : sProd.getRHS()) {
            if (s instanceof Terminal) {
                sentence += " " + s.toString();
            } else if (s instanceof Relation) {
                sentence += " " + s.toString() + " ";
            } else if (s instanceof CombinedSymbol) {
                CombinedSymbol cs = (CombinedSymbol) s;
                if (cs.isTerminal()) {
                    sentence += " " + cs.toString();
                } else {
                    sentence += getRandomSentenceFor((Nonterminal) s);
                }
            } else { //Nonterminal
                sentence += getRandomSentenceFor((Nonterminal) s);
            }
        }
        return "(" + sentence + ")_" + Arrays.toString(sProd.getAnchor().toArray());
    }

    public String getRandomSentence() throws GrammarException {
        return getRandomSentenceFor(getStartSymbol());
    }
}

package org.dellapenna.research.grammars.sg;

import org.dellapenna.research.grammars.symbols.Relation;
import org.dellapenna.research.grammars.symbols.AlphaRelation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.dellapenna.research.grammars.Grammar;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.Production;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Symbols;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.json.JSONArray;
import org.json.JSONObject;

public class SGGrammar implements Grammar {

    Map<Nonterminal, List<SGProduction>> productions;
    List<Nonterminal> startsymbols = new ArrayList<>();
    private static final Random r = new Random();

    public SGGrammar(Nonterminal startSymbol, List<SGProduction> productions) throws GrammarException {
        this(Arrays.asList(startSymbol), productions);
    }

    public SGGrammar(List<Nonterminal> startSymbols, List<SGProduction> productions) throws GrammarException {
        this.startsymbols = startSymbols;
        this.productions = new HashMap<>();
        for (SGProduction p : productions) {
            addProduction(p);
        }
    }

    public SGGrammar() {
        productions = new HashMap<>();
        startsymbols = new ArrayList<>();
    }

    public void setStartSymbol(Nonterminal s) throws GrammarException {
        startsymbols.clear();
        addStartSymbol(s);
    }

    public void addStartSymbol(Nonterminal s) throws GrammarException {
        startsymbols.add(s);
    }

    public void setStartSymbols(List<Nonterminal> list) throws GrammarException {
        startsymbols = list;
    }

    @Override
    public List<Nonterminal> getStartSymbols() {
        return this.startsymbols;
    }

    @Override
    public Nonterminal getStartSymbol() {
        return this.startsymbols.get(0);
    }

    public void addProduction(SGProduction p) throws GrammarException {

        //Prende la lista delle produzioni che hanno a sinistra lo stesso nonterminale di p.
        List<SGProduction> pl = this.productions.get(p.getLHSNonterminal());
        if (pl == null) {
            //Se non ce n'è nessuna, la creiamo e inseriamo nelle produzioni il simbolo
            //e la produzione.
            pl = new ArrayList<>();
            this.productions.put(p.getLHSNonterminal(), pl);
        }
        //Altrimenti ci basta aggiungere la produzione alla lista di quel nonterminale già esistente.
        pl.add(p);
    }

    //Altre addProduction nel caso di pedice j alla produzione o se i parametri sono passati in altro modo.
    public void addProduction(Nonterminal lhs, List<Integer> anchor, Symbol... rhs) throws GrammarException {
        addProduction(new SGProduction(lhs, anchor, Arrays.asList(rhs)));
    }

    public void addProduction(Nonterminal lhs, Integer[] anchor, Symbol... rhs) throws GrammarException {
        addProduction(new SGProduction(lhs, anchor, rhs));
    }

    public void addProduction(Nonterminal lhs, Symbol... rhs) throws GrammarException {
        addProduction(new SGProduction(lhs, new Integer[0], rhs));
    }

    @Override
    //restituisce la lista delle produzioni
    public List<? extends Production> getProductions() {
        List<SGProduction> flatten = new ArrayList();
        for (Collection<SGProduction> pl : productions.values()) {
            flatten.addAll(pl);
        }
        return flatten;
    }

    //restituisce le produzioni di un certo nonterminale
    public List<? extends SGProduction> getProductionsByNonterminal(Nonterminal n) {
        return productions.get(n);
    }

    @Override
    public String toString() {
        String productionsString = "";
        for (Nonterminal start : getStartSymbols()) {
            for (Production p : getProductionsByNonterminal(start)) {
                productionsString += p + "\n";
            }
        }
        productionsString += "\n";
        for (Production p : getProductions()) {
            SGProduction cfgp = (SGProduction) p;
            if (!getStartSymbols().contains(cfgp.getLHSNonterminal())) {
                productionsString += p + "\n";
            }
        }
        return productionsString;
    }

    @Override
    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        JSONArray r = new JSONArray();
        o.put("start", r);
        for (Symbol s : getStartSymbols()) {
            r.put(s.toJson());
        }
        r = new JSONArray();
        o.put("terminals", r);
        for (Symbol s : getSymbols(Terminal.TYPENAME)) {
            r.put(s.toJson());
        }
        r = new JSONArray();
        o.put("nonterminals", r);
        for (Symbol s : getSymbols(Nonterminal.TYPENAME)) {
            r.put(s.toJson());
        }
        r = new JSONArray();
        o.put("relations", r);
        for (Symbol s : getSymbols(Relation.TYPENAME)) {
            r.put(s.toJson());
        }

        r = new JSONArray();
        o.put("productions", r);
        for (Production p : getProductions()) {
            r.put(p.toJson());
        }
        return o;
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

    private String getRandomSentenceFor(Nonterminal n) {
        //Partiamo dalla stringa vuota.
        String sentence = "";
        //prendiamo le produzioni di quel nonterminale
        List<? extends SGProduction> prods = getProductionsByNonterminal(n);
        //Ne scegliamo una casuale.
        SGProduction sProd = prods.get(r.nextInt(prods.size()));

        //Per ogni simbolo presente nella parte destra della produzione scelta, 
        //se è un terminale, lo aggiungiamo alla sentenza.
        //Se è una relazione, lo aggiungiamo con degli spazi
        //Se è un nonterminale, applichiamo ricorsivamente il metodo.
        for (Symbol s : sProd.getRHS()) {
            if (s instanceof Terminal) {
                sentence += s.toString();
            } else if (s instanceof Relation) {
                sentence += " " + s.toString() + " ";
            } else {
                sentence += getRandomSentenceFor((Nonterminal) s);
            }
        }
        return "(" + sentence + ")_" + Arrays.toString(sProd.getAnchor().toArray());
    }

    public String getRandomSentence() {
        return getRandomSentenceFor(getStartSymbol());
    }

    @Override
    //restituisce tutti i simboli di un certo tipo usati nella grammatica
    public Symbol[] getSymbols(String type) {
        Set<Symbol> result = new HashSet();
        for (Production p : getProductions()) {
            SGProduction sgp = (SGProduction) p;
            result.addAll(Arrays.asList(p.getSymbols(type)));
        }
        return result.toArray(new Symbol[0]);
    }

}

package org.dellapenna.research.grammars.cf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.dellapenna.research.grammars.Grammar;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.Production;
import org.dellapenna.research.grammars.symbols.Action;
import org.dellapenna.research.grammars.symbols.CombinedSymbol;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Symbols;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.json.JSONArray;
import org.json.JSONObject;

public class CFGGrammar implements Grammar {

    Map<Nonterminal, List<CFGProduction>> productions;
    private static final Random r = new Random();
    List<Nonterminal> startsymbols;

    public CFGGrammar(Nonterminal startSymbol, List<CFGProduction> productions) throws GrammarException {
        this(Arrays.asList(startSymbol), productions);
    }

    public CFGGrammar(List<Nonterminal> startSymbols, List<CFGProduction> productions) throws GrammarException {
        this.startsymbols = startSymbols;
        this.productions = new HashMap<>();
        for (CFGProduction p : productions) {
            addProduction(p);
        }
    }

    public CFGGrammar() {
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

    public void addProduction(CFGProduction p) throws GrammarException {

        List<CFGProduction> pl = this.productions.get(p.getLHSNonterminal());
        if (pl == null) {
            pl = new ArrayList<>();
            this.productions.put(p.getLHSNonterminal(), pl);
        }
        pl.add(p);
    }

    public void deleteProduction(CFGProduction p) throws GrammarException {
        List<CFGProduction> pl = this.productions.get(p.getLHSNonterminal());

        for (Iterator<CFGProduction> iterator = pl.iterator(); iterator.hasNext();) {
            CFGProduction prod = iterator.next();
            if (pl.get(0).equals(p)) {
                iterator.remove();
            }
        }

    }

    public void addProduction(Nonterminal lhs, Symbol... rhs) throws GrammarException {
        addProduction(new CFGProduction(lhs, rhs));
    }

    @Override
    //restituisce la lista delle produzioni
    public List<? extends Production> getProductions() {
        List<CFGProduction> flatten = new ArrayList();
        for (Collection<CFGProduction> pl : productions.values()) {
            flatten.addAll(pl);
        }
        return flatten;
    }

    //restituisce le produzioni di un certo nonterminale
    public List<? extends CFGProduction> getProductionsByNonterminal(Nonterminal n) {
        return productions.get(n);
    }

    @Override
    public String toString() {
        String productionsString = "";
        for (Nonterminal start : getStartSymbols()) {
            List<? extends CFGProduction> prods = getProductionsByNonterminal(start);
            if (prods != null) {
                for (Production p : prods) {
                    productionsString += p + "\n";
                }
            }
        }
        productionsString += "\n";
        for (Production p : getProductions()) {
            CFGProduction cfgp = (CFGProduction) p;
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
        o.put("productions", r);
        for (Production p : getProductions()) {
            r.put(p.toJson());
        }
        return o;
    }

    public static CFGGrammar fromJson(JSONObject o) throws GrammarException {

        JSONArray r = o.getJSONArray("terminals");
        for (int i = 0; i < r.length(); ++i) { //register only
            Symbols.create(r.getString(i), Terminal.TYPENAME);
        }
        r = o.getJSONArray("nonterminals");
        for (int i = 0; i < r.length(); ++i) { //register only
            Symbols.create(r.getString(i), Nonterminal.TYPENAME);
        }

        List<Nonterminal> starts = new ArrayList<>();
        r = o.getJSONArray("start");
        for (int i = 0; i < r.length(); ++i) {
            starts.add((Nonterminal) Symbols.get(r.getString(i)));
        }

        JSONArray productionsa = o.getJSONArray("productions");
        List<CFGProduction> productions = new ArrayList<>();
        for (int i = 0; i < productionsa.length(); ++i) {
            productions.add(CFGProduction.fromJson(productionsa.getJSONObject(i)));
        }
        return new CFGGrammar(starts, productions);
    }

    public String getRandomSentenceFor(Nonterminal n) {
        String sentence = "";
        List<? extends CFGProduction> prods = getProductionsByNonterminal(n);
        CFGProduction sProd = prods.get(r.nextInt(prods.size()));

        //I controlli su CombinedSymbol sono stati aggiunti allo scopo di generare sentenze anche
        //Con la grammatica intermedia composta da CombinedSymbols. In questo modo riusciamo a distinguere
        //se un CS è terminale o meno.
        for (Symbol s : sProd.getRHS()) {
            if (s instanceof Terminal || s instanceof Action) {
                sentence = sentence + " " + s.toString();
            } else {
                if (s instanceof CombinedSymbol) {
                    CombinedSymbol cs = (CombinedSymbol) s;
                    if (cs.isTerminal()) {

                        sentence += cs.toString();
                    } else {

                        sentence += getRandomSentenceFor((Nonterminal) s);
                    }
                }
            }
        }
        return sentence;
    }

    public String getRandomSentence() {
        return getRandomSentenceFor(getStartSymbol());
    }

    @Override
    //restituisce tutti i simboli di un certo tipo usati nella grammatica
    public Symbol[] getSymbols(String type) {
        Set<Symbol> result = new HashSet();
        for (Production p : getProductions()) {
            CFGProduction cfgp = (CFGProduction) p;
            result.addAll(Arrays.asList(p.getSymbols(type)));
        }
        return result.toArray(new Symbol[0]);
    }

}

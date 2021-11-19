package org.dellapenna.research.grammars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Terminal;
import org.json.JSONArray;
import org.json.JSONObject;

public class GenericGrammar implements Grammar {

    Map<Nonterminal, List<Production>> productions;
    List<Nonterminal> startsymbols;

    public GenericGrammar(Nonterminal startSymbol, List<? extends Production> productions) throws GrammarException {
        this(Arrays.asList(startSymbol), productions);
    }

    public GenericGrammar(List<Nonterminal> startSymbols, List<? extends Production> productions) throws GrammarException {
        this();
        this.startsymbols = startSymbols;
        for (Production p : productions) {
            addProduction(p);
        }
    }

    public GenericGrammar() {
        this.startsymbols = new ArrayList<>();
        this.productions = new HashMap<>();
    }

    @Override
    public void setStartSymbol(Nonterminal s) {
        setStartSymbols(Collections.singletonList(s));
    }

    @Override
    public void setStartSymbols(List<Nonterminal> s) {
        startsymbols = s;
    }

    @Override
    public void addStartSymbol(Nonterminal s) {
        startsymbols.add(s);
    }

    @Override
    public List<Nonterminal> getStartSymbols() {
        return this.startsymbols;
    }

    @Override
    public Nonterminal getStartSymbol() throws GrammarException {
        if (this.startsymbols.size() == 1) {
            return this.startsymbols.get(0);
        } else {
            throw new GrammarException("The grammar has more than one start symbol");
        }
    }

    @Override
    public final void addProduction(Production p) throws GrammarException {
        List<Production> pl = this.productions.get(p.getLHSNonterminal());
        if (pl == null) {
            pl = new ArrayList<>();
            this.productions.put(p.getLHSNonterminal(), pl);
        }
        pl.add(p);

    }

    public final void deleteProduction(Production p) throws GrammarException {

        List<Production> pl = this.productions.get(((Production) p).getLHSNonterminal());
        for (Iterator<Production> iterator = pl.iterator(); iterator.hasNext();) {
            Production prod = iterator.next();
            if (prod.equals(p)) {
                iterator.remove();
            }
        }
    }

    @Override
    public List<? extends Production> getProductions() {
        List<Production> flatten = new ArrayList();
        for (Collection<Production> pl : productions.values()) {
            flatten.addAll(pl);
        }
        return flatten;
    }

    //restituisce le produzioni di un certo nonterminale
    @Override
    public List<? extends Production> getProductionsByLHS(Nonterminal n) {
        return productions.get(n);
    }

    //restituisce tutti i simboli di un certo tipo usati nella grammatica
    @Override
    public Symbol[] getSymbols(String type) {
        Set<Symbol> result = new HashSet();
        for (Production p : getProductions()) {
            result.addAll(Arrays.asList(p.getSymbols(type)));
        }
        return result.toArray(new Symbol[0]);
    }

    @Override
    public String toString() {
        String productionsString = "";
        for (Nonterminal start : getStartSymbols()) {
            List<? extends Production> prods = getProductionsByLHS(start);
            if (prods != null) {
                for (Production p : prods) {
                    productionsString += p + "\n";
                }
            }
        }
        productionsString += "\n";
        List<? extends Production> prods = getProductions();
        prods.sort((p1, p2) -> {
            return p1.getLHSNonterminal().toString().compareTo(p2.getLHSNonterminal().toString());
        });
        for (Production p : prods) {
            if (!getStartSymbols().contains(p.getLHSNonterminal())) {
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

}

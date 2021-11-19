package org.dellapenna.research.grammars.cf;

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

public class CFGrammar extends GenericGrammar {

    public CFGrammar(Nonterminal startSymbol, List<CFProduction> productions) throws GrammarException {
        this(Arrays.asList(startSymbol), productions);
    }

    public CFGrammar(List<Nonterminal> startSymbols, List<CFProduction> productions) throws GrammarException {
        super(startSymbols, productions);
    }

    public CFGrammar() {
        super();
    }

    public void addProduction(Nonterminal lhs, Symbol... rhs) throws GrammarException {
        addProduction(new CFProduction(lhs, rhs));
    }

    public static CFGrammar fromJson(JSONObject o) throws GrammarException {

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
        List<CFProduction> productions = new ArrayList<>();
        for (int i = 0; i < productionsa.length(); ++i) {
            productions.add(CFProduction.fromJson(productionsa.getJSONObject(i)));
        }
        return new CFGrammar(starts, productions);
    }

    public String getRandomSentenceFor(Nonterminal n) {
        Random r = new Random();
        String sentence = "";
        List<CFProduction> prods = (List<CFProduction>) getProductionsByLHS(n);
        CFProduction sProd = prods.get(r.nextInt(prods.size()));

        //I controlli su CombinedSymbol sono stati aggiunti allo scopo di generare sentenze anche
        //Con la grammatica intermedia composta da CombinedSymbols. In questo modo riusciamo a distinguere
        //se un CS è terminale o meno.
        for (Symbol s : sProd.getRHS()) {
            if (s instanceof Terminal) {
                sentence += " " + s.toString();
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
        return sentence;
    }

    public String getRandomSentence() throws GrammarException {
        return getRandomSentenceFor(getStartSymbol());
    }

}

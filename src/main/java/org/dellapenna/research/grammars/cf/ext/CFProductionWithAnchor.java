package org.dellapenna.research.grammars.cf.ext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dellapenna.research.grammars.ext.GenericProductionWithAnchor;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.AlphaSymbol;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Symbols;
import org.json.JSONArray;
import org.json.JSONObject;

public class CFProductionWithAnchor extends GenericProductionWithAnchor {

    public CFProductionWithAnchor(Nonterminal lhs, List<Symbol> rhs) {
        super(lhs, rhs);
    }

    public CFProductionWithAnchor(Nonterminal lhs, List<Integer> anchor, List<Symbol> rhs) throws GrammarException {
        super(lhs,anchor, rhs);
    }

    public CFProductionWithAnchor(Nonterminal lhs, Integer[] anchor, Symbol... rhs) throws GrammarException {
        this(lhs, Arrays.asList(anchor), Arrays.asList(rhs));
    }

    public CFProductionWithAnchor(Nonterminal lhs, Symbol... rhs) {
        this(lhs, Arrays.asList(rhs));
    }

    @Override
    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        JSONArray r = new JSONArray();
        JSONArray a = new JSONArray(getAnchor());
        r.put(a);
        for (Symbol s : getRHS()) {
            r.put(s.toJson());
        }
        o.put(getLHSNonterminal().toString(), r);
        return o;
    }

    public static CFProductionWithAnchor fromJson(JSONObject o) throws GrammarException {
        if (o.length() == 1) {
            String lhs = o.names().getString(0);
            JSONArray rhsa = o.getJSONArray(lhs);

            List<Integer> a = new ArrayList<>();
            JSONArray aa = rhsa.getJSONArray(0);
            for (int i = 0; i < aa.length(); ++i) {
                a.add(aa.getInt(i));
            }

            List<Symbol> rhs = new ArrayList<>();
            for (int i = 1; i < rhsa.length(); ++i) {
                rhs.add(AlphaSymbol.fromJson(rhsa.get(i)));
            }
            return new CFProductionWithAnchor((AlphaNonterminal) Symbols.create(lhs, Nonterminal.TYPENAME), a, rhs);
        }
        throw new GrammarException("Unable to read a CF production from " + o);
    }
}

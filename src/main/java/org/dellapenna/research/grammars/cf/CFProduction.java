package org.dellapenna.research.grammars.cf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dellapenna.research.grammars.GenericProduction;
import org.dellapenna.research.grammars.symbols.AlphaNonterminal;
import org.dellapenna.research.grammars.symbols.AlphaSymbol;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.dellapenna.research.grammars.symbols.Symbols;
import org.json.JSONArray;
import org.json.JSONObject;

public class CFProduction extends GenericProduction {

    public CFProduction(Nonterminal lhs, List<Symbol> rhs) {
        super(lhs, rhs);
    }

    public CFProduction(Nonterminal lhs, Symbol... rhs) {
        this(lhs, Arrays.asList(rhs));
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
    
 
    public static CFProduction fromJson(JSONObject o) throws GrammarException {
        if (o.length() == 1) {
            String lhs = o.names().getString(0);
            JSONArray rhsa = o.getJSONArray(lhs);
            List<Symbol> rhs = new ArrayList<>();
            for (int i = 0; i < rhsa.length(); ++i) {
                rhs.add(AlphaSymbol.fromJson(rhsa.get(i)));
            }
            return new CFProduction((AlphaNonterminal) Symbols.create(lhs, Nonterminal.TYPENAME), rhs);
        }
        throw new GrammarException("Unable to read a CF production from " + o);
    }
}

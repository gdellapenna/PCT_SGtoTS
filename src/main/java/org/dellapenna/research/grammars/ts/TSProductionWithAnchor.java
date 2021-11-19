package org.dellapenna.research.grammars.ts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dellapenna.research.grammars.GrammarException;
import org.dellapenna.research.grammars.symbols.Nonterminal;
import org.dellapenna.research.grammars.cf.ext.CFProductionWithAnchor;
import org.dellapenna.research.grammars.symbols.Symbol;
import org.json.JSONObject;

public class TSProductionWithAnchor extends CFProductionWithAnchor {

    private final Map<Integer, List<Action>> actions;

    public TSProductionWithAnchor(Nonterminal lhs, List<Symbol> rhs) {
        super(lhs, rhs);
        actions = new HashMap<>();

    }

    public TSProductionWithAnchor(Nonterminal lhs, List<Integer> anchor, List<Symbol> rhs) throws GrammarException {
        super(lhs, anchor, rhs);
        actions = new HashMap<>();
    }

    public List<Action> getActionsAt(int position) {
        return actions.get(position);
    }

    public Map<Integer, List<Action>> getActions() {
        return actions;
    }

    public void addActionAt(int position, Action action) {
        if (!actions.containsKey(position)) {
            actions.put(position, new ArrayList<>());
        }
        actions.get(position).add(action);
    }

    public void addActions(Map<Integer, List<Action>> actions) {
        for (Map.Entry<Integer, List<Action>> e : actions.entrySet()) {
            for (Action a : e.getValue()) {
                addActionAt(e.getKey(), a);
            }
        }
    }

    @Override
    public String toString() {
        String result = getLHSNonterminal() + " -> ";
        if (!getAnchor().isEmpty()) {
            result += Arrays.toString(getAnchor().toArray());
        }
        for (int i = 0; i < getRHS().size(); ++i) {
            result += " " + getRHS().get(i);
            if (actions.containsKey(i)) {
                for (Action a : actions.get(i)) {
                    result += " " + a;
                }
            }
        }
        return result;
    }

    @Override
    public JSONObject toJson() {
        throw new UnsupportedOperationException("Serialization for augmented productions not implemented.");
    }

    public static TSProductionWithAnchor fromJson(JSONObject o) throws GrammarException {
        throw new UnsupportedOperationException("Serialization for augmented productions not implemented.");
    }
   

}

package org.dellapenna.research.grammars.symbols;

import java.util.HashMap;
import java.util.Map;
import org.dellapenna.research.grammars.GrammarException;

/**
 *
 * @author Giuseppe Della Penna
 */
public class Symbols {

    private static final Map<String, Map<Object, Symbol>> registry = new HashMap<>();

    //get with specified type or create
    public static Symbol create(Object signature, String typename) throws GrammarException {
        //Se il registry non contiene già quel typename, lo inseriamo. 
        if (!registry.containsKey(typename)) {
            registry.put(typename, new HashMap<>());
        }
        
        //Prendiamo la mappa legata a quel typename.
        Map<Object, Symbol> typeregistry = registry.get(typename);
        //Se non c'è già quella firma nella mappa...
        if (!typeregistry.containsKey(signature)) {
            //should be added
            //try to find it elsewhere
            for (Map.Entry<String, Map<Object, Symbol>> r : registry.entrySet()) {
                if (r.getValue().containsKey(signature)) {
                    throw new GrammarException("Symbol " + signature.toString() + " already declared with type " + r.getKey());
                }
            }
            Symbol symbol = null;
            //Nel caso di terminali e nonterminali, la firma è il loro nome.
            switch (typename) {
                case Nonterminal.TYPENAME:
                    symbol = new AlphaNonterminal((String) signature);
                    break;
                case Terminal.TYPENAME:
                    symbol = new AlphaTerminal((String) signature);
                    break;
                case Relation.TYPENAME:
                    symbol = new AlphaRelation((AlphaRelation.RelationSignature) signature);
                    break;
                case CombinedSymbol.TYPENAME:
                    symbol = new CombinedSymbol((Symbol[]) signature);
                    break;
                default:
                    throw new GrammarException("Unable to create a symbol of type " + typename);
            }
            typeregistry.put(signature, symbol);
        }
        return typeregistry.get(signature);
    }

    //get if exists (with its type)
    public static Symbol get(Object name) throws GrammarException {
        for (Map.Entry<String, Map<Object, Symbol>> r : registry.entrySet()) {
            if (r.getValue().containsKey(name)) {
                return r.getValue().get(name);
            }
        }
        throw new GrammarException("Unable to find symbol " + name);
    }

    public static Symbol[] getAll(String typename) {
        if (!registry.containsKey(typename)) {
            return new AlphaSymbol[0];
        }
        return registry.get(typename).values().toArray(new Symbol[0]);
    }

    public static Symbol fromJson(Object o) throws GrammarException {
        if (o instanceof String) {
            return get((String) o);
        } else {
            throw new GrammarException("Unable to create this kind of symbol ");
        }
    }
}

package org.dellapenna.research.grammars.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.dellapenna.research.grammars.GrammarException;
import org.json.JSONArray;

public class CombinedSymbol implements Nonterminal {

    public static final String TYPENAME = "C";

    private List<Symbol> symbols;
    private Boolean terminal = false;

    //Possono essere passati zero o più simboli (numero arbitrario di parametri)
    //Questo costruttore prende i simboli input e ne fa una lista.
    //Poi chiama il costruttore che prende in input la lista.
    public CombinedSymbol(Symbol... symbols) {
        this(Arrays.asList(symbols));
    }

    protected CombinedSymbol(List<Symbol> symbols) {
        this.symbols = symbols;
        this.terminal = false;
    }

    //ritorna il numero di sotto-simboli che compongono questo simbolo
    public int getNSymbols() {
        return symbols.size();
    }

    //restituisce l'i-esimo sotto simbolo di questo simbolo
    public Symbol getSymbol(int i) {
        return symbols.get(i);
    }

    //ritorna i tipi di tutti i sotto-simboli.
    @Override
    public String getTypeName() {
        String result = "";
        for (int i = 0; i < symbols.size(); ++i) {
            Symbol s = symbols.get(i);
            if (s != null) {
                result += s.getTypeName() + (i < symbols.size() - 1 ? "-" : "");
            }

        }
        return result;
    }

    @Override
    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(Boolean terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < symbols.size(); ++i) {
            Symbol s = symbols.get(i);
            if (s != null) {
                result += s.toString() + (i < symbols.size() - 1 ? "_" : "");
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.symbols);
        return hash;
    }

    //Per l'equals confrontiamo le liste.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CombinedSymbol other = (CombinedSymbol) obj;
        if (!Objects.equals(this.symbols, other.symbols)) {
            return false;
        }
        return true;
    }

    @Override
    public JSONArray toJson() {
        JSONArray o = new JSONArray();
        for (Symbol s : symbols) {
            o.put(s.toJson());
        }
        return o;
    }

    public static CombinedSymbol fromJson(JSONArray o) throws GrammarException {
        List<Symbol> symbols = new ArrayList<>();
        for (int i = 0; i < o.length(); ++i) {
            symbols.add(Symbols.fromJson(o.get(i)));
        }
        return new CombinedSymbol(symbols);
    }

    public static CombinedSymbol create(Symbol... symbols) throws GrammarException {
        return (CombinedSymbol) Symbols.create(symbols, TYPENAME);
    }

}

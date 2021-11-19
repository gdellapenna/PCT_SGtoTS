package org.dellapenna.research.grammars.symbols;

import java.util.Objects;
import org.dellapenna.research.grammars.GrammarException;

public abstract class AlphaSymbol implements Symbol {

    private String name;
    private String typename;

    protected AlphaSymbol(String name, String typename) {
        this.name = name;
        this.typename = typename;
    }

    //Il metodo setTypeName non è presente perché impostato nelle interfacce dei vari tipi di simbolo.
    @Override
    public String getTypeName() {
        return typename;
    }

    //restituisce il nome del simbolo
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    
    //Il metodo hashCode genera una rappresentazione numerica dei contenuti di un oggetto
    //allo scopo di fornire un ulteriore meccanismo per identificarlo.
    //In effetti questo metodo restituisce int.
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        //se è lo stesso oggetto
        if (this == obj) {
            return true;
        }
        //Se l'oggetto passato è null
        if (obj == null) {
            return false;
        }
        //Se l'oggetto passato è di un altra classe
        if (getClass() != obj.getClass()) {
            return false;
        }
        //Se il nome dell'oggetto passato è diverso dal nostro
        final AlphaSymbol other = (AlphaSymbol) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        
        return true;
    }

    @Override
    public Object toJson() {
        return getName();
    }

    //Se l'oggetto o è una stringa json, la prendiamo e ne facciamo il cast
    //a AlphaSymbol.
    public static AlphaSymbol fromJson(Object o) throws GrammarException {
        if (o instanceof String) {
            return (AlphaSymbol) Symbols.get((String) o);
        } else {
            return null; //per ora
        }
    }
}

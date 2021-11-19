package org.dellapenna.research.grammars.symbols;

/**
 *
 * @author Giuseppe Della Penna
 */
public interface Symbol {

    @Override
    public String toString();

    public Object toJson();

    public String getTypeName();
    
    public boolean isTerminal();

}

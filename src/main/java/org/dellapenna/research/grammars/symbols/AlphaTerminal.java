package org.dellapenna.research.grammars.symbols;

import org.dellapenna.research.grammars.GrammarException;

public class AlphaTerminal extends AlphaSymbol implements Terminal {

    //Il costruttore chiede solo il nome del parametro perché il TypeName è
    //Staticamente definito dall'interfaccia Terminal che viene implementata.
    protected AlphaTerminal(String name) {
        super(name, TYPENAME);
    }

    
    public static AlphaTerminal create(String name) throws GrammarException {
        return (AlphaTerminal) Symbols.create(name, TYPENAME);
    }

    @Override
    public boolean isTerminal() {
        return true;
    }
    
    

}

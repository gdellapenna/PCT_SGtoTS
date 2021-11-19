package org.dellapenna.research.grammars.symbols;

import org.dellapenna.research.grammars.GrammarException;

public class AlphaNonterminal extends AlphaSymbol implements Nonterminal {

    //Il costruttore chiede solo il nome del parametro perché il TypeName è
    //Staticamente definito dall'interfaccia Nonterminal che viene implementata.
    protected AlphaNonterminal(String name) {
        super(name, TYPENAME);
    }

    public static AlphaNonterminal create(String name) throws GrammarException {
        return (AlphaNonterminal) Symbols.create(name, TYPENAME);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

}

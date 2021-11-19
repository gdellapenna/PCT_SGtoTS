package org.dellapenna.research.grammars.symbols;

import java.util.Objects;
import org.dellapenna.research.grammars.GrammarException;
import static org.dellapenna.research.grammars.symbols.Relation.TYPENAME;
import org.json.JSONObject;

public class AlphaRelation implements Relation {

    //RelationSignature contiene nome, index e i metodi adatti a gestirli (hash e equals).
    public static class RelationSignature {

        private final String name;
        private final int index;

        public RelationSignature(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.name);
            hash = 37 * hash + this.index;
            return hash;
        }

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
            final RelationSignature other = (RelationSignature) obj;
            if (this.index != other.index) {
                return false;
            }
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return true;
        }

    }

    //RelationSignature è l'unico attributo di AlphaRelation. Di conseguenza, il
    //costruttore accetta name e index, costruisce la firma, e ne chiama un
    //altro che prende in input la firma.
    private RelationSignature signature;

    protected AlphaRelation(String name, int index) {
        this(new RelationSignature(name, index));
    }

    protected AlphaRelation(RelationSignature s) {
        this.signature = s;
    }

    //restituisce l'apice della relazione
    public int getIndex() {
        return signature.getIndex();
    }

    //restituisce il nome della relazione
    public String getName() {
        return signature.getName();
    }

    public RelationSignature getSignature() {
        return signature;
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    @Override
    public String toString() {
        if (getIndex() <= 1) {
            return getName();
        } else {
        return getName() + "^" + getIndex();
    }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.signature);
        return hash;
    }

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
        final AlphaRelation other = (AlphaRelation) obj;
        if (!Objects.equals(this.signature, other.signature)) {
            return false;
        }
        return true;
    }

    @Override
    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put(getName(), getIndex());
        return o;
    }

    public static AlphaRelation fromJson(JSONObject o) throws GrammarException {
        if (o.length() == 1) {
            String name = o.names().getString(0);
            int index = o.getInt(name);
            try {
                return (AlphaRelation) Symbols.create(new RelationSignature(name, index), TYPENAME);
            } catch (GrammarException x) {
                throw new GrammarException("Unable to read a relation from " + o, x);
            }
        }
        throw new GrammarException("Unable to read a relation from " + o);
    }

    public static AlphaRelation create(String name, int index) throws GrammarException {
        return (AlphaRelation) Symbols.create(new RelationSignature(name, index), TYPENAME);
    }

    //Se nessun indice viene specificato, passiamo 1 (il default)
    public static AlphaRelation create(String name) throws GrammarException {
        return (AlphaRelation) Symbols.create(new RelationSignature(name, 1), TYPENAME);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

}

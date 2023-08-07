package wqfm.dsGT;

import java.util.Set;

public class DummyTaxa implements IDummyTaxa {
    
    Set<Integer> taxas;
    int index;

    public DummyTaxa(Set<Integer> t, int index){
        taxas = t;
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public boolean isTaxaIncluded(int taxa) {
        return taxas.contains(taxa);
    }
    @Override
    public boolean equals(Object me) {
        DummyTaxa tx = (DummyTaxa)me;
        return index == tx.index;
    }   

    @Override
    public int hashCode() {
        return index;
    }
}

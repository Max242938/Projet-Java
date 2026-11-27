package be.umons.projetjava.visitor;
public interface Element {
    void accept(DeviceVisitor v);
}

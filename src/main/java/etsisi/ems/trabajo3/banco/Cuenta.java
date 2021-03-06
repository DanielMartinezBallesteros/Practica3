package etsisi.ems.trabajo3.banco;

import java.util.Vector;

public class Cuenta {
    private Vector<Movimiento> mMovimientos;

    public Cuenta() {
        mMovimientos = new Vector<Movimiento>();
    }

    public void ingresar(double x) throws Exception {
        if (x <= 0)
            throw new Exception("No se puede ingresar una cantidad negativa");
        
        this.mMovimientos.addElement(Movimiento.realizarMovimiento("Ingreso en efectivo", x));
    }

    public void retirar(double x) throws Exception {
        if (x <= 0)
            throw new Exception("No se puede retirar una cantidad negativa");
        
        if (getSaldo() < x)
            throw new Exception("Saldo insuficiente");
        
        this.mMovimientos.addElement(Movimiento.realizarMovimiento("Retirada de efectivo", -x));
    }

    public void ingresar(String concepto, double x) throws Exception {
        if (x <= 0)
            throw new Exception("No se puede ingresar una cantidad negativa");

        this.mMovimientos.addElement(Movimiento.realizarMovimiento(concepto, x));
    }

    public void retirar(String concepto, double x) throws Exception {
        if (x <= 0)
            throw new Exception("No se puede retirar una cantidad negativa");
        if (getSaldo() < x)
            throw new Exception("Saldo insuficiente");
        
        this.mMovimientos.addElement(Movimiento.realizarMovimiento(concepto, -x));
    }

    public double getSaldo() {
        double r = 0.0;
        for (int i = 0; i < this.mMovimientos.size(); i++) {
            Movimiento m = (Movimiento) mMovimientos.elementAt(i);
            r += m.getImporte();			
        }
        return r;
    }

    public void addMovimiento(Movimiento m) {
        mMovimientos.addElement(m);
    }
    
    /**
     * Búsca los movimientos de un determinado mes/año
     * 
     * @param mes mes del que sea desea buscar los movimientos
     * @param anyo año del que se desea buscar los movimientos
     * @return Devuelve un Vector con los movimientos realizados en el mes/año especificado
     */
    public Vector<Movimiento> buscarMovimiento(int mes, int anyo){
        Vector<Movimiento> movimientos = new Vector();
        
        for(Movimiento m:mMovimientos){
            if (m.getFecha().getMonthValue() == mes && m.getFecha().getYear() == anyo){
                movimientos.add(m);
            }
        }
        
        return movimientos;
    }
}
package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Movimiento {
    private String mConcepto;
    private LocalDate mFecha;
    private double mImporte;
    private boolean mLiquidado;

    public Movimiento() {		
        mLiquidado = false; //lo necesito para los movimientos de las tarjetas de crédito
    }

    public double getImporte() {
        return mImporte;
    }

    public String getConcepto() {
        return mConcepto;
    }

    public void setConcepto(String newMConcepto) {
        mConcepto = newMConcepto;
    }

    public LocalDate getFecha() {
        return mFecha;
    }

    public void setFecha(LocalDate newMFecha) {
        mFecha = newMFecha;
    }

    public void setImporte(double newMImporte) {
        mImporte = newMImporte;
    }

    public boolean isLiquidado() {
        return mLiquidado;
    }

    public void setLiquidado(boolean mliquidado) {
        this.mLiquidado = mliquidado;
    }
    
    public static Movimiento realizarMovimiento(String concepto, double importe){
        Movimiento m = new Movimiento();
        
        m.setConcepto(concepto);
        m.setImporte(importe);
        m.setFecha(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        
        return m;
    }
}
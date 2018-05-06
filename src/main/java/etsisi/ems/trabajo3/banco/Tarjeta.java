package etsisi.ems.trabajo3.banco;

public abstract class Tarjeta {
    protected Cuenta mCuentaAsociada;
    
    public void setCuenta(Cuenta c){
        mCuentaAsociada = c;
    }

    public void retirar(double x) throws Exception{
    }

    public void ingresar(double x) throws Exception{
    }

    public void pagoEnEstablecimiento(String datos, double x) throws Exception{
    }

    public double getSaldo(){
        return 0;
    }
}

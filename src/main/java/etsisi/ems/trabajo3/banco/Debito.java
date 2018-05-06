package etsisi.ems.trabajo3.banco;

public class Debito extends Tarjeta{
    @Override
    public void retirar(double x) throws Exception {
        this.mCuentaAsociada.retirar("Retirada en cajero automático", x);
    }

    @Override
    public void ingresar(double x) throws Exception {
        this.mCuentaAsociada.ingresar("Ingreso en cajero automático", x);
    }

    @Override
    public void pagoEnEstablecimiento(String datos, double x) throws Exception {
        this.mCuentaAsociada.retirar("Compra en :" + datos, x);
    }

    @Override
    public double getSaldo() {
        return mCuentaAsociada.getSaldo();
    }
}
package etsisi.ems.trabajo3.banco;

import java.util.Date;
import java.util.Vector;
import java.time.LocalDate;
import java.time.ZoneId;

public class Credito {
    private Cuenta mCuentaAsociada;
    private double mCredito;
    private Vector<Movimiento> mMovimientos;
    private String mNumero, mTitular; // ### NO SE USA ###
    private LocalDate mFechaDeCaducidad; // ### NO SE USA ###
    private String mNombreEntidad; // ### NO SE USA ###
    private int mCCV; // ### NO SE USA ###
    private int mMarcaInternacional; //mastercard, maestro, visa ...
    private int mTipo; //oro platino clásica

    public Credito(String numero, String titular, LocalDate fechacaducidad, double credito, int marcainternacional,	String nombreentidad, int ccv) {
        mNumero = numero;
        mTitular = titular;
        mFechaDeCaducidad = fechacaducidad;
        mCredito = credito;
        mMovimientos = new Vector<Movimiento>();
        mMarcaInternacional = marcainternacional;
        mNombreEntidad = nombreentidad;
        mCCV = ccv;
    }
	
    public Credito(String numero, String titular, LocalDate fechacaducidad, int tipo, int marcainternacional, String nombreentidad, int ccv) {
        mNumero = numero;
        mTitular = titular;
        mFechaDeCaducidad = fechacaducidad;
        mTipo = tipo;
        mCredito = calcularCredito(mTipo);
        mMovimientos = new Vector<Movimiento>();
        mMarcaInternacional = marcainternacional;
        mNombreEntidad = nombreentidad;
        mCCV = ccv;
    }
	
    public double calcularCredito(int tipo) {
        double credito;
        
        switch (tipo) {
            case 1: //oro 	
                    credito = 1000;
                    break;		
            case 2: //platino
                    credito =  800;
                    break;		
            case 3: //clasica
                    credito =  600;
                    break;		
            default:
                    credito =  600;
                    break;		
        }
        return credito;
    }

    public void setCuenta(Cuenta c) {
        mCuentaAsociada = c;
    }

    public void retirar(double x) throws Exception {	
        double comisiontarifa;
        
        switch (mMarcaInternacional) {
            case 1: //mastercard
                comisiontarifa = 0.05;
                break;	
            case 2: //maestro
                comisiontarifa = 0.05;
                break;	
            case 3: //visa clásica
                comisiontarifa = 0.03;
                break;
            case 4: //visa electrón
                comisiontarifa = 0.02;
                break;
            default:
                comisiontarifa = 0.05;
                break;		
            }
		
            // Añadimos una comisión de un 5% o 3% o 2%, mínimo de 3 euros.
            double comision = (x * comisiontarifa < 3.0 ? 3 : x * comisiontarifa); 		
            
            if (x > getCreditoDisponible())
                throw new Exception("Crédito insuficiente");
            
            Movimiento m = new Movimiento();
            m.setConcepto("Retirada en cuenta asociada (cajero automático)");
            m.setImporte(x + comision);
            Date date = new Date();
            LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            m.setFecha(fecha);
            mMovimientos.addElement(m);
	}

    //traspaso tarjeta a cuenta
    public void ingresar(double x) throws Exception {
        // Movimiento m=new Movimiento();
        // m.setConcepto("Ingreso en cuenta asociada (cajero automático)");
        // m.setImporte(x);
        // mMovimientos.addElement(m);

        double comision = (x * 0.05 < 3.0 ? 3 : x * 0.05); // Añadimos una comisión de un 5%, mínimo de 3 euros.		
        
        if (x > getCreditoDisponible())
            throw new Exception("Crédito insuficiente");
        
        Movimiento m = new Movimiento();
        m.setConcepto("Traspaso desde tarjeta a cuenta");
        m.setImporte(x);
        Date date = new Date();
        LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        m.setFecha(fecha);
        mMovimientos.addElement(m);

        mCuentaAsociada.ingresar("Traspaso desde tarjeta a cuenta", x);
        mCuentaAsociada.retirar("Comision Traspaso desde tarjeta a cuenta", comision);
    }

    public void pagoEnEstablecimiento(String datos, double x) throws Exception {
        Movimiento m = new Movimiento();
        m.setConcepto("Compra a crédito en: " + datos);
        m.setImporte(x);
        Date date = new Date();
        LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        m.setFecha(fecha);
        mMovimientos.addElement(m);
    }

    public double getSaldo() {
        double r = 0.0;
        
        for (int i = 0; i < this.mMovimientos.size(); i++) {
            Movimiento m = (Movimiento) mMovimientos.elementAt(i);
            r += m.getImporte();
        }
        
        return r;
    }

    public double getCreditoDisponible() {
        return mCredito - getSaldo();
    }

    public void liquidar(int mes, int anyo) throws Exception {
        double r = 0.0;
        for (int i = 0; i < this.mMovimientos.size(); i++) {
            Movimiento m = (Movimiento) mMovimientos.elementAt(i);
            if (m.getFecha().getMonthValue() == mes && m.getFecha().getYear() == anyo && !m.isLiquidado())
                r += m.getImporte();
                m.setLiquidado(true);
        }
		
        if (r != 0) {
            Movimiento liq = new Movimiento();
            liq.setConcepto("Liquidación de operaciones tarj. crédito, " + (mes) + " de " + (anyo));
            liq.setImporte(-r);
            Date date = new Date();
            LocalDate fecha = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            liq.setFecha(fecha);
            mCuentaAsociada.addMovimiento(liq);			
        }
    }
	
    //liquidación parcial sobre el total de los gastos realizados con esa tarjeta durante el mes/año de liquidación que consiste en lo siguiente: 
    //los gastos totales, incluida una comisión de 12%, se dividen en 3 cuotas a pagar en los 3 meses siguientes 
    /**
     * Liquidación parcial sobre el total de los gastos realizados con esa tarjeta durante el mes/año de liquidación que consiste en lo siguiente: 
     * los gastos totales, incluida una comisión de 12%, se dividen en 3 cuotas a pagar en los 3 meses siguientes
     * 
     * 
     * @param mes mes del que sea desea liquidar los movimientos
     * @param anyo año del que se desea liquidar los movimientos
     */
    public void liquidarPlazos (int mes, int anyo){
        int numPlazos = 3;
        double r = 0.0;
        
        //Obtener los movimientos de un determinado mes/año y marcarlos cómo liquidados
        for (int i = 0; i < this.mMovimientos.size(); i++) {
            Movimiento m = (Movimiento) mMovimientos.elementAt(i);
            if (m.getFecha().getMonthValue() == mes && m.getFecha().getYear() == anyo && !m.isLiquidado())
                r += m.getImporte();
                m.setLiquidado(true);
        }
        
        // Si se han habido movimientos ese mes, aplicar el 12% de comisión y partir la luiquidación en los 3 siguientes meses
        if (r != 0) {
            r *= 1.12;
            r /= (double)numPlazos;
            
            for(int i=0;i<numPlazos;i++){
                Movimiento liq = new Movimiento();
                
                liq.setConcepto("Liquidación de operaciones tarj. crédito, " + (mes) + " de " + (anyo));
                liq.setImporte(-r);
                liq.setFecha(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusMonths(i));
                
                mCuentaAsociada.addMovimiento(liq);
            }			
        }
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
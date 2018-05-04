package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class DebitoTest extends TestCase {
    Debito debito;
    Cuenta cuenta;

    public DebitoTest(String sTestName) {
        super(sTestName);
    }

    @Before
    public void setUp() throws Exception {
        cuenta = new Cuenta("0001.0002.12.1234567890", "Fulano de Tal");
        cuenta.ingresar(1000.0);
        Date hoy = new Date();
        LocalDate fechacaducidad = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        fechacaducidad.plusYears(4); // Caduca en 4 años
        debito = new Debito("1234567890123456", "Fulano de Tal", fechacaducidad);
        debito.setCuenta(cuenta);
    }

    @After
    public void tearDown() throws Exception {
    
    }

    @Test
    public void testIngresar100() {
        try {
            double saldoAnteriorCuenta = cuenta.getSaldo();		
            debito.ingresar(100);
            double saldoActualCuenta = cuenta.getSaldo();
            assertTrue(debito.getSaldo() == cuenta.getSaldo());
            assertTrue(saldoAnteriorCuenta + 100 == saldoActualCuenta);			
        } catch (Exception e) {
            fail("No deberia haber fallado");
        }
    }


    @Test
    public void testRetirar1000() {
        try {
            double saldoAnteriorCuenta = cuenta.getSaldo();		
            debito.retirar(1000.0);
            double saldoActualCuenta = cuenta.getSaldo();
            assertTrue(debito.getSaldo() == cuenta.getSaldo());
            assertTrue(saldoAnteriorCuenta -1000 == saldoActualCuenta);			
        } catch (Exception e) {
            fail("No deberia haber fallado");
        }
    }

    @Test
    public void testpagoEnEstablecimiento100() {
        try {
            double saldoAnteriorCuenta = cuenta.getSaldo();		
            debito.pagoEnEstablecimiento("Mango", 100);
            double saldoActualCuenta = cuenta.getSaldo();
            assertTrue(debito.getSaldo() == cuenta.getSaldo());
            assertTrue(saldoAnteriorCuenta- 100 == saldoActualCuenta);
        } catch (Exception e) {
            fail("No deberia haber fallado");
        }
    }
}
package etsisi.ems.trabajo3.banco;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class CreditoTest extends TestCase {
	Cuenta cuenta;
	Credito tarjeta;

	public CreditoTest(String sTestName) {
		super(sTestName);
	}

	@Before
	public void setUp() throws Exception {
		cuenta = new Cuenta("0001.0002.12.1234567890", "Fulano de Tal");
		cuenta.ingresar(1000.0);
		Date hoy = new Date();
		LocalDate fechacaducidad = hoy.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		fechacaducidad.plusYears(4); // Caduca en 4 años
		tarjeta = new Credito("1234567890123456", "Fulano de Tal", fechacaducidad, 1000.0, 1, "bbva", 123); // 1000€ de crédito, tarjeta mastercard
		tarjeta.setCuenta(cuenta);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIngresar500() {
		try {
			double saldoAnteriorCuenta = cuenta.getSaldo();
			double saldoAnteriorTarjeta = tarjeta.getSaldo();
			double creditoAnterior = tarjeta.getCreditoDisponible();

			tarjeta.ingresar(500.0);

			double saldoActualCuenta = cuenta.getSaldo();
			double saldoActualTarjeta = tarjeta.getSaldo();
			double creditoActual = tarjeta.getCreditoDisponible();
			
			double comision = (500 * 0.05 < 3.0 ? 3 : 500 * 0.05);
			
			assertTrue(saldoActualCuenta == saldoAnteriorCuenta + 500 - comision); //comisión 25€
			assertTrue(saldoActualTarjeta == saldoAnteriorTarjeta + 500);
			assertTrue(creditoActual + 500 == creditoAnterior);
		} catch (Exception e) {	
			fail("Salta excepcion - No deberia haber fallado");
		}
	}


	@Test
	public void testRetirar1() {
		try {
			double saldoAnteriorCuenta = cuenta.getSaldo();			
			double creditoAnterior = tarjeta.getCreditoDisponible();
			tarjeta.retirar(1.0);
			double saldoActualCuenta = cuenta.getSaldo();
			double saldoActualTarjeta = tarjeta.getSaldo();
			double creditoActual = tarjeta.getCreditoDisponible();

			assertTrue(saldoActualCuenta == saldoAnteriorCuenta);			
			assertTrue(saldoActualTarjeta == 4); // 1 + 3€ de comisión mínima
			assertTrue(creditoActual == creditoAnterior - 4); // 1 + 3€ de comisión mínima
		} catch (Exception e) {
			fail("Salta excepcion - No deberia haber fallado");
		}
	}
	
	@Test
	public void testRetirar300() {
		try {
			double saldoAnteriorCuenta = cuenta.getSaldo();			
			double creditoAnterior = tarjeta.getCreditoDisponible();
			tarjeta.retirar(300.0);
			double saldoActualCuenta = cuenta.getSaldo();
			double saldoActualTarjeta = tarjeta.getSaldo();
			double creditoActual = tarjeta.getCreditoDisponible();

			assertTrue(saldoActualCuenta == saldoAnteriorCuenta);			
			assertTrue(saldoActualTarjeta == 315); // 5% de comisión de una tarjeta mastercard
			assertTrue(creditoActual == creditoAnterior - 315);
		} catch (Exception e) {
			fail("Salta excepcion - No deberia haber fallado");
		}
	}
	
	@Test
	public void testpagoEnEstablecimiento600() {
		try {
			double saldoAnteriorCuenta = cuenta.getSaldo();			
			double creditoAnterior = tarjeta.getCreditoDisponible();
			tarjeta.pagoEnEstablecimiento("Mango", 600);
			double saldoActualCuenta = cuenta.getSaldo();
			double saldoActualTarjeta = tarjeta.getSaldo();
			double creditoActual = tarjeta.getCreditoDisponible();

			assertTrue(saldoActualCuenta == saldoAnteriorCuenta);			
			assertTrue(saldoActualTarjeta == 600);
			assertTrue(creditoActual == creditoAnterior - 600);
		} catch (Exception e) {
			fail("Salta excepcion - No deberia haber fallado");
		}
	}


	@Test
	public void testLiquidar() {
		try {
			double saldoAnteriorCuenta = cuenta.getSaldo();
			tarjeta.pagoEnEstablecimiento("Zara", 120.0);
			tarjeta.pagoEnEstablecimiento("El Corte Inglés", 230.0);
			Date date = new Date();
			tarjeta.liquidar(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
			assertTrue(saldoAnteriorCuenta  - 350.0 == cuenta.getSaldo());
		} catch (Exception e) {
			fail("Salta excepcion - No deberia haber fallado");
		}
	}
	
	@Test
	public void testLiquidarBis() {
		try {
			double saldoAnteriorCuenta = cuenta.getSaldo();
			tarjeta.pagoEnEstablecimiento("Zara", 120.0);
			tarjeta.pagoEnEstablecimiento("El Corte Inglés", 230.0);
			Date date = new Date();
			tarjeta.liquidar(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
			tarjeta.liquidar(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
			assertTrue(saldoAnteriorCuenta  - 350.0 == cuenta.getSaldo());
		} catch (Exception e) {
			fail("Salta excepcion - No deberia haber fallado");
		}
	}

	public static void main(String args[]) {
		Result result = JUnitCore.runClasses(CreditoTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}

	}
}
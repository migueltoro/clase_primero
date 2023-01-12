package us.lsi.aeropuerto;

import static us.lsi.tools.StreamTools.toList;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GenDatos {
	
	public static void main(String[] args) {
		System.out.println(GenDatos.random());
		OcupacionVuelo ocp = GenDatos.random(Vuelos.of().get(0),2020);
		System.out.println(ocp);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		System.out.println(ocp.fecha().format(formatter));
	}
	
	public static Random rnd = new Random(System.nanoTime());

	public static Vuelo random() {
		Integer e = GenDatos.rnd.nextInt(Aerolineas.of().size());
		String codigo = Aerolineas.of().get(e).codigo();
		String numero = String.format("%04d",GenDatos.rnd.nextInt(1000));
		Integer ad = GenDatos.rnd.nextInt(Aeropuertos.of().size());
		String codeDestino = Aeropuertos.of().get(ad).codigo();
		Integer ao;
		do {
			ao = GenDatos.rnd.nextInt(Aeropuertos.of().size());
		} while (ao == ad);
		String codeOrigen = Aeropuertos.of().get(ao).codigo();
		Double precio = 1000*GenDatos.rnd.nextDouble();
		Integer numPlazas = GenDatos.rnd.nextInt(300);
		Duration duracion = Duration.of(GenDatos.rnd.nextInt(360), ChronoUnit.MINUTES);
		LocalTime hora = LocalTime.of(GenDatos.rnd.nextInt(24),GenDatos.rnd.nextInt(60));
		DayOfWeek diaSemana = DayOfWeek.of(1+GenDatos.rnd.nextInt(7));
		return new Vuelo(codigo,numero,codeDestino,codeOrigen,precio,numPlazas,duracion,hora,diaSemana);
	}
	
	public static Vuelos random(Integer numVuelos) {
		List<Vuelo> vuelos = toList(IntStream.range(0,numVuelos).boxed().map(e->random()));
		return new Vuelos(vuelos);
	}

	public static OcupacionVuelo random(Vuelo v, Integer anyo) {
		String codeVuelo = v.codigo();
		Integer np = v.numPlazas();
		LocalTime t = v.hora();
		DayOfWeek dw = v.diaSemana();
		LocalDate d = Stream.iterate(LocalDate.of(anyo,1,1),dt->dt.plus(1,ChronoUnit.DAYS))
				.filter(dt->dt.getDayOfWeek().equals(dw))
				.findFirst()
				.get();
		d = d.plus(7*GenDatos.rnd.nextInt(53),ChronoUnit.DAYS); //53 semanas en un a�o
		LocalDateTime fecha = LocalDateTime.of(d, t);
	    Integer numPasajeros = np>0?GenDatos.rnd.nextInt(np):0;
		return new OcupacionVuelo(codeVuelo,fecha,numPasajeros);
	}

	public static OcupacionesVuelos random(Integer numOcupaciones, Integer anyo) {
		Integer n = Vuelos.of().size();
		List<OcupacionVuelo> r = toList(
				IntStream.range(0, numOcupaciones).boxed()
				.map(e -> random(Vuelos.of().get(GenDatos.rnd.nextInt(n)), anyo)));
		OcupacionesVuelos.focupacionesVuelos =  new OcupacionesVuelos(r);
		return OcupacionesVuelos.focupacionesVuelos;
	}

}

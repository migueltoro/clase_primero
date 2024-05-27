package us.lsi.aeropuerto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import us.lsi.tools.Stream2;

public class PreguntasFuncional implements Preguntas {

	PreguntasFuncional() {
		super();
	}


	//1. Dada una cadena de caracteres s devuelve el n�mero total de pasajeros a
	// ciudades destino que tienen
	// como prefijo s (esto es, comienzan por s).

	public Integer numeroDepasajeros(String prefix) {
		IntStream st = OcupacionesVuelos.of().todas().stream()
				.filter(ocp -> ocp.vuelo().ciudadDestino().startsWith(prefix))
				.mapToInt(v -> v.numPasajeros());
		return 	st.sum();
	}
		

	//2.  Dado un conjunto de ciudades destino s y una fecha f devuelve cierto si
	// existe un vuelo en la fecha f con destino en s.

	public Boolean hayDestino(Set<String> destinos, LocalDate f) {
		Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream()
			.filter(ocp -> ocp.fecha().toLocalDate().equals(f));
		
		return st.anyMatch(ocp -> destinos.contains(ocp.vuelo().ciudadDestino()));
	}
	
	
	public Boolean hayDestino2(Set<String> destinos, LocalDate f) {
		Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream()
			.filter(ocp -> ocp.fecha().toLocalDate().equals(f))
			.filter(ocp -> destinos.contains(ocp.vuelo().ciudadDestino()));
		
		return st.findFirst().isPresent();
	}
		
		
	//3. Dada una fecha f devuelve el conjunto de ciudades destino diferentes de todos
	// los vuelos de fecha f

	public Set<String> destinosDiferentes(LocalDate f) {
		Stream<String> st = OcupacionesVuelos.of().todas().stream()
			.filter(ocp -> ocp.fecha().toLocalDate().equals(f))
			.map(ocp -> ocp.vuelo().ciudadDestino());
		
		return 	st.collect(Collectors.toSet());
	}
	
	public SortedSet<String> destinosDiferentes2(LocalDate f) {
		Stream<String> st = OcupacionesVuelos.of().todas().stream()
				.filter(ocp -> ocp.fecha().toLocalDate().equals(f))
				.map(ocp -> ocp.vuelo().ciudadDestino());
			
			return 	st.collect(Collectors.toCollection(()-> new TreeSet<>(Comparator.naturalOrder())));
	}
	
	public List<String> destinosDiferentes3(LocalDate f) {
		Stream<String> st = OcupacionesVuelos.of().todas().stream()
				.filter(ocp -> ocp.fecha().toLocalDate().equals(f))
				.map(ocp -> ocp.vuelo().ciudadDestino())
				.distinct()
				.sorted();
			
			return 	st.collect(Collectors.toList());
	}
		
		
	//4. Dado un anyo devuelve un SortedMap que relacione cada destino con el
	// total de pasajeros a ese destino en el a�o anyo

	public SortedMap<String, Integer> totalPasajerosADestino(Integer a) {
		Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream()
				.filter(ocp -> ocp.fecha().getYear() == a);
			
		return	st.collect(Collectors.groupingBy(ocp -> ocp.vuelo().ciudadDestino(),
					    () -> new TreeMap<String, Integer>(Comparator.reverseOrder()),
					    		Collectors.summingInt(ocp -> ocp.numPasajeros())));
	}

	//5. Dado un destino devuelve el c�digo de la aerolinea del primer vuelo con plazas libres a ese
	// destino

	public String primerVuelo(String destino) {
		Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream()
				.filter(ocp -> ocp.vuelo().ciudadDestino().equals(destino))
				.filter(ocp->ocp.vuelo().numPlazas() > ocp.numPasajeros())
				.filter(ocp -> ocp.fecha().isAfter(LocalDateTime.now()));
			
		return st.min(Comparator.comparing(OcupacionVuelo::fecha))
					.get()
					.vuelo()
					.codigoAerolinea();

	}

	//6. Devuelve para los vuelos con n asientos vacíos un Map que haga corresponder a cada ciudad
	// destino la media de los precios de los vuelos a ese destino.
		
		private Double preM(List<OcupacionVuelo> ls){
			return ls.stream().mapToDouble(ocp->ocp.vuelo().precio()).average().getAsDouble();
		}

		public Map<String, Double> precioMedio(Integer n ) {
			Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream()
					.filter(ocp -> ocp.vuelo().numPlazas()-ocp.numPasajeros() == n);
			
			return st.collect(Collectors.groupingBy(ocp -> ocp.vuelo().ciudadDestino(),
					Collectors.collectingAndThen(Collectors.toList(),g->preM(g))));
		}
		
		
		public Map<String, Double> precioMedio2(Integer n ) {
			Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream()
					.filter(ocp -> ocp.vuelo().numPlazas()-ocp.numPasajeros() == n);
			
			return Stream2.groupingList(st, ocp -> ocp.vuelo().ciudadDestino(), 
					x->x, g->preM(g));
		}

		//7. Devuelve un Map tal que dado un entero n haga corresponder
		// a cada fecha la lista de los n destinos con los vuelos de mayor duraci�n.
		
		private static Comparator<OcupacionVuelo> cmp = 
				Comparator.comparing((OcupacionVuelo ocp) -> 
				ocp.vuelo().duracion().getSeconds()).reversed();
		
		private List<String> mayorDuracion(List<OcupacionVuelo> ls,Integer n){
			Stream<String> st = ls.stream()
					.sorted(cmp)
					.limit(n)
					.map(ocp -> ocp.vuelo().ciudadDestino());
			
			return	st.toList();
		}

		public Map<LocalDate, List<String>> destinosConMayorDuracion(Integer n) {
			Stream<OcupacionVuelo> st =  OcupacionesVuelos.of().todas().stream();
			
			return st.collect(Collectors.groupingBy(oc -> oc.fecha().toLocalDate(),
					Collectors.collectingAndThen(Collectors.toList(),ls->mayorDuracion(ls,n))));
		}
		
		public Map<LocalDate, List<String>> destinosConMayorDuracion2(Integer n) {
			Stream<OcupacionVuelo> st =  OcupacionesVuelos.of().todas().stream();
			
			return Stream2.groupingList(st,oc -> oc.fecha().toLocalDate(),x->x,ls->mayorDuracion(ls,n));
		}

		//8. Dada una fecha f devuelve el precio medio de los vuelos con salida posterior
		// a f. Si no hubiera vuelos devuelve 0.0

		public Double precioMedio(LocalDateTime f) {
			DoubleStream st = OcupacionesVuelos.of().todas().stream()
					.filter(ocp -> ocp.fecha().isAfter(f))
//					.filter(p))
					.mapToDouble(ocp -> ocp.vuelo().precio());
			
			return	st.average().orElse(0.0);
		}
		
		

		//9. Devuelve un Map que haga corresponder a cada destino un conjunto con las
		// fechas de los vuelos a ese destino.

		public Map<String, Set<LocalDate>> fechasADestino() {
			Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream();
			
			return st.collect(Collectors.groupingBy(ocp -> ocp.vuelo().ciudadDestino(),
					Collectors.mapping(OcupacionVuelo::fechaSalida,Collectors.toSet())));
		}
		
		
		public Map<String, Set<LocalDate>> fechasADestino2() {
			Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream();
			
			return Stream2.groupingSet(st, ocp -> ocp.vuelo().ciudadDestino(), OcupacionVuelo::fechaSalida);
		}
		
		
		//10. Devuelve el destino con mayor n�mero de vuelos
		
		public String destinoConMasVuelos() {	
			Map<String,Integer> numVuelosDeDestino = Vuelos.of().todos().stream()
					 .collect(Collectors.groupingBy(Vuelo::codigoDestino,
							  Collectors.collectingAndThen(Collectors.counting(),Long::intValue)));
			return 	numVuelosDeDestino.keySet().stream()
					.max(Comparator.comparing(d->numVuelosDeDestino.get(d)))
					.get();	
		}
		
		public String destinoConMasVuelos2() {	
			Stream<Vuelo> st = Vuelos.of().todos().stream();
			Map<String,Integer> numVuelosDeDestino = Stream2.groupingSize(st,Vuelo::codigoDestino);
			
			return 	numVuelosDeDestino.keySet().stream()
					.max(Comparator.comparing(d->numVuelosDeDestino.get(d)))
					.get();	
		}
		
		
		//11. Dado un entero m devuelve un conjunto ordenado con las duraciones de todos los vuelos cuya duraci�n es mayor que m minutos.
		
		public SortedSet<Duration> duraciones(Integer m) {
			Stream<Duration> st = Vuelos.of().todos().stream()
					.map(Vuelo::duracion)
					.filter(d->d.getSeconds()/60. > m);

			return st.collect(Collectors.toCollection(()->new TreeSet<>()));
		}
	 
		
		//12. Dado un n�mero n devuelve un conjunto con los destinos de los vuelos que est�n entre los n que m�s duraci�n tienen.
		
		public Set<String> destinosMayorDuracion(Integer n) {
			Stream<String> st = Vuelos.of().todos().stream()
					.sorted(Comparator.comparing(Vuelo::duracion).reversed())
					.limit(n)
					.map(Vuelo::codigoDestino);
			
			return st.collect(Collectors.toSet());
		}
		
		//13. Dado un n�mero n devuelve un conjunto con los n destinos con m�s vuelos
		
		public Set<String> entreLosMasVuelos(Integer n) {			
			Map<String,Long> vuelosADestino = Vuelos.of().todos().stream()
					.collect(Collectors.groupingBy(Vuelo::codigoDestino, Collectors.counting()));
			
			return vuelosADestino.keySet().stream()
					.sorted(Comparator.comparing(d->vuelosADestino.get(d)).reversed())
					.limit(n)
					.collect(Collectors.toSet());
		}
		
		
		
		// 14. Dado un n�mero entero n devuelve una lista con los destinos que tienen m�s de n vuelos
		
		public List<String> masDeNVuelos(Integer n) {
			Map<String,Long> vuelosADestino = Vuelos.of().todos().stream()
					.collect(Collectors.groupingBy(Vuelo::codigoDestino,Collectors.counting()));
			
			return vuelosADestino.keySet().stream()
					.filter(d->vuelosADestino.get(d) > n)
					.collect(Collectors.toList());
		}
		
		
		// 15. Devuelve un Map que relaci�n cada destino con el porcentaje de los vuelos del total que van a ese destino.
		
		public Map<String,Double>  porcentajeADestino() {
			Integer n = Vuelos.of().size();
			
			return Vuelos.of().todos().stream().collect(Collectors.groupingBy(Vuelo::codigoDestino,
					Collectors.collectingAndThen(Collectors.toList(),g->(1.0*g.size())/n)));
		}
		
		public Map<String,Double>  porcentajeADestinoOcupacionesVuelos() {
			Integer n = OcupacionesVuelos.of().size();
			
			return OcupacionesVuelos.of().todas().stream()
					.map(ocp->ocp.vuelo())
					.collect(Collectors.groupingBy(Vuelo::codigoDestino,
					    Collectors.collectingAndThen(Collectors.toList(),g->(1.0*g.size())/n)));
		}
		
		// 16. Devuelve un Map que haga corresponder a cada ciudad destino el vuelo de m�s barato
		
		public Map<String,Vuelo> masBarato() {
			
			return Vuelos.of().todos().stream().collect(Collectors.groupingBy(Vuelo::ciudadDestino,
					Collectors.collectingAndThen(
							Collectors.reducing(BinaryOperator.minBy(Comparator.comparing(Vuelo::precio))),
							e->e.get())));
		}
		
		public Map<String,Vuelo> masBarato2() {
			
			return Vuelos.of().todos().stream()
					.collect(Collectors.groupingBy(Vuelo::ciudadDestino,
							Collectors.collectingAndThen(
									Collectors.minBy(Comparator.comparing(Vuelo::precio)),
										x->x.get())));
		}
	
		
		
		// 17. Devuelve un Map que haga corresponder a cada destino el n�mero de fechas
		// distintas en las que hay vuelos a ese destino.

		public Map<String, Integer> fechasDistintas() {
			Stream<OcupacionVuelo> st = OcupacionesVuelos.of().todas().stream();
	
			return st.collect(Collectors.groupingBy(ocp -> ocp.vuelo().ciudadDestino(),
					Collectors.mapping(OcupacionVuelo::fecha,
							Collectors.collectingAndThen(Collectors.toSet(),s->s.size()))));
		}

}

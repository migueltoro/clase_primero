package us.lsi.aeropuerto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import us.lsi.tools.FileTools;

public class OcupacionesVuelos {
	
	static OcupacionesVuelos focupacionesVuelos = null;
	
	public static OcupacionesVuelos get() {
		return OcupacionesVuelos.focupacionesVuelos;
	}

	public static OcupacionesVuelos leeFicheroOcupaciones(String fichero) {
		List<OcupacionVuelo> r = FileTools.streamFromFile(fichero)
				.map(x -> OcupacionVuelo.parse(x))
				.collect(Collectors.toList());
		return new OcupacionesVuelos(r);
	}

	private List<OcupacionVuelo> ocupaciones;

	public OcupacionesVuelos(List<OcupacionVuelo> ocupaciones) {
		super();
		this.ocupaciones = ocupaciones;
	}

	public Stream<OcupacionVuelo> stream() {
		return this.ocupaciones.stream();
	}
	
	public OcupacionVuelo get(Integer i) {
		return this.ocupaciones.get(i);
	}
	
	public Integer size() {
		return this.ocupaciones.size();
	}
	
}
